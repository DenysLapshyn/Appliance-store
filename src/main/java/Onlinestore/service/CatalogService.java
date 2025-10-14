package Onlinestore.service;

import Onlinestore.dto.GetItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.ItemMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CatalogService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final Environment environment;

    public String getCatalogPage(Model model) {
        List<Item> items = itemRepository.findAll();
        List<GetItemDTO> getItemDTOS = itemMapper.itemListToGetItemDTOList(items);
        model.addAttribute("items", getItemDTOS);
        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));

        return "catalog";
    }

    public String getCatalogPage(int page, int size, String sortDir, Model model) {
        // Ensure page number is not negative
        int currentPage = (page < 1) ? 0 : page - 1;

        // Determine sorting direction
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by("id").descending() :
                Sort.by("id").ascending();

        // Create pageable object
        Pageable pageable = PageRequest.of(currentPage, size, sort);

        // Fetch paginated and sorted items
        Page<Item> itemPage = itemRepository.findAll(pageable);

        // Convert to DTOs
        List<GetItemDTO> getItemDTOS = itemMapper.itemListToGetItemDTOList(itemPage.getContent());

        // Add data to model
        model.addAttribute("items", getItemDTOS);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));

        return "catalog";
    }

    public String getShowItemPage(Model model, long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            return "error";
        }
        Item item = itemRepository.findById(id).get();

        // if user logged in
        model.addAttribute("alreadyOrdered", false);
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

            // check if user already bought it
            for (Order order : user.getOrders()) {
                if (Objects.equals(order.getItem().getId(), item.getId())) {
                    model.addAttribute("alreadyOrdered", true);
                    break;
                }
            }
        }

        GetItemDTO getItemDTO = itemMapper.itemToGetItemDTO(item); // getItemDTO has null fields

        model.addAttribute("item", getItemDTO);

        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));
        model.addAttribute("imagesFolder", environment.getProperty("item.images.directory.on.server"));

        return "item";
    }

}
