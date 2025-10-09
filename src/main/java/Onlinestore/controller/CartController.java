package Onlinestore.controller;

import Onlinestore.dto.GetOrderDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.OrderMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.service.CartService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;

@Controller
@AllArgsConstructor
public class CartController
{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final Environment environment;
    private final OrderMapper orderMapper;
    private final CartService cartService;
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @GetMapping("/cart")
    public String getCartPage(Model model)
    {
        logger.info("User trying to get cart page");
        return cartService.getCartPage(model);
    }
    
    @PostMapping("/cart/add-order")
    public String addOrder(@RequestParam("item-id") int itemId)
    {
        logger.info("User or admin trying to add a new order");
        return cartService.addOrder(itemId);
    }
    
    @PostMapping("/cart/delete-order")
    public String deleteOrder(@RequestParam("order_id") int orderId)
    {
        logger.info("User or admin trying to delete an order");
        return cartService.deleteOrder(orderId);
    }
    
    @PostMapping("/cart/buy-orders")
    public String buyOrders(@RequestParam("amount_to_purchase") ArrayList<Integer> amountToPurchase)
    {
        logger.info("User or admin trying to buy their orders");
        return cartService.buyOrders(amountToPurchase);
    }
}
