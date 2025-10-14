package Onlinestore.service;

import Onlinestore.dto.GetOrderDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.OrderMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

@Service
@AllArgsConstructor
public class CartService {

    private final OrderMapper orderMapper;
    private final Environment environment;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public String getCartPage(Model model) {
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Set<Order> orders = user.getOrders();
        List<Order> orderList = new ArrayList<>(orders.stream().toList());
        orderList.sort(Comparator.comparing(Order::getId));

        Set<GetOrderDTO> getOrderDTOS = orderMapper.orderListToOrderDTOSet(orders);

        model.addAttribute("orders", getOrderDTOS);
        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));

        return "cart";
    }

    public String addOrder(long itemId) {
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Item item = itemRepository.getById(itemId);

        Order order = new Order(item, 1, user.getId());
        orderRepository.save(order);
        user.addOrder(order);
        userRepository.save(user);

        return "redirect:/catalog";
    }

    public String deleteOrder(long orderId) {
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        user.deleteOrderById(orderId);

        userRepository.save(user);
        orderRepository.deleteById(orderId);

        return "redirect:/cart";
    }

    public String buyOrders(ArrayList<Integer> amountToPurchase) {
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Set<Order> orders = user.getOrders();

        // check if amount to buy <= stored amount for each order
        Iterator<Order> orderIterator = orders.iterator();
        int currentAmountToPurchaseIndex = 0;
        while (orderIterator.hasNext()) {
            if (amountToPurchase.get(currentAmountToPurchaseIndex) > orderIterator.next().getItem().getAmount()) {
                return "redirect:/error";
            }
            currentAmountToPurchaseIndex++;
        }

        // update amount of stored items
        orderIterator = orders.iterator();
        currentAmountToPurchaseIndex = 0;
        while (orderIterator.hasNext()) {
            Item item = orderIterator.next().getItem();
            item.setAmount(item.getAmount() - amountToPurchase.get(currentAmountToPurchaseIndex));
            currentAmountToPurchaseIndex++;
            itemRepository.save(item);
        }

        orderRepository.deleteAll(orders);

        user.getOrders().clear();
        userRepository.save(user);

        return "redirect:/cart";
    }

}
