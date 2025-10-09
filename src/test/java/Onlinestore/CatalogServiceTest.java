package Onlinestore;

import Onlinestore.dto.GetItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.ItemMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.service.CatalogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private Environment environment;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private CatalogService catalogService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    private List<Item> mockItems;
    private List<GetItemDTO> mockItemDTOs;
    private Item mockItem;
    private GetItemDTO mockItemDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Mock SecurityContextHolder for getShowItemPage tests
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        // Setup mock items for getCatalogPage
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");

        mockItems = Arrays.asList(item1, item2);

        // Setup mock DTOs for getCatalogPage
        GetItemDTO dto1 = new GetItemDTO();
        dto1.setId(1L);
        dto1.setName("Item 1");

        GetItemDTO dto2 = new GetItemDTO();
        dto2.setId(2L);
        dto2.setName("Item 2");

        mockItemDTOs = Arrays.asList(dto1, dto2);

        // Setup mock item for getShowItemPage
        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Test Item");

        // Setup mock DTO for getShowItemPage
        mockItemDTO = new GetItemDTO();
        mockItemDTO.setId(1L);
        mockItemDTO.setName("Test Item");

        // Setup mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setOrders(new HashSet<>());
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    // ========== Tests for getCatalogPage() ==========

    @Test
    void getCatalogPage_ShouldReturnCatalogView_WhenItemsExist() {
        // Arrange
        String expectedLogoFolder = "/images/logos";
        when(itemRepository.findAll(Sort.by("id"))).thenReturn(mockItems);
        when(itemMapper.itemListToGetItemDTOList(mockItems)).thenReturn(mockItemDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn(expectedLogoFolder);

        // Act
        String viewName = catalogService.getCatalogPage(model);

        // Assert
        assertEquals("catalog", viewName);
        verify(itemRepository).findAll(Sort.by("id"));
        verify(itemMapper).itemListToGetItemDTOList(mockItems);
        verify(model).addAttribute("items", mockItemDTOs);
        verify(model).addAttribute("logoFolder", expectedLogoFolder);
        verify(environment).getProperty("item.logos.directory.on.server");
    }

    @Test
    void getCatalogPage_ShouldReturnCatalogView_WhenNoItemsExist() {
        // Arrange
        List<Item> emptyItems = Collections.emptyList();
        List<GetItemDTO> emptyDTOs = Collections.emptyList();
        String expectedLogoFolder = "/images/logos";

        when(itemRepository.findAll(Sort.by("id"))).thenReturn(emptyItems);
        when(itemMapper.itemListToGetItemDTOList(emptyItems)).thenReturn(emptyDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn(expectedLogoFolder);

        // Act
        String viewName = catalogService.getCatalogPage(model);

        // Assert
        assertEquals("catalog", viewName);
        verify(itemRepository).findAll(Sort.by("id"));
        verify(itemMapper).itemListToGetItemDTOList(emptyItems);
        verify(model).addAttribute("items", emptyDTOs);
        verify(model).addAttribute("logoFolder", expectedLogoFolder);
    }

    @Test
    void getCatalogPage_ShouldUseSortById() {
        // Arrange
        Sort expectedSort = Sort.by("id");
        when(itemRepository.findAll(any(Sort.class))).thenReturn(mockItems);
        when(itemMapper.itemListToGetItemDTOList(any())).thenReturn(mockItemDTOs);
        when(environment.getProperty(anyString())).thenReturn("/logos");

        // Act
        catalogService.getCatalogPage(model);

        // Assert
        verify(itemRepository).findAll(expectedSort);
    }

    @Test
    void getCatalogPage_ShouldHandleNullLogoFolder() {
        // Arrange
        when(itemRepository.findAll(Sort.by("id"))).thenReturn(mockItems);
        when(itemMapper.itemListToGetItemDTOList(mockItems)).thenReturn(mockItemDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn(null);

        // Act
        String viewName = catalogService.getCatalogPage(model);

        // Assert
        assertEquals("catalog", viewName);
        verify(model).addAttribute("logoFolder", null);
    }

    @Test
    void getCatalogPage_ShouldAddBothAttributesToModel() {
        // Arrange
        String logoFolder = "/logos";
        when(itemRepository.findAll(any(Sort.class))).thenReturn(mockItems);
        when(itemMapper.itemListToGetItemDTOList(any())).thenReturn(mockItemDTOs);
        when(environment.getProperty(anyString())).thenReturn(logoFolder);

        // Act
        catalogService.getCatalogPage(model);

        // Assert
        verify(model).addAttribute("items", mockItemDTOs);
        verify(model).addAttribute("logoFolder", logoFolder);
        verify(model, times(2)).addAttribute(anyString(), any());
    }

    @Test
    void getCatalogPage_ShouldCallMapperWithCorrectItemList() {
        // Arrange
        when(itemRepository.findAll(any(Sort.class))).thenReturn(mockItems);
        when(itemMapper.itemListToGetItemDTOList(mockItems)).thenReturn(mockItemDTOs);
        when(environment.getProperty(anyString())).thenReturn("/logos");

        // Act
        catalogService.getCatalogPage(model);

        // Assert
        verify(itemMapper).itemListToGetItemDTOList(mockItems);
    }

    @Test
    void getCatalogPage_ShouldRequestCorrectEnvironmentProperty() {
        // Arrange
        when(itemRepository.findAll(any(Sort.class))).thenReturn(mockItems);
        when(itemMapper.itemListToGetItemDTOList(any())).thenReturn(mockItemDTOs);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");

        // Act
        catalogService.getCatalogPage(model);

        // Assert
        verify(environment).getProperty("item.logos.directory.on.server");
    }

    @Test
    void getCatalogPage_ShouldAlwaysReturnCatalogString() {
        // Arrange
        when(itemRepository.findAll(any(Sort.class))).thenReturn(mockItems);
        when(itemMapper.itemListToGetItemDTOList(any())).thenReturn(mockItemDTOs);
        when(environment.getProperty(anyString())).thenReturn("/logos");

        // Act
        String result1 = catalogService.getCatalogPage(model);

        // Different scenario - empty list
        when(itemRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());
        when(itemMapper.itemListToGetItemDTOList(any())).thenReturn(Collections.emptyList());
        String result2 = catalogService.getCatalogPage(model);

        // Assert
        assertEquals("catalog", result1);
        assertEquals("catalog", result2);
    }

    // ========== Tests for getShowItemPage() ==========

    @Test
    void getShowItemPage_ShouldReturnErrorView_WhenItemNotFound() {
        // Arrange
        long itemId = 999L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("error", viewName);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(itemMapper);
        verifyNoInteractions(model);
    }

    @Test
    void getShowItemPage_ShouldReturnItemView_WhenItemExists_AndUserNotAuthenticated() {
        // Arrange
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");
        when(environment.getProperty("item.images.directory.on.server")).thenReturn("/images");
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("item", viewName);
        verify(itemRepository, times(2)).findById(itemId);
        verify(itemMapper).itemToGetItemDTO(mockItem);
        verify(model).addAttribute("alreadyOrdered", false);
        verify(model).addAttribute("item", mockItemDTO);
        verify(model).addAttribute("logoFolder", "/logos");
        verify(model).addAttribute("imagesFolder", "/images");
    }

    @Test
    void getShowItemPage_ShouldReturnItemView_WhenItemExists_AndUserIsAnonymous() {
        // Arrange
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");
        when(environment.getProperty("item.images.directory.on.server")).thenReturn("/images");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("anonymousUser");

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("item", viewName);
        verify(model).addAttribute("alreadyOrdered", false);
        verify(model).addAttribute("item", mockItemDTO);
        verify(model).addAttribute("logoFolder", "/logos");
        verify(model).addAttribute("imagesFolder", "/images");
    }

    @Test
    void getShowItemPage_ShouldSetAlreadyOrderedFalse_WhenUserHasNoOrders() {
        // Arrange
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty(anyString())).thenReturn("/path");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUser()).thenReturn(mockUser);

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("item", viewName);
        verify(model).addAttribute("alreadyOrdered", false);
    }

    @Test
    void getShowItemPage_ShouldSetAlreadyOrderedFalse_WhenUserOrdersDifferentItems() {
        // Arrange
        long itemId = 1L;

        Item differentItem = new Item();
        differentItem.setId(2L);

        Order order1 = new Order();
        order1.setItem(differentItem);

        Order order2 = new Order();
        Item anotherItem = new Item();
        anotherItem.setId(3L);
        order2.setItem(anotherItem);

        mockUser.setOrders(new HashSet<>(Arrays.asList(order1, order2)));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty(anyString())).thenReturn("/path");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUser()).thenReturn(mockUser);

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("item", viewName);
        verify(model).addAttribute("alreadyOrdered", false);
    }

    @Test
    void getShowItemPage_ShouldSetAlreadyOrderedTrue_WhenUserAlreadyOrderedThisItem() {
        // Arrange
        long itemId = 1L;

        Order order = new Order();
        order.setItem(mockItem);

        mockUser.setOrders(new HashSet<>(Collections.singleton(order)));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty(anyString())).thenReturn("/path");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUser()).thenReturn(mockUser);

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("item", viewName);
        verify(model).addAttribute("alreadyOrdered", true);
    }

    @Test
    void getShowItemPage_ShouldSetAlreadyOrderedTrue_WhenUserOrderedItemAmongMultipleOrders() {
        // Arrange
        long itemId = 1L;

        Item differentItem1 = new Item();
        differentItem1.setId(2L);

        Item differentItem2 = new Item();
        differentItem2.setId(3L);

        Order order1 = new Order();
        order1.setItem(differentItem1);

        Order order2 = new Order();
        order2.setItem(mockItem); // This one matches

        Order order3 = new Order();
        order3.setItem(differentItem2);

        mockUser.setOrders(new HashSet<>(Arrays.asList(order1, order2, order3)));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty(anyString())).thenReturn("/path");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUser()).thenReturn(mockUser);

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("item", viewName);
        verify(model).addAttribute("alreadyOrdered", true);
    }

    @Test
    void getShowItemPage_ShouldAddAllAttributesToModel_WhenSuccessful() {
        // Arrange
        long itemId = 1L;
        String logoFolder = "/test/logos";
        String imagesFolder = "/test/images";

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn(logoFolder);
        when(environment.getProperty("item.images.directory.on.server")).thenReturn(imagesFolder);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        catalogService.getShowItemPage(model, itemId);

        // Assert
        verify(model).addAttribute("alreadyOrdered", false);
        verify(model).addAttribute("item", mockItemDTO);
        verify(model).addAttribute("logoFolder", logoFolder);
        verify(model).addAttribute("imagesFolder", imagesFolder);
        verify(model, times(4)).addAttribute(anyString(), any());
    }

    @Test
    void getShowItemPage_ShouldCallRepositoryTwice_WhenItemExists() {
        // Arrange
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty(anyString())).thenReturn("/path");
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        catalogService.getShowItemPage(model, itemId);

        // Assert
        verify(itemRepository, times(2)).findById(itemId);
    }

    @Test
    void getShowItemPage_ShouldRequestCorrectEnvironmentProperties() {
        // Arrange
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn("/logos");
        when(environment.getProperty("item.images.directory.on.server")).thenReturn("/images");
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        catalogService.getShowItemPage(model, itemId);

        // Assert
        verify(environment).getProperty("item.logos.directory.on.server");
        verify(environment).getProperty("item.images.directory.on.server");
    }

    @Test
    void getShowItemPage_ShouldHandleNullEnvironmentProperties() {
        // Arrange
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemMapper.itemToGetItemDTO(mockItem)).thenReturn(mockItemDTO);
        when(environment.getProperty("item.logos.directory.on.server")).thenReturn(null);
        when(environment.getProperty("item.images.directory.on.server")).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        String viewName = catalogService.getShowItemPage(model, itemId);

        // Assert
        assertEquals("item", viewName);
        verify(model).addAttribute("logoFolder", null);
        verify(model).addAttribute("imagesFolder", null);
    }
}
