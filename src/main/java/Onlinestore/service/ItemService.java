package Onlinestore.service;

import Onlinestore.dto.NewItemDTO;
import Onlinestore.dto.UpdateItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.User;
import Onlinestore.mapper.ItemMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
@AllArgsConstructor
public class ItemService {
    private final Environment environment;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public String saveLogoToFolder(long id, MultipartFile logo) {
        String logoName = "logo" + id;

        String logosDirectory = environment.getProperty("item.logos.directory");
        File logoFile = new File(logosDirectory + logoName);
        try (OutputStream os = new FileOutputStream(logoFile)) {
            os.write(logo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logoName;
    }

    public Set<String> saveImagesToFolder(long id, MultipartFile[] images) {
        Set<String> imageNames = new HashSet<>();

        String imageTemplateName = "image" + id + ".";
        String itemsDirectory = environment.getProperty("item.images.directory");

        for (int i = 0; i < images.length; i++) {
            String localImageName = imageTemplateName + (i + 1);
            String fullImageName = itemsDirectory + localImageName;
            File imageFile = new File(fullImageName);
            try (OutputStream os = new FileOutputStream(imageFile)) {
                os.write(images[i].getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageNames.add(localImageName);
        }

        return imageNames;
    }

    public void deleteLogoFromFolder(long id) {
        String logoName = "logo" + id;
        String logosDirectory = environment.getProperty("item.logos.directory");
        File logoFile = new File(logosDirectory + logoName);
        logoFile.delete();
    }

    public void deleteImagesFromFolder(long id) {
        String imagesDirectory = environment.getProperty("item.images.directory");
        File[] files = new File(imagesDirectory).listFiles();

        for (File file : files) {
            if (file.getName().startsWith("image" + id)) {
                file.delete();
            }
        }
    }


    public String getAddItemPage(Model model) {
        NewItemDTO newItemDTO = new NewItemDTO();
        model.addAttribute("newItemDTO", newItemDTO);
        return "add-item";
    }

    public String addItem(@ModelAttribute("newItemDTO") @Valid NewItemDTO newItemDTO,
                          BindingResult bindingResult,
                          @RequestParam(value = "logo", required = false) MultipartFile logo,
                          @RequestParam(value = "images", required = false) MultipartFile[] images,
                          @RequestParam(value = "spec-names", required = false) ArrayList<String> specNames,
                          @RequestParam(value = "spec-values", required = false) ArrayList<String> specValues) {
        if (bindingResult.hasErrors()) {
            return "add-item";
        }

        // ensure that admin uploads no more that item.images.upload.max.amount files
        if (images.length > Integer.parseInt(environment.getProperty("item.images.upload.max.amount"))) {
            return "redirect:/admin";
        }

        // fill item.specs
        if (specNames != null && specValues != null) {
            Map<String, String> specs = newItemDTO.getSpecs();
            for (int i = 0; i < specNames.size(); i++) {
                specs.put(specNames.get(i), specValues.get(i));
            }
        }

        Item item = itemMapper.newItemDTOToItem(newItemDTO);

        // save item to the database
        item = itemRepository.save(item);
        itemRepository.flush();
        long insertedId = item.getId();

        // (write logo image into file system) & (write logo name into item)
        if (logo != null && !logo.isEmpty()) {
            String logoName = saveLogoToFolder(insertedId, logo);
            item.setLogoName(logoName);
        } else {
            item.setLogoName(null);
        }

        // (write images into file system) & (write image names into item)
        if (!images[0].isEmpty()) {
            Set<String> imageNames = saveImagesToFolder(insertedId, images);
            item.setImageNames(imageNames);
        } else {
            item.setImageNames(null);
        }

        // write item into database
        itemRepository.save(item);

        return "redirect:/admin";
    }

    public String getUpdateItemPage(long id, Model model) {
        Item item = itemRepository.getById(id);
        UpdateItemDTO updateItemDTO = itemMapper.itemToUpdateItemDTO(item);

        model.addAttribute("updateItemDTO", updateItemDTO);

        return "update-item";
    }

    public String updateItem(@ModelAttribute("updateItemDTO") @Valid UpdateItemDTO updateItemDTO,
                             BindingResult bindingResult,
                             @RequestParam("logo") MultipartFile logo,
                             @RequestParam("images") MultipartFile[] images,
                             @RequestParam(value = "delete-previous-logo", required = false) boolean deletePreviousLogo,
                             @RequestParam(value = "delete-previous-images", required = false) boolean deletePreviousImages,
                             @RequestParam("spec-names") ArrayList<String> specNames,
                             @RequestParam("spec-values") ArrayList<String> specValues) {

        // ensure that admin uploads no more that item.images.upload.max.amount files
        if (images != null && images.length > Integer.parseInt(environment.getProperty("item.images.upload.max.amount"))) {
            return "error";
        }

        if (bindingResult.hasErrors()) {
            return "update-item";
        }

        Item item = itemMapper.updateItemDTOToItem(updateItemDTO);

        Item oldItem = itemRepository.getById(item.getId());
        String oldLogoName = oldItem.getLogoName();
        Set<String> oldImageNames = oldItem.getImageNames();
        item.setLogoName(oldLogoName);
        item.setImageNames(oldImageNames);

        itemRepository.save(item);

        // process logo
        if (deletePreviousLogo) {
            // delete from the database
            item.setLogoName(null);

            // delete from the folder
            deleteLogoFromFolder(item.getId());
        } else if (!logo.isEmpty()) {
            // delete from the database
            item.setLogoName(null);

            // delete from the folder
            deleteLogoFromFolder(item.getId());

            // save file to the folder
            String logoName = saveLogoToFolder(item.getId(), logo);

            // save file to the database
            item.setLogoName(logoName);
        }

        if (deletePreviousImages) {
            // delete from the database
            item.setImageNames(null);

            // delete from the folder
            deleteImagesFromFolder(item.getId());
        } else if (!images[0].isEmpty()) {
            // delete from the database
            item.setImageNames(null);

            // delete from the folder
            deleteImagesFromFolder(item.getId());

            // save files to the folder
            Set<String> imageNames = saveImagesToFolder(item.getId(), images);

            // save file to the database
            item.setImageNames(imageNames);
        }

        // fill item.specs
        Map<String, String> specs = new LinkedHashMap<>();
        for (int i = 0; i < specNames.size(); i++) {
            specs.put(specNames.get(i), specValues.get(i));
        }
        item.setSpecs(specs);
        itemRepository.save(item);

        return "redirect:/catalog";
    }

    public String deleteItem(long itemId) {
        // delete user's orders from database
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.deleteOrdersByItemId(itemId);
            userRepository.save(user);
        }

        // delete orders from database
        orderRepository.deleteOrdersByItem(itemRepository.getById(itemId));

        // delete item from database
        Item itemToDelete = itemRepository.getById(itemId);
        itemRepository.delete(itemToDelete);

        // delete logo and images from disc
        deleteLogoFromFolder(itemId);
        deleteImagesFromFolder(itemId);

        return "redirect:/catalog";
    }
}
