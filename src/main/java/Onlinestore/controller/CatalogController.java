package Onlinestore.controller;

import Onlinestore.service.CatalogService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class CatalogController {
    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
    private final CatalogService catalogService;

    @GetMapping("/catalog")
    public String getCatalogPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model
    ) {
        logger.info("Someone trying to receive catalog page: page={}, size={}, sortDir={}", page, size, sortDir);
        return catalogService.getCatalogPage(page, size, sortDir, model);
    }

    @GetMapping("/catalog/{id}")
    public String getShowItemPage(Model model, @PathVariable("id") int id) {
        logger.info("User trying to receive a page with detailed item information");
        return catalogService.getShowItemPage(model, id);
    }
}
