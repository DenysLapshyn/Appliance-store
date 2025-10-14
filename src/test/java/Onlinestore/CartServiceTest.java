package Onlinestore;

import Onlinestore.dto.GetOrderDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.OrderMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserDetailsImpl;
import Onlinestore.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private Environment environment;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetailsImpl;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Set<Order> testOrders;
    private Set<GetOrderDTO> testOrderDTOs;

    @BeforeEach
    void setUp() {
        // Set up SecurityContext
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);

        // Set up test user
        testUser = new User();
        testUser.setId(1L);
        when(userDetailsImpl.getUser()).thenReturn(testUser);

        // Set up test orders
        testOrders = new HashSet<>();
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        testOrders.add(order1);
        testOrders.add(order2);
        testUser.setOrders(testOrders);

        // Set up test DTOs
        testOrderDTOs = new HashSet<>();
        GetOrderDTO dto1 = new GetOrderDTO();
        GetOrderDTO dto2 = new GetOrderDTO();
        testOrderDTOs.add(dto1);
        testOrderDTOs.add(dto2);
    }

    @Test
    void getCartPage_shouldReturnCartViewName() {
        // Arrange
        when(orderMapper.orderListToOrderDTOSet(any())).thenReturn(testOrderDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");

        // Act
        String viewName = cartService.getCartPage(model);

        // Assert
        assertEquals("cart", viewName);
    }

    @Test
    void getCartPage_shouldRetrieveAuthenticatedUser() {
        // Arrange
        when(orderMapper.orderListToOrderDTOSet(any())).thenReturn(testOrderDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");

        // Act
        cartService.getCartPage(model);

        // Assert
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
        verify(userDetailsImpl).getUser();
    }

    @Test
    void getCartPage_shouldAddOrdersToModel() {
        // Arrange
        when(orderMapper.orderListToOrderDTOSet(testOrders)).thenReturn(testOrderDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");

        // Act
        cartService.getCartPage(model);

        // Assert
        verify(model).addAttribute(eq("orders"), eq(testOrderDTOs));
    }

    @Test
    void getCartPage_shouldAddLogoFolderToModel() {
        // Arrange
        String logoPath = "/images/logos";
        when(orderMapper.orderListToOrderDTOSet(any())).thenReturn(testOrderDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn(logoPath);

        // Act
        cartService.getCartPage(model);

        // Assert
        verify(model).addAttribute(eq("logoFolder"), eq(logoPath));
    }

    @Test
    void getCartPage_shouldMapOrdersToOrderDTOs() {
        // Arrange
        when(orderMapper.orderListToOrderDTOSet(testOrders)).thenReturn(testOrderDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");

        // Act
        cartService.getCartPage(model);

        // Assert
        verify(orderMapper).orderListToOrderDTOSet(testOrders);
    }

    @Test
    void getCartPage_shouldHandleEmptyOrders() {
        // Arrange
        testUser.setOrders(new HashSet<>());
        Set<GetOrderDTO> emptyDTOs = new HashSet<>();
        when(orderMapper.orderListToOrderDTOSet(any())).thenReturn(emptyDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");

        // Act
        String viewName = cartService.getCartPage(model);

        // Assert
        assertEquals("cart", viewName);
        verify(model).addAttribute(eq("orders"), eq(emptyDTOs));
    }

    @Test
    void getCartPage_shouldFetchLogoDirectoryFromEnvironment() {
        // Arrange
        when(orderMapper.orderListToOrderDTOSet(any())).thenReturn(testOrderDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/custom/path");

        // Act
        cartService.getCartPage(model);

        // Assert
        verify(environment).getProperty("item.logos.directory.on.server");
    }

    @Test
    void getCartPage_shouldHandleMultipleOrders() {
        // Arrange
        Set<Order> largeOrderSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setId((long) i);
            largeOrderSet.add(order);
        }
        testUser.setOrders(largeOrderSet);

        when(orderMapper.orderListToOrderDTOSet(largeOrderSet)).thenReturn(testOrderDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");

        // Act
        String viewName = cartService.getCartPage(model);

        // Assert
        assertEquals("cart", viewName);
        verify(orderMapper).orderListToOrderDTOSet(largeOrderSet);
    }

    @Test
    void addOrder_shouldReturnRedirectToCatalog() {
        // Arrange
        long itemId = 1L;
        Item testItem = new Item();
        testItem.setId(itemId);

        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String result = cartService.addOrder(itemId);

        // Assert
        assertEquals("redirect:/catalog", result);
    }

    @Test
    void addOrder_shouldRetrieveAuthenticatedUser() {
        // Arrange
        long itemId = 1L;
        Item testItem = new Item();
        testItem.setId(itemId);

        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId);

        // Assert
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
        verify(userDetailsImpl).getUser();
    }

    @Test
    void addOrder_shouldRetrieveItemFromRepository() {
        // Arrange
        long itemId = 5L;
        Item testItem = new Item();
        testItem.setId(itemId);

        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId);

        // Assert
        verify(itemRepository).getById(itemId);
    }

    @Test
    void addOrder_shouldCreateOrderWithCorrectParameters() {
        // Arrange
        long itemId = 1L;
        long userId = 10L;
        testUser.setId(userId);

        Item testItem = new Item();
        testItem.setId(itemId);
        testItem.setName("Test Item");

        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            assertEquals(testItem, savedOrder.getItem());
            assertEquals(1, savedOrder.getAmount());
            assertEquals(userId, savedOrder.getUserId());
            return savedOrder;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId);

        // Assert
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addOrder_shouldSaveOrderToRepository() {
        // Arrange
        long itemId = 1L;
        Item testItem = new Item();
        testItem.setId(itemId);

        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId);

        // Assert
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addOrder_shouldAddOrderToUser() {
        // Arrange
        long itemId = 1L;
        Item testItem = new Item();
        testItem.setId(itemId);

        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId);

        // Assert
        verify(spyUser).addOrder(any(Order.class));
    }

    @Test
    void addOrder_shouldSaveUpdatedUser() {
        // Arrange
        long itemId = 1L;
        Item testItem = new Item();
        testItem.setId(itemId);

        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId);

        // Assert
        verify(userRepository).save(testUser);
    }

    @Test
    void addOrder_shouldCreateOrderWithQuantityOne() {
        // Arrange
        long itemId = 1L;
        Item testItem = new Item();
        testItem.setId(itemId);

        when(itemRepository.getById(itemId)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            assertEquals(1, savedOrder.getAmount());
            return savedOrder;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId);

        // Assert
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addOrder_shouldHandleDifferentItemIds() {
        // Arrange
        long itemId1 = 100L;
        long itemId2 = 200L;

        Item testItem1 = new Item();
        testItem1.setId(itemId1);
        Item testItem2 = new Item();
        testItem2.setId(itemId2);

        when(itemRepository.getById(itemId1)).thenReturn(testItem1);
        when(itemRepository.getById(itemId2)).thenReturn(testItem2);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addOrder(itemId1);
        cartService.addOrder(itemId2);

        // Assert
        verify(itemRepository).getById(itemId1);
        verify(itemRepository).getById(itemId2);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(userRepository, times(2)).save(testUser);
    }

    @Test
    void deleteOrder_shouldReturnRedirectToCart() {
        // Arrange
        long orderId = 1L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        String result = cartService.deleteOrder(orderId);

        // Assert
        assertEquals("redirect:/cart", result);
    }

    @Test
    void deleteOrder_shouldRetrieveAuthenticatedUser() {
        // Arrange
        long orderId = 1L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        cartService.deleteOrder(orderId);

        // Assert
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
        verify(userDetailsImpl).getUser();
    }

    @Test
    void deleteOrder_shouldDeleteOrderFromUser() {
        // Arrange
        long orderId = 5L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        cartService.deleteOrder(orderId);

        // Assert
        verify(spyUser).deleteOrderById(orderId);
    }

    @Test
    void deleteOrder_shouldSaveUpdatedUser() {
        // Arrange
        long orderId = 1L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        cartService.deleteOrder(orderId);

        // Assert
        verify(userRepository).save(spyUser);
    }

    @Test
    void deleteOrder_shouldDeleteOrderFromRepository() {
        // Arrange
        long orderId = 10L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        cartService.deleteOrder(orderId);

        // Assert
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrder_shouldDeleteOrderInCorrectSequence() {
        // Arrange
        long orderId = 1L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        cartService.deleteOrder(orderId);

        // Assert
        // Verify the order: 1. Delete from user, 2. Save user, 3. Delete from repository
        var inOrder = inOrder(spyUser, userRepository, orderRepository);
        inOrder.verify(spyUser).deleteOrderById(orderId);
        inOrder.verify(userRepository).save(spyUser);
        inOrder.verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrder_shouldHandleDifferentOrderIds() {
        // Arrange
        long orderId1 = 1L;
        long orderId2 = 2L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(anyLong());

        // Act
        cartService.deleteOrder(orderId1);
        cartService.deleteOrder(orderId2);

        // Assert
        verify(spyUser).deleteOrderById(orderId1);
        verify(spyUser).deleteOrderById(orderId2);
        verify(orderRepository).deleteById(orderId1);
        verify(orderRepository).deleteById(orderId2);
        verify(userRepository, times(2)).save(spyUser);
    }

    @Test
    void deleteOrder_shouldWorkWithZeroOrderId() {
        // Arrange
        long orderId = 0L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        String result = cartService.deleteOrder(orderId);

        // Assert
        assertEquals("redirect:/cart", result);
        verify(spyUser).deleteOrderById(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrder_shouldWorkWithLargeOrderId() {
        // Arrange
        long orderId = 999999L;
        User spyUser = spy(testUser);
        when(userDetailsImpl.getUser()).thenReturn(spyUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        String result = cartService.deleteOrder(orderId);

        // Assert
        assertEquals("redirect:/cart", result);
        verify(spyUser).deleteOrderById(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void buyOrders_shouldReturnRedirectToCart_whenPurchaseSuccessful() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setAmount(5);

        Order order1 = new Order(item1, 1, testUser.getId());
        Order order2 = new Order(item2, 1, testUser.getId());

        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        orders.add(order2);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(3);
        amountToPurchase.add(2);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        String result = cartService.buyOrders(amountToPurchase);

        // Assert
        assertEquals("redirect:/cart", result);
    }

    @Test
    void buyOrders_shouldReturnRedirectToError_whenPurchaseAmountExceedsStock() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(5);

        Order order1 = new Order(item1, 1, testUser.getId());

        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(10); // More than available stock

        // Act
        String result = cartService.buyOrders(amountToPurchase);

        // Assert
        assertEquals("redirect:/error", result);
        verify(itemRepository, never()).save(any(Item.class));
        verify(orderRepository, never()).deleteAll(any());
    }

    @Test
    void buyOrders_shouldRetrieveAuthenticatedUser() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Order order1 = new Order(item1, 1, testUser.getId());
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(5);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        cartService.buyOrders(amountToPurchase);

        // Assert
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
        verify(userDetailsImpl).getUser();
    }

    @Test
    void buyOrders_shouldUpdateItemAmounts() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setAmount(8);

        Order order1 = new Order(item1, 1, testUser.getId());
        Order order2 = new Order(item2, 1, testUser.getId());

        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        orders.add(order2);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(3);
        amountToPurchase.add(5);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        cartService.buyOrders(amountToPurchase);

        // Assert
        verify(itemRepository, times(2)).save(any(Item.class));
    }

    @Test
    void buyOrders_shouldDecrementItemStockByPurchaseAmount() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Order order1 = new Order(item1, 1, testUser.getId());
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(7);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            assertEquals(3, savedItem.getAmount()); // 10 - 7 = 3
            return savedItem;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        cartService.buyOrders(amountToPurchase);

        // Assert
        verify(itemRepository).save(item1);
    }

    @Test
    void buyOrders_shouldDeleteAllOrders() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Order order1 = new Order(item1, 1, testUser.getId());
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(5);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        cartService.buyOrders(amountToPurchase);

        // Assert
        verify(orderRepository).deleteAll(orders);
    }

    @Test
    void buyOrders_shouldSaveUpdatedUser() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Order order1 = new Order(item1, 1, testUser.getId());
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(5);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        cartService.buyOrders(amountToPurchase);

        // Assert
        verify(userRepository).save(testUser);
    }

    @Test
    void buyOrders_shouldHandleMultipleOrders() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(20);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setAmount(15);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setAmount(10);

        Order order1 = new Order(item1, 1, testUser.getId());
        Order order2 = new Order(item2, 1, testUser.getId());
        Order order3 = new Order(item3, 1, testUser.getId());

        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(5);
        amountToPurchase.add(10);
        amountToPurchase.add(3);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        String result = cartService.buyOrders(amountToPurchase);

        // Assert
        assertEquals("redirect:/cart", result);
        verify(itemRepository, times(3)).save(any(Item.class));
        verify(orderRepository).deleteAll(orders);
        verify(userRepository).save(testUser);
    }

    @Test
    void buyOrders_shouldReturnError_whenAnyItemHasInsufficientStock() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setAmount(3); // Insufficient stock

        Order order1 = new Order(item1, 1, testUser.getId());
        Order order2 = new Order(item2, 1, testUser.getId());

        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        orders.add(order2);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(5);
        amountToPurchase.add(5); // More than available

        // Act
        String result = cartService.buyOrders(amountToPurchase);

        // Assert
        assertEquals("redirect:/error", result);
        verify(itemRepository, never()).save(any(Item.class));
        verify(orderRepository, never()).deleteAll(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void buyOrders_shouldHandleExactStockAmount() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(5);

        Order order1 = new Order(item1, 1, testUser.getId());
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(5); // Exactly the available amount

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            assertEquals(0, savedItem.getAmount());
            return savedItem;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        String result = cartService.buyOrders(amountToPurchase);

        // Assert
        assertEquals("redirect:/cart", result);
        verify(itemRepository).save(item1);
    }

    @Test
    void buyOrders_shouldExecuteInCorrectOrder() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setAmount(10);

        Order order1 = new Order(item1, 1, testUser.getId());
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        testUser.setOrders(orders);

        ArrayList<Integer> amountToPurchase = new ArrayList<>();
        amountToPurchase.add(5);

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderRepository).deleteAll(any());

        // Act
        cartService.buyOrders(amountToPurchase);

        // Assert
        // Verify order: 1. Save items, 2. Delete orders, 3. Clear user orders, 4. Save user
        var inOrder = inOrder(itemRepository, orderRepository, userRepository);
        inOrder.verify(itemRepository).save(any(Item.class));
        inOrder.verify(orderRepository).deleteAll(orders);
        inOrder.verify(userRepository).save(testUser);
    }

}