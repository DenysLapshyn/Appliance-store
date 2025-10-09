package Onlinestore.controller;

import Onlinestore.dto.NewItemDTO;
import Onlinestore.dto.UpdateItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.User;
import Onlinestore.mapper.ItemMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.service.ItemService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Controller
@AllArgsConstructor
public class AdminController
{
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final Environment environment;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/admin")
    public String getAdminPage()
    {
        return "admin";
    }
    
    @GetMapping("/admin/add-item")
    public String getAddItemPage(Model model)
    {
        logger.info("Received request to get the page to add new item");
        return itemService.getAddItemPage(model);
    }
    
    @PostMapping("/admin/add-item")
    public String addItem(@ModelAttribute("newItemDTO") @Valid NewItemDTO newItemDTO,
                             BindingResult bindingResult,
                             @RequestParam(value = "logo", required = false) MultipartFile logo,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             @RequestParam(value = "spec-names", required = false) ArrayList<String> specNames,
                             @RequestParam(value = "spec-values", required = false) ArrayList<String> specValues)
    {
        logger.info("Admin trying to create a new item");
        return itemService.addItem(newItemDTO, bindingResult, logo, images, specNames, specValues);
    }
    
    @GetMapping("/admin/edit-or-delete-items")
    public String getEditOrDeletePage()
    {
        logger.info("Received request to get edit or delete items page");
        return "redirect:/catalog";
    }
    
    @GetMapping("/admin/update-item/{id}")
    public String getUpdateItemPage(@PathVariable("id") int id, Model model)
    {
        logger.info("Received request to get update item page");
        return itemService.getUpdateItemPage(id, model);
    }
    
    @PostMapping("/admin/update-item")
    public String updateItem(@ModelAttribute("item") @Valid UpdateItemDTO updateItemDTO,
                             BindingResult bindingResult,
                             @RequestParam("logo") MultipartFile logo,
                             @RequestParam("images") MultipartFile[] images,
                             @RequestParam(value = "delete-previous-logo", required = false) boolean deletePreviousLogo,
                             @RequestParam(value = "delete-previous-images", required = false) boolean deletePreviousImages,
                             @RequestParam("spec-names") ArrayList<String> specNames,
                             @RequestParam("spec-values") ArrayList<String> specValues)
    {
        logger.info("Received request to update item");
        return itemService.updateItem(updateItemDTO, bindingResult, logo, images, deletePreviousLogo, deletePreviousImages, specNames, specValues);
    }
    
    @PostMapping("/admin/delete-item")
    public String deleteItem(@RequestParam("id") int itemId)
    {
        logger.info("Received request to delete item");
        return itemService.deleteItem(itemId);
    }
}
