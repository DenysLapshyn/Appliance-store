package Onlinestore.controller;

import Onlinestore.dto.GetItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.ItemMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.service.CatalogService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.*;

@Controller
@AllArgsConstructor
public class CatalogController
{
    private final ItemRepository itemRepository;
    private final Environment environment;
    private final ItemMapper itemMapper;
    private final CatalogService catalogService;
    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @GetMapping("/catalog")
    public String getCatalogPage(Model model)
    {
        logger.info("Someone trying to receive catalog page");
        return catalogService.getCatalogPage(model);
    }
    
    @GetMapping("/catalog/{id}")
    public String getShowItemPage(Model model, @PathVariable("id") int id)
    {
        logger.info("User trying to receive a page with detailed item information");
        return catalogService.getShowItemPage(model, id);
    }
}
