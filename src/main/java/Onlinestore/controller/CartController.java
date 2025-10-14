package Onlinestore.controller;

import Onlinestore.service.CartService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@AllArgsConstructor
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    @GetMapping("/cart")
    public String getCartPage(Model model) {
        logger.info("User trying to get cart page");
        return cartService.getCartPage(model);
    }

    @PostMapping("/cart/add-order")
    public String addOrder(@RequestParam("item-id") int itemId) {
        logger.info("User or admin trying to add a new order");
        return cartService.addOrder(itemId);
    }

    @PostMapping("/cart/delete-order")
    public String deleteOrder(@RequestParam("order_id") int orderId) {
        logger.info("User or admin trying to delete an order");
        return cartService.deleteOrder(orderId);
    }

    @PostMapping("/cart/buy-orders")
    public String buyOrders(@RequestParam("amount_to_purchase") ArrayList<Integer> amountToPurchase) {
        logger.info("User or admin trying to buy their orders");
        return cartService.buyOrders(amountToPurchase);
    }
}
