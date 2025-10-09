package Onlinestore;

import Onlinestore.dto.NewItemDTO;
import Onlinestore.dto.UpdateItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.User;
import Onlinestore.mapper.ItemMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private Environment environment;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MultipartFile logo;

    @TempDir
    Path tempDir;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(
                environment,
                itemMapper,
                itemRepository,
                userRepository,
                orderRepository
        );
    }

    @Test
    void saveLogoToFolder_shouldSaveLogoAndReturnLogoName() throws IOException {
        // Arrange
        long itemId = 123L;
        String expectedLogoName = "logo123";
        byte[] logoBytes = "test logo content".getBytes();
        String logosDirectory = tempDir.toString() + File.separator;

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(logo.getBytes()).thenReturn(logoBytes);

        // Act
        String actualLogoName = itemService.saveLogoToFolder(itemId, logo);

        // Assert
        assertEquals(expectedLogoName, actualLogoName);

        File savedFile = new File(logosDirectory + expectedLogoName);
        assertTrue(savedFile.exists());

        byte[] savedContent = Files.readAllBytes(savedFile.toPath());
        assertArrayEquals(logoBytes, savedContent);

        verify(environment).getProperty("item.logos.directory");
        verify(logo).getBytes();
    }

    @Test
    void saveLogoToFolder_shouldHandleEmptyLogo() throws IOException {
        // Arrange
        long itemId = 999L;
        String expectedLogoName = "logo999";
        byte[] emptyBytes = new byte[0];
        String logosDirectory = tempDir.toString() + File.separator;

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(logo.getBytes()).thenReturn(emptyBytes);

        // Act
        String actualLogoName = itemService.saveLogoToFolder(itemId, logo);

        // Assert
        assertEquals(expectedLogoName, actualLogoName);

        File savedFile = new File(logosDirectory + expectedLogoName);
        assertTrue(savedFile.exists());
        assertEquals(0, savedFile.length());
    }

    @Test
    void saveLogoToFolder_shouldGenerateUniqueNamesForDifferentItems() throws IOException {
        // Arrange
        String logosDirectory = tempDir.toString() + File.separator;
        byte[] logoBytes = "logo content".getBytes();

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(logo.getBytes()).thenReturn(logoBytes);

        // Act
        String logoName1 = itemService.saveLogoToFolder(1L, logo);
        String logoName2 = itemService.saveLogoToFolder(2L, logo);
        String logoName3 = itemService.saveLogoToFolder(100L, logo);

        // Assert
        assertEquals("logo1", logoName1);
        assertEquals("logo2", logoName2);
        assertEquals("logo100", logoName3);

        assertTrue(new File(logosDirectory + logoName1).exists());
        assertTrue(new File(logosDirectory + logoName2).exists());
        assertTrue(new File(logosDirectory + logoName3).exists());
    }

    @Test
    void saveImagesToFolder_shouldSaveSingleImageAndReturnImageName() throws IOException {
        // Arrange
        long itemId = 123L;
        String imagesDirectory = tempDir.toString() + File.separator;
        byte[] imageBytes = "test image content".getBytes();

        MultipartFile image1 = mock(MultipartFile.class);
        when(image1.getBytes()).thenReturn(imageBytes);
        MultipartFile[] images = {image1};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(1, imageNames.size());
        assertTrue(imageNames.contains("image123.1"));

        File savedFile = new File(imagesDirectory + "image123.1");
        assertTrue(savedFile.exists());
        assertArrayEquals(imageBytes, Files.readAllBytes(savedFile.toPath()));

        verify(environment).getProperty("item.images.directory");
        verify(image1).getBytes();
    }

    @Test
    void saveImagesToFolder_shouldSaveMultipleImagesWithSequentialNumbers() throws IOException {
        // Arrange
        long itemId = 456L;
        String imagesDirectory = tempDir.toString() + File.separator;

        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);
        MultipartFile image3 = mock(MultipartFile.class);

        when(image1.getBytes()).thenReturn("image 1 content".getBytes());
        when(image2.getBytes()).thenReturn("image 2 content".getBytes());
        when(image3.getBytes()).thenReturn("image 3 content".getBytes());

        MultipartFile[] images = {image1, image2, image3};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(3, imageNames.size());
        assertTrue(imageNames.contains("image456.1"));
        assertTrue(imageNames.contains("image456.2"));
        assertTrue(imageNames.contains("image456.3"));

        assertTrue(new File(imagesDirectory + "image456.1").exists());
        assertTrue(new File(imagesDirectory + "image456.2").exists());
        assertTrue(new File(imagesDirectory + "image456.3").exists());

        assertArrayEquals("image 1 content".getBytes(),
                Files.readAllBytes(new File(imagesDirectory + "image456.1").toPath()));
        assertArrayEquals("image 2 content".getBytes(),
                Files.readAllBytes(new File(imagesDirectory + "image456.2").toPath()));
        assertArrayEquals("image 3 content".getBytes(),
                Files.readAllBytes(new File(imagesDirectory + "image456.3").toPath()));
    }

    @Test
    void saveImagesToFolder_shouldHandleEmptyImages() throws IOException {
        // Arrange
        long itemId = 789L;
        String imagesDirectory = tempDir.toString() + File.separator;

        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);

        when(image1.getBytes()).thenReturn(new byte[0]);
        when(image2.getBytes()).thenReturn(new byte[0]);

        MultipartFile[] images = {image1, image2};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(2, imageNames.size());
        assertTrue(imageNames.contains("image789.1"));
        assertTrue(imageNames.contains("image789.2"));

        File savedFile1 = new File(imagesDirectory + "image789.1");
        File savedFile2 = new File(imagesDirectory + "image789.2");

        assertTrue(savedFile1.exists());
        assertTrue(savedFile2.exists());
        assertEquals(0, savedFile1.length());
        assertEquals(0, savedFile2.length());
    }

    @Test
    void saveImagesToFolder_shouldHandleLargeNumberOfImages() throws IOException {
        // Arrange
        long itemId = 999L;
        String imagesDirectory = tempDir.toString() + File.separator;
        int imageCount = 10;

        MultipartFile[] images = new MultipartFile[imageCount];
        for (int i = 0; i < imageCount; i++) {
            images[i] = mock(MultipartFile.class);
            when(images[i].getBytes()).thenReturn(("image " + i + " content").getBytes());
        }

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(imageCount, imageNames.size());

        for (int i = 1; i <= imageCount; i++) {
            String expectedName = "image999." + i;
            assertTrue(imageNames.contains(expectedName));
            assertTrue(new File(imagesDirectory + expectedName).exists());
        }
    }

    @Test
    void saveImagesToFolder_shouldGenerateUniqueNamesForDifferentItems() throws IOException {
        // Arrange
        String imagesDirectory = tempDir.toString() + File.separator;

        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);

        when(image1.getBytes()).thenReturn("content 1".getBytes());
        when(image2.getBytes()).thenReturn("content 2".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames1 = itemService.saveImagesToFolder(1L, new MultipartFile[]{image1});
        Set<String> imageNames2 = itemService.saveImagesToFolder(2L, new MultipartFile[]{image2});
        Set<String> imageNames3 = itemService.saveImagesToFolder(100L, new MultipartFile[]{image1});

        // Assert
        assertTrue(imageNames1.contains("image1.1"));
        assertTrue(imageNames2.contains("image2.1"));
        assertTrue(imageNames3.contains("image100.1"));

        assertTrue(new File(imagesDirectory + "image1.1").exists());
        assertTrue(new File(imagesDirectory + "image2.1").exists());
        assertTrue(new File(imagesDirectory + "image100.1").exists());
    }

    @Test
    void saveImagesToFolder_shouldHandleDifferentImageSizes() throws IOException {
        // Arrange
        long itemId = 555L;
        String imagesDirectory = tempDir.toString() + File.separator;

        MultipartFile smallImage = mock(MultipartFile.class);
        MultipartFile largeImage = mock(MultipartFile.class);

        byte[] smallBytes = "small".getBytes();
        byte[] largeBytes = new byte[1024 * 100]; // 100KB
        for (int i = 0; i < largeBytes.length; i++) {
            largeBytes[i] = (byte) (i % 256);
        }

        when(smallImage.getBytes()).thenReturn(smallBytes);
        when(largeImage.getBytes()).thenReturn(largeBytes);

        MultipartFile[] images = {smallImage, largeImage};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(2, imageNames.size());

        File smallFile = new File(imagesDirectory + "image555.1");
        File largeFile = new File(imagesDirectory + "image555.2");

        assertTrue(smallFile.exists());
        assertTrue(largeFile.exists());

        assertEquals(smallBytes.length, smallFile.length());
        assertEquals(largeBytes.length, largeFile.length());

        assertArrayEquals(smallBytes, Files.readAllBytes(smallFile.toPath()));
        assertArrayEquals(largeBytes, Files.readAllBytes(largeFile.toPath()));
    }

    @Test
    void saveImagesToFolder_shouldOverwriteExistingFiles() throws IOException {
        // Arrange
        long itemId = 777L;
        String imagesDirectory = tempDir.toString() + File.separator;
        String imageName = "image777.1";

        // Create existing file with old content
        File existingFile = new File(imagesDirectory + imageName);
        Files.write(existingFile.toPath(), "old content".getBytes());

        MultipartFile newImage = mock(MultipartFile.class);
        byte[] newContent = "new content".getBytes();
        when(newImage.getBytes()).thenReturn(newContent);

        MultipartFile[] images = {newImage};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(1, imageNames.size());
        assertTrue(imageNames.contains(imageName));

        // Verify old content was replaced
        assertArrayEquals(newContent, Files.readAllBytes(existingFile.toPath()));
    }

    @Test
    void saveImagesToFolder_shouldReturnEmptySetForEmptyArray() throws IOException {
        // Arrange
        long itemId = 111L;
        String imagesDirectory = tempDir.toString() + File.separator;
        MultipartFile[] images = {};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(0, imageNames.size());
        assertTrue(imageNames.isEmpty());
    }

    @Test
    void saveImagesToFolder_shouldMaintainOrderInFileNaming() throws IOException {
        // Arrange
        long itemId = 888L;
        String imagesDirectory = tempDir.toString() + File.separator;

        MultipartFile[] images = new MultipartFile[5];
        for (int i = 0; i < 5; i++) {
            images[i] = mock(MultipartFile.class);
            when(images[i].getBytes()).thenReturn(("Image at index " + i).getBytes());
        }

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        for (int i = 0; i < 5; i++) {
            String expectedName = "image888." + (i + 1);
            File imageFile = new File(imagesDirectory + expectedName);
            assertTrue(imageFile.exists());

            String content = new String(Files.readAllBytes(imageFile.toPath()));
            assertEquals("Image at index " + i, content);
        }
    }

    @Test
    void saveImagesToFolder_shouldHandleSpecialCharactersInContent() throws IOException {
        // Arrange
        long itemId = 333L;
        String imagesDirectory = tempDir.toString() + File.separator;

        MultipartFile image = mock(MultipartFile.class);
        byte[] specialBytes = new byte[]{0x00, (byte)0xFF, 0x7F, (byte)0x80, 0x01};
        when(image.getBytes()).thenReturn(specialBytes);

        MultipartFile[] images = {image};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        Set<String> imageNames = itemService.saveImagesToFolder(itemId, images);

        // Assert
        assertEquals(1, imageNames.size());
        File savedFile = new File(imagesDirectory + "image333.1");
        assertTrue(savedFile.exists());
        assertArrayEquals(specialBytes, Files.readAllBytes(savedFile.toPath()));
    }

    @Test
    void saveImagesToFolder_shouldCallGetBytesExactlyOncePerImage() throws IOException {
        // Arrange
        long itemId = 222L;
        String imagesDirectory = tempDir.toString() + File.separator;

        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);
        MultipartFile image3 = mock(MultipartFile.class);

        when(image1.getBytes()).thenReturn("content1".getBytes());
        when(image2.getBytes()).thenReturn("content2".getBytes());
        when(image3.getBytes()).thenReturn("content3".getBytes());

        MultipartFile[] images = {image1, image2, image3};

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.saveImagesToFolder(itemId, images);

        // Assert
        verify(image1, times(1)).getBytes();
        verify(image2, times(1)).getBytes();
        verify(image3, times(1)).getBytes();
        verify(environment, times(1)).getProperty("item.images.directory");
    }

    @Test
    void deleteLogoFromFolder_shouldDeleteExistingLogo() throws IOException {
        // Arrange
        long itemId = 123L;
        String logoName = "logo123";
        String logosDirectory = tempDir.toString() + File.separator;

        // Create logo file
        File logoFile = new File(logosDirectory + logoName);
        Files.write(logoFile.toPath(), "logo content".getBytes());
        assertTrue(logoFile.exists());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act
        itemService.deleteLogoFromFolder(itemId);

        // Assert
        assertFalse(logoFile.exists());
        verify(environment).getProperty("item.logos.directory");
    }

    @Test
    void deleteLogoFromFolder_shouldHandleNonExistentLogo() {
        // Arrange
        long itemId = 456L;
        String logoName = "logo456";
        String logosDirectory = tempDir.toString() + File.separator;

        File logoFile = new File(logosDirectory + logoName);
        assertFalse(logoFile.exists());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> itemService.deleteLogoFromFolder(itemId));
        verify(environment).getProperty("item.logos.directory");
    }

    @Test
    void deleteLogoFromFolder_shouldDeleteOnlySpecificItemLogo() throws IOException {
        // Arrange
        String logosDirectory = tempDir.toString() + File.separator;

        // Create multiple logo files
        File logo1 = new File(logosDirectory + "logo1");
        File logo2 = new File(logosDirectory + "logo2");
        File logo3 = new File(logosDirectory + "logo3");

        Files.write(logo1.toPath(), "logo 1 content".getBytes());
        Files.write(logo2.toPath(), "logo 2 content".getBytes());
        Files.write(logo3.toPath(), "logo 3 content".getBytes());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act
        itemService.deleteLogoFromFolder(2L);

        // Assert
        assertTrue(logo1.exists());
        assertFalse(logo2.exists());
        assertTrue(logo3.exists());
    }

    @Test
    void deleteLogoFromFolder_shouldHandleDifferentItemIds() throws IOException {
        // Arrange
        String logosDirectory = tempDir.toString() + File.separator;

        File logo100 = new File(logosDirectory + "logo100");
        File logo999 = new File(logosDirectory + "logo999");
        File logo1 = new File(logosDirectory + "logo1");

        Files.write(logo100.toPath(), "content".getBytes());
        Files.write(logo999.toPath(), "content".getBytes());
        Files.write(logo1.toPath(), "content".getBytes());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act
        itemService.deleteLogoFromFolder(100L);
        itemService.deleteLogoFromFolder(999L);

        // Assert
        assertFalse(logo100.exists());
        assertFalse(logo999.exists());
        assertTrue(logo1.exists());
    }

    @Test
    void deleteLogoFromFolder_shouldNotDeleteOtherFiles() throws IOException {
        // Arrange
        long itemId = 5L;
        String logosDirectory = tempDir.toString() + File.separator;

        File logoFile = new File(logosDirectory + "logo5");
        File otherFile1 = new File(logosDirectory + "logo50");
        File otherFile2 = new File(logosDirectory + "image5");
        File otherFile3 = new File(logosDirectory + "logo5.bak");

        Files.write(logoFile.toPath(), "logo".getBytes());
        Files.write(otherFile1.toPath(), "other1".getBytes());
        Files.write(otherFile2.toPath(), "other2".getBytes());
        Files.write(otherFile3.toPath(), "other3".getBytes());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act
        itemService.deleteLogoFromFolder(itemId);

        // Assert
        assertFalse(logoFile.exists());
        assertTrue(otherFile1.exists());
        assertTrue(otherFile2.exists());
        assertTrue(otherFile3.exists());
    }

    @Test
    void deleteLogoFromFolder_shouldWorkForMultipleConsecutiveDeletions() throws IOException {
        // Arrange
        String logosDirectory = tempDir.toString() + File.separator;

        File logo7 = new File(logosDirectory + "logo7");
        Files.write(logo7.toPath(), "content".getBytes());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act - delete multiple times
        itemService.deleteLogoFromFolder(7L);
        itemService.deleteLogoFromFolder(7L);
        itemService.deleteLogoFromFolder(7L);

        // Assert - should not throw exception
        assertFalse(logo7.exists());
    }

    @Test
    void deleteLogoFromFolder_shouldHandleLargeItemId() throws IOException {
        // Arrange
        long largeItemId = 999999999L;
        String logoName = "logo999999999";
        String logosDirectory = tempDir.toString() + File.separator;

        File logoFile = new File(logosDirectory + logoName);
        Files.write(logoFile.toPath(), "logo content".getBytes());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act
        itemService.deleteLogoFromFolder(largeItemId);

        // Assert
        assertFalse(logoFile.exists());
    }

    @Test
    void deleteLogoFromFolder_shouldDeleteFileRegardlessOfSize() throws IOException {
        // Arrange
        long itemId = 88L;
        String logosDirectory = tempDir.toString() + File.separator;

        File logoFile = new File(logosDirectory + "logo88");

        // Create large file
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        Files.write(logoFile.toPath(), largeContent);
        assertTrue(logoFile.exists());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act
        itemService.deleteLogoFromFolder(itemId);

        // Assert
        assertFalse(logoFile.exists());
    }

    @Test
    void deleteLogoFromFolder_shouldDeleteEmptyFile() throws IOException {
        // Arrange
        long itemId = 99L;
        String logosDirectory = tempDir.toString() + File.separator;

        File logoFile = new File(logosDirectory + "logo99");
        Files.write(logoFile.toPath(), new byte[0]);
        assertTrue(logoFile.exists());
        assertEquals(0, logoFile.length());

        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        // Act
        itemService.deleteLogoFromFolder(itemId);

        // Assert
        assertFalse(logoFile.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldDeleteAllImagesForSpecificItem() throws IOException {
        // Arrange
        long itemId = 123L;
        String imagesDirectory = tempDir.toString() + File.separator;

        File image1 = new File(imagesDirectory + "image123.1");
        File image2 = new File(imagesDirectory + "image123.2");
        File image3 = new File(imagesDirectory + "image123.3");

        Files.write(image1.toPath(), "image 1".getBytes());
        Files.write(image2.toPath(), "image 2".getBytes());
        Files.write(image3.toPath(), "image 3".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(itemId);

        // Assert
        assertFalse(image1.exists());
        assertFalse(image2.exists());
        assertFalse(image3.exists());
        verify(environment).getProperty("item.images.directory");
    }

    @Test
    void deleteImagesFromFolder_shouldNotDeleteImagesFromOtherItems() throws IOException {
        // Arrange
        String imagesDirectory = tempDir.toString() + File.separator;

        // Create images for different items
        File image1_1 = new File(imagesDirectory + "image1.1");
        File image1_2 = new File(imagesDirectory + "image1.2");
        File image2_1 = new File(imagesDirectory + "image2.1");
        File image3_1 = new File(imagesDirectory + "image3.1");

        Files.write(image1_1.toPath(), "content".getBytes());
        Files.write(image1_2.toPath(), "content".getBytes());
        Files.write(image2_1.toPath(), "content".getBytes());
        Files.write(image3_1.toPath(), "content".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(2L);

        // Assert
        assertTrue(image1_1.exists());
        assertTrue(image1_2.exists());
        assertFalse(image2_1.exists());
        assertTrue(image3_1.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldHandleNoImagesPresent() {
        // Arrange
        long itemId = 456L;
        String imagesDirectory = tempDir.toString() + File.separator;

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> itemService.deleteImagesFromFolder(itemId));
        verify(environment).getProperty("item.images.directory");
    }

    @Test
    void deleteImagesFromFolder_shouldDeleteOnlySingleImageIfOnlyOneExists() throws IOException {
        // Arrange
        long itemId = 789L;
        String imagesDirectory = tempDir.toString() + File.separator;

        File singleImage = new File(imagesDirectory + "image789.1");
        Files.write(singleImage.toPath(), "single image content".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(itemId);

        // Assert
        assertFalse(singleImage.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldDeleteManyImagesForItem() throws IOException {
        // Arrange
        long itemId = 999L;
        String imagesDirectory = tempDir.toString() + File.separator;
        int imageCount = 15;

        File[] images = new File[imageCount];
        for (int i = 1; i <= imageCount; i++) {
            images[i - 1] = new File(imagesDirectory + "image999." + i);
            Files.write(images[i - 1].toPath(), ("content " + i).getBytes());
        }

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(itemId);

        // Assert
        for (File image : images) {
            assertFalse(image.exists());
        }
    }

    @Test
    void deleteImagesFromFolder_shouldHandleNonSequentialImageNumbers() throws IOException {
        // Arrange
        long itemId = 111L;
        String imagesDirectory = tempDir.toString() + File.separator;

        // Create images with non-sequential numbering
        File image1 = new File(imagesDirectory + "image111.1");
        File image5 = new File(imagesDirectory + "image111.5");
        File image10 = new File(imagesDirectory + "image111.10");

        Files.write(image1.toPath(), "content".getBytes());
        Files.write(image5.toPath(), "content".getBytes());
        Files.write(image10.toPath(), "content".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(itemId);

        // Assert
        assertFalse(image1.exists());
        assertFalse(image5.exists());
        assertFalse(image10.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldHandleImagesWithDifferentSizes() throws IOException {
        // Arrange
        long itemId = 222L;
        String imagesDirectory = tempDir.toString() + File.separator;

        File smallImage = new File(imagesDirectory + "image222.1");
        File mediumImage = new File(imagesDirectory + "image222.2");
        File largeImage = new File(imagesDirectory + "image222.3");
        File emptyImage = new File(imagesDirectory + "image222.4");

        Files.write(smallImage.toPath(), "small".getBytes());
        Files.write(mediumImage.toPath(), new byte[1024 * 10]); // 10KB
        Files.write(largeImage.toPath(), new byte[1024 * 1024]); // 1MB
        Files.write(emptyImage.toPath(), new byte[0]);

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(itemId);

        // Assert
        assertFalse(smallImage.exists());
        assertFalse(mediumImage.exists());
        assertFalse(largeImage.exists());
        assertFalse(emptyImage.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldWorkForMultipleConsecutiveDeletions() throws IOException {
        // Arrange
        long itemId = 333L;
        String imagesDirectory = tempDir.toString() + File.separator;

        File image = new File(imagesDirectory + "image333.1");
        Files.write(image.toPath(), "content".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act - delete multiple times
        itemService.deleteImagesFromFolder(itemId);
        itemService.deleteImagesFromFolder(itemId);
        itemService.deleteImagesFromFolder(itemId);

        // Assert - should not throw exception
        assertFalse(image.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldHandleLargeItemId() throws IOException {
        // Arrange
        long largeItemId = 999999999L;
        String imagesDirectory = tempDir.toString() + File.separator;

        File image1 = new File(imagesDirectory + "image999999999.1");
        File image2 = new File(imagesDirectory + "image999999999.2");

        Files.write(image1.toPath(), "content1".getBytes());
        Files.write(image2.toPath(), "content2".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(largeItemId);

        // Assert
        assertFalse(image1.exists());
        assertFalse(image2.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldDeleteImagesWithVariousExtensions() throws IOException {
        // Arrange
        long itemId = 444L;
        String imagesDirectory = tempDir.toString() + File.separator;

        // Create images that start with "image444" regardless of what comes after
        File image1 = new File(imagesDirectory + "image444.1");
        File image2 = new File(imagesDirectory + "image444.2.jpg");
        File image3 = new File(imagesDirectory + "image444abc");

        Files.write(image1.toPath(), "content".getBytes());
        Files.write(image2.toPath(), "content".getBytes());
        Files.write(image3.toPath(), "content".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(itemId);

        // Assert
        assertFalse(image1.exists());
        assertFalse(image2.exists());
        assertFalse(image3.exists());
    }

    @Test
    void deleteImagesFromFolder_shouldHandleEmptyDirectory() {
        // Arrange
        long itemId = 555L;
        String imagesDirectory = tempDir.toString() + File.separator;

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act & Assert
        assertDoesNotThrow(() -> itemService.deleteImagesFromFolder(itemId));
    }


    @Test
    void deleteImagesFromFolder_shouldHandleDirectoryWithMixedFiles() throws IOException {
        // Arrange
        long itemId = 777L;
        String imagesDirectory = tempDir.toString() + File.separator;

        File targetImage1 = new File(imagesDirectory + "image777.1");
        File targetImage2 = new File(imagesDirectory + "image777.2");
        File logo = new File(imagesDirectory + "logo777");
        File otherImage = new File(imagesDirectory + "image888.1");
        File randomFile = new File(imagesDirectory + "readme.txt");

        Files.write(targetImage1.toPath(), "content".getBytes());
        Files.write(targetImage2.toPath(), "content".getBytes());
        Files.write(logo.toPath(), "logo content".getBytes());
        Files.write(otherImage.toPath(), "other image".getBytes());
        Files.write(randomFile.toPath(), "readme".getBytes());

        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteImagesFromFolder(itemId);

        // Assert
        assertFalse(targetImage1.exists());
        assertFalse(targetImage2.exists());
        assertTrue(logo.exists());
        assertTrue(otherImage.exists());
        assertTrue(randomFile.exists());
    }

    @Test
    void addItem_shouldReturnAddItemViewWhenBindingResultHasErrors() {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        MultipartFile logo = mock(MultipartFile.class);
        MultipartFile[] images = new MultipartFile[]{mock(MultipartFile.class)};
        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, logo, images, specNames, specValues);

        // Assert
        assertEquals("add-item", result);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void addItem_shouldRedirectToAdminWhenTooManyImagesUploaded() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        MultipartFile logo = mock(MultipartFile.class);
        MultipartFile[] images = new MultipartFile[6]; // Assuming max is 5
        for (int i = 0; i < images.length; i++) {
            images[i] = mock(MultipartFile.class);
        }

        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("5");

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        assertEquals("redirect:/admin", result);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void addItem_shouldSaveItemWithoutLogoAndImages() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(1L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        assertEquals("redirect:/admin", result);
        verify(itemRepository, times(2)).save(any(Item.class));
        verify(itemRepository).flush();
    }

    @Test
    void addItem_shouldSaveItemWithLogo() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(123L);

        String logosDirectory = tempDir.toString() + File.separator;

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(false);
        when(logo.getBytes()).thenReturn("logo content".getBytes());

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        assertEquals("redirect:/admin", result);
        verify(itemRepository, times(2)).save(any(Item.class));
        assertTrue(new File(logosDirectory + "logo123").exists());
    }

    @Test
    void addItem_shouldSaveItemWithLogoAndImages() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(789L);

        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(false);
        when(logo.getBytes()).thenReturn("logo".getBytes());

        MultipartFile image1 = mock(MultipartFile.class);
        when(image1.isEmpty()).thenReturn(false);
        when(image1.getBytes()).thenReturn("image".getBytes());
        MultipartFile[] images = {image1};

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        assertEquals("redirect:/admin", result);
        verify(itemRepository, times(2)).save(any(Item.class));
        assertTrue(new File(logosDirectory + "logo789").exists());
        assertTrue(new File(imagesDirectory + "image789.1").exists());
    }

    @Test
    void addItem_shouldPopulateSpecsWhenProvided() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        newItemDTO.setSpecs(new LinkedHashMap<>());
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(111L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        specNames.add("Color");
        specNames.add("Size");

        ArrayList<String> specValues = new ArrayList<>();
        specValues.add("Red");
        specValues.add("Large");

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, logo, images, specNames, specValues);

        // Assert
        assertEquals("redirect:/admin", result);
        assertEquals("Red", newItemDTO.getSpecs().get("Color"));
        assertEquals("Large", newItemDTO.getSpecs().get("Size"));
    }

    @Test
    void addItem_shouldHandleNullSpecNames() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(222L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        assertEquals("redirect:/admin", result);
        verify(itemRepository, times(2)).save(any(Item.class));
    }

    @Test
    void addItem_shouldHandleNullLogo() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(333L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        String result = itemService.addItem(newItemDTO, bindingResult, null, images, null, null);

        // Assert
        assertEquals("redirect:/admin", result);
        verify(itemRepository, times(2)).save(any(Item.class));
    }

    @Test
    void addItem_shouldCallRepositorySaveExactlyTwice() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(444L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        verify(itemRepository, times(2)).save(any(Item.class));
        verify(itemRepository, times(1)).flush();
    }

    @Test
    void addItem_shouldCallMapperToConvertDTO() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(555L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        verify(itemMapper).newItemDTOToItem(newItemDTO);
    }

    @Test
    void addItem_shouldSetLogoNameToNullWhenNoLogo() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = mock(Item.class);
        when(item.getId()).thenReturn(666L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        verify(item).setLogoName(null);
    }

    @Test
    void addItem_shouldSetImageNamesToNullWhenNoImages() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = mock(Item.class);
        when(item.getId()).thenReturn(777L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        // Act
        itemService.addItem(newItemDTO, bindingResult, logo, images, null, null);

        // Assert
        verify(item).setImageNames(null);
    }

    @Test
    void addItem_shouldPopulateMultipleSpecs() throws IOException {
        // Arrange
        NewItemDTO newItemDTO = new NewItemDTO();
        newItemDTO.setSpecs(new LinkedHashMap<>());
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item item = new Item();
        item.setId(999L);

        when(itemMapper.newItemDTOToItem(newItemDTO)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        specNames.add("Brand");
        specNames.add("Model");
        specNames.add("Year");
        specNames.add("Color");

        ArrayList<String> specValues = new ArrayList<>();
        specValues.add("Toyota");
        specValues.add("Camry");
        specValues.add("2023");
        specValues.add("Blue");

        // Act
        itemService.addItem(newItemDTO, bindingResult, logo, images, specNames, specValues);

        // Assert
        assertEquals(4, newItemDTO.getSpecs().size());
        assertEquals("Toyota", newItemDTO.getSpecs().get("Brand"));
        assertEquals("Camry", newItemDTO.getSpecs().get("Model"));
        assertEquals("2023", newItemDTO.getSpecs().get("Year"));
        assertEquals("Blue", newItemDTO.getSpecs().get("Color"));
    }

    @Test
    void getUpdateItemPage_shouldRetrieveItemAndReturnUpdateItemView() {
        // Arrange
        long itemId = 123L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(itemId);
        updateItemDTO.setName("Test Item");

        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        String result = itemService.getUpdateItemPage(itemId, model);

        // Assert
        assertEquals("update-item", result);
        verify(itemRepository).getById(itemId);
        verify(itemMapper).itemToUpdateItemDTO(item);
        verify(model).addAttribute("updateItemDTO", updateItemDTO);
    }

    @Test
    void getUpdateItemPage_shouldCallRepositoryWithCorrectId() {
        // Arrange
        long itemId = 456L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verify(itemRepository).getById(456L);
    }

    @Test
    void getUpdateItemPage_shouldCallMapperWithRetrievedItem() {
        // Arrange
        long itemId = 789L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Sample Item");
        item.setPrice(99.99);

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verify(itemMapper).itemToUpdateItemDTO(item);
    }

    @Test
    void getUpdateItemPage_shouldAddCorrectAttributeToModel() {
        // Arrange
        long itemId = 111L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(itemId);
        updateItemDTO.setName("Product");

        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verify(model).addAttribute("updateItemDTO", updateItemDTO);
        verify(model, times(1)).addAttribute(anyString(), any());
    }

    @Test
    void getUpdateItemPage_shouldWorkWithLargeItemId() {
        // Arrange
        long largeItemId = 999999999L;
        Item item = new Item();
        item.setId(largeItemId);

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(largeItemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        String result = itemService.getUpdateItemPage(largeItemId, model);

        // Assert
        assertEquals("update-item", result);
        verify(itemRepository).getById(largeItemId);
    }

    @Test
    void getUpdateItemPage_shouldWorkWithSmallItemId() {
        // Arrange
        long smallItemId = 1L;
        Item item = new Item();
        item.setId(smallItemId);

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(smallItemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        String result = itemService.getUpdateItemPage(smallItemId, model);

        // Assert
        assertEquals("update-item", result);
        verify(itemRepository).getById(smallItemId);
    }

    @Test
    void getUpdateItemPage_shouldHandleItemWithAllFieldsPopulated() {
        // Arrange
        long itemId = 222L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Complete Item");
        item.setPrice(149.99);
        item.setLogoName("logo222");
        item.setImageNames(Set.of("image222.1", "image222.2"));
        item.setSpecs(Map.of("Color", "Red", "Size", "Large"));

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(itemId);
        updateItemDTO.setName("Complete Item");

        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        String result = itemService.getUpdateItemPage(itemId, model);

        // Assert
        assertEquals("update-item", result);
        verify(itemRepository).getById(itemId);
        verify(itemMapper).itemToUpdateItemDTO(item);
        verify(model).addAttribute("updateItemDTO", updateItemDTO);
    }

    @Test
    void getUpdateItemPage_shouldHandleItemWithMinimalFields() {
        // Arrange
        long itemId = 333L;
        Item item = new Item();
        item.setId(itemId);

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        String result = itemService.getUpdateItemPage(itemId, model);

        // Assert
        assertEquals("update-item", result);
        verify(itemRepository).getById(itemId);
    }

    @Test
    void getUpdateItemPage_shouldReturnCorrectViewName() {
        // Arrange
        long itemId = 444L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        String result = itemService.getUpdateItemPage(itemId, model);

        // Assert
        assertEquals("update-item", result);
        assertNotEquals("add-item", result);
        assertNotEquals("update-item-form", result);
        assertNotEquals("redirect:/admin", result);
    }

    @Test
    void getUpdateItemPage_shouldNotInteractWithOtherRepositories() {
        // Arrange
        long itemId = 555L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verifyNoInteractions(userRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(environment);
    }

    @Test
    void getUpdateItemPage_shouldCallMethodsInCorrectOrder() {
        // Arrange
        long itemId = 666L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        InOrder inOrder = inOrder(itemRepository, itemMapper, model);
        inOrder.verify(itemRepository).getById(itemId);
        inOrder.verify(itemMapper).itemToUpdateItemDTO(item);
        inOrder.verify(model).addAttribute("updateItemDTO", updateItemDTO);
    }

    @Test
    void getUpdateItemPage_shouldPassExactDTOReturnedByMapper() {
        // Arrange
        long itemId = 777L;
        Item item = new Item();
        UpdateItemDTO expectedDTO = new UpdateItemDTO();
        expectedDTO.setId(itemId);
        expectedDTO.setName("Expected Item");

        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(expectedDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verify(model).addAttribute("updateItemDTO", expectedDTO);
    }

    @Test
    void getUpdateItemPage_shouldCallRepositoryGetByIdExactlyOnce() {
        // Arrange
        long itemId = 888L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verify(itemRepository, times(1)).getById(itemId);
    }

    @Test
    void getUpdateItemPage_shouldCallMapperExactlyOnce() {
        // Arrange
        long itemId = 999L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verify(itemMapper, times(1)).itemToUpdateItemDTO(item);
    }

    @Test
    void getUpdateItemPage_shouldAddAttributeWithCorrectKey() {
        // Arrange
        long itemId = 101L;
        Item item = new Item();
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        Model model = mock(Model.class);

        when(itemRepository.getById(itemId)).thenReturn(item);
        when(itemMapper.itemToUpdateItemDTO(item)).thenReturn(updateItemDTO);

        // Act
        itemService.getUpdateItemPage(itemId, model);

        // Assert
        verify(model).addAttribute(eq("updateItemDTO"), any(UpdateItemDTO.class));
    }

    @Test
    void getUpdateItemPage_shouldHandleConsecutiveCallsWithDifferentIds() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);
        Item item3 = new Item();
        item3.setId(3L);

        UpdateItemDTO dto1 = new UpdateItemDTO();
        UpdateItemDTO dto2 = new UpdateItemDTO();
        UpdateItemDTO dto3 = new UpdateItemDTO();

        Model model1 = mock(Model.class);
        Model model2 = mock(Model.class);
        Model model3 = mock(Model.class);

        when(itemRepository.getById(1L)).thenReturn(item1);
        when(itemRepository.getById(2L)).thenReturn(item2);
        when(itemRepository.getById(3L)).thenReturn(item3);
        when(itemMapper.itemToUpdateItemDTO(item1)).thenReturn(dto1);
        when(itemMapper.itemToUpdateItemDTO(item2)).thenReturn(dto2);
        when(itemMapper.itemToUpdateItemDTO(item3)).thenReturn(dto3);

        // Act
        String result1 = itemService.getUpdateItemPage(1L, model1);
        String result2 = itemService.getUpdateItemPage(2L, model2);
        String result3 = itemService.getUpdateItemPage(3L, model3);

        // Assert
        assertEquals("update-item", result1);
        assertEquals("update-item", result2);
        assertEquals("update-item", result3);

        verify(itemRepository).getById(1L);
        verify(itemRepository).getById(2L);
        verify(itemRepository).getById(3L);

        verify(model1).addAttribute("updateItemDTO", dto1);
        verify(model2).addAttribute("updateItemDTO", dto2);
        verify(model3).addAttribute("updateItemDTO", dto3);
    }

    @Test
    void updateItem_shouldReturnUpdateItemViewWhenBindingResultHasErrors() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        MultipartFile logo = mock(MultipartFile.class);
        MultipartFile[] images = new MultipartFile[]{mock(MultipartFile.class)};
        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("update-item", result);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateItem_shouldUpdateItemWithoutChangingLogoOrImages() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(123L);
        updateItemDTO.setName("Updated Item");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(123L);
        oldItem.setLogoName("logo123");
        oldItem.setImageNames(Set.of("image123.1", "image123.2"));

        Item updatedItem = new Item();
        updatedItem.setId(123L);
        updatedItem.setName("Updated Item");

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(123L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        specNames.add("Color");
        ArrayList<String> specValues = new ArrayList<>();
        specValues.add("Blue");

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        verify(itemRepository, times(2)).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateSpecs() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(333L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(333L);

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(333L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(333L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        specNames.add("Brand");
        specNames.add("Model");
        specNames.add("Year");

        ArrayList<String> specValues = new ArrayList<>();
        specValues.add("Toyota");
        specValues.add("Corolla");
        specValues.add("2024");

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        verify(updatedItem).setSpecs(argThat(specs ->
                specs.size() == 3 &&
                        "Toyota".equals(specs.get("Brand")) &&
                        "Corolla".equals(specs.get("Model")) &&
                        "2024".equals(specs.get("Year"))
        ));
    }

    @Test
    void updateItem_shouldReplaceBothLogoAndImages() throws IOException {
        // Arrange
        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(777L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(777L);
        oldItem.setLogoName("logo777");
        oldItem.setImageNames(Set.of("image777.1"));

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(777L);

        // Create old files
        File oldLogo = new File(logosDirectory + "logo777");
        File oldImage = new File(imagesDirectory + "image777.1");
        Files.write(oldLogo.toPath(), "old logo".getBytes());
        Files.write(oldImage.toPath(), "old image".getBytes());

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(777L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        MultipartFile newLogo = mock(MultipartFile.class);
        when(newLogo.isEmpty()).thenReturn(false);
        when(newLogo.getBytes()).thenReturn("new logo".getBytes());

        MultipartFile newImage = mock(MultipartFile.class);
        when(newImage.isEmpty()).thenReturn(false);
        when(newImage.getBytes()).thenReturn("new image".getBytes());
        MultipartFile[] images = {newImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, newLogo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        assertTrue(oldLogo.exists());
        assertTrue(new File(imagesDirectory + "image777.1").exists());
        assertArrayEquals("new logo".getBytes(), Files.readAllBytes(oldLogo.toPath()));
        assertArrayEquals("new image".getBytes(),
                Files.readAllBytes(new File(imagesDirectory + "image777.1").toPath()));
    }

    @Test
    void updateItem_shouldPreserveOldLogoAndImagesWhenNoChanges() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(888L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(888L);
        oldItem.setLogoName("logo888");
        oldItem.setImageNames(Set.of("image888.1", "image888.2"));

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(888L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(888L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        verify(updatedItem).setLogoName("logo888");
        verify(updatedItem).setImageNames(Set.of("image888.1", "image888.2"));
    }

    @Test
    void updateItem_shouldCallRepositorySaveMultipleTimes() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(999L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(999L);

        Item updatedItem = new Item();
        updatedItem.setId(999L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(999L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        verify(itemRepository, times(2)).save(any(Item.class));
    }

    @Test
    void updateItem_shouldHandleEmptySpecs() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(101L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(101L);

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(101L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(101L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        verify(updatedItem).setSpecs(argThat(Map::isEmpty));
    }

    @Test
    void updateItem_shouldSetSpecsInCorrectOrder() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(606L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(606L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(606L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        specNames.add("First");
        specNames.add("Second");
        specNames.add("Third");

        ArrayList<String> specValues = new ArrayList<>();
        specValues.add("Value1");
        specValues.add("Value2");
        specValues.add("Value3");

        // Act
        itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        verify(updatedItem).setSpecs(argThat(specs -> {
            if (!(specs instanceof LinkedHashMap)) return false;
            ArrayList<String> keys = new ArrayList<>(specs.keySet());
            return keys.get(0).equals("First") &&
                    keys.get(1).equals("Second") &&
                    keys.get(2).equals("Third");
        }));
    }

    @Test
    void updateItem_shouldHandleItemWithNullOldLogoName() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(707L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(707L);
        oldItem.setLogoName(null);

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(707L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(707L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        verify(updatedItem).setLogoName(null);
    }

    @Test
    void updateItem_shouldHandleItemWithNullOldImageNames() throws IOException {
        // Arrange
        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(808L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(808L);
        oldItem.setImageNames(null);

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(808L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(808L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        verify(updatedItem).setImageNames(null);
    }

    @Test
    void updateItem_shouldAddNewLogoWhenOldLogoWasNull() throws IOException {
        // Arrange
        String logosDirectory = tempDir.toString() + File.separator;

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(909L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(909L);
        oldItem.setLogoName(null);

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(909L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(909L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);

        MultipartFile newLogo = mock(MultipartFile.class);
        when(newLogo.isEmpty()).thenReturn(false);
        when(newLogo.getBytes()).thenReturn("new logo".getBytes());

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, newLogo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        assertTrue(new File(logosDirectory + "logo909").exists());
        verify(updatedItem).setLogoName("logo909");
    }

    @Test
    void updateItem_shouldAddNewImagesWhenOldImagesWereNull() throws IOException {
        // Arrange
        String imagesDirectory = tempDir.toString() + File.separator;

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(1010L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(1010L);
        oldItem.setImageNames(null);

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(1010L);

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(1010L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile newImage = mock(MultipartFile.class);
        when(newImage.isEmpty()).thenReturn(false);
        when(newImage.getBytes()).thenReturn("new image".getBytes());
        MultipartFile[] images = {newImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        assertTrue(new File(imagesDirectory + "image1010.1").exists());
        verify(updatedItem).setImageNames(any(Set.class));
    }

    @Test
    void updateItem_shouldNotDeleteLogoWhenDeleteFlagIsFalseAndNoNewLogo() throws IOException {
        // Arrange
        String logosDirectory = tempDir.toString() + File.separator;

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(1111L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(1111L);
        oldItem.setLogoName("logo1111");

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(1111L);

        // Create logo file
        File logoFile = new File(logosDirectory + "logo1111");
        Files.write(logoFile.toPath(), "logo content".getBytes());

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(1111L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        assertTrue(logoFile.exists());
        verify(updatedItem).setLogoName("logo1111");
    }

    @Test
    void updateItem_shouldNotDeleteImagesWhenDeleteFlagIsFalseAndNoNewImages() throws IOException {
        // Arrange
        String imagesDirectory = tempDir.toString() + File.separator;

        UpdateItemDTO updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setId(1212L);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Item oldItem = new Item();
        oldItem.setId(1212L);
        oldItem.setImageNames(Set.of("image1212.1"));

        Item updatedItem = mock(Item.class);
        when(updatedItem.getId()).thenReturn(1212L);

        // Create image file
        File imageFile = new File(imagesDirectory + "image1212.1");
        Files.write(imageFile.toPath(), "image content".getBytes());

        when(itemMapper.updateItemDTOToItem(updateItemDTO)).thenReturn(updatedItem);
        when(itemRepository.getById(1212L)).thenReturn(oldItem);
        when(environment.getProperty("item.images.upload.max.amount")).thenReturn("10");

        MultipartFile logo = mock(MultipartFile.class);
        when(logo.isEmpty()).thenReturn(true);

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        MultipartFile[] images = {emptyImage};

        ArrayList<String> specNames = new ArrayList<>();
        ArrayList<String> specValues = new ArrayList<>();

        // Act
        String result = itemService.updateItem(updateItemDTO, bindingResult, logo, images,
                false, false, specNames, specValues);

        // Assert
        assertEquals("redirect:/catalog", result);
        assertTrue(imageFile.exists());
        verify(updatedItem).setImageNames(Set.of("image1212.1"));
    }

    @Test
    void deleteItem_shouldDeleteBothLogoAndImages() throws IOException {
        // Arrange
        long itemId = 333L;
        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        Item item = new Item();
        item.setId(itemId);

        // Create logo and image files
        File logoFile = new File(logosDirectory + "logo333");
        File image1 = new File(imagesDirectory + "image333.1");
        File image2 = new File(imagesDirectory + "image333.2");
        Files.write(logoFile.toPath(), "logo".getBytes());
        Files.write(image1.toPath(), "image 1".getBytes());
        Files.write(image2.toPath(), "image 2".getBytes());

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        assertFalse(logoFile.exists());
        assertFalse(image1.exists());
        assertFalse(image2.exists());
    }

    @Test
    void deleteItem_shouldDeleteFilesAfterDeletingFromDatabase() throws IOException {
        // Arrange
        long itemId = 1010L;
        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        Item item = new Item();
        item.setId(itemId);

        File logoFile = new File(logosDirectory + "logo1010");
        File imageFile = new File(imagesDirectory + "image1010.1");
        Files.write(logoFile.toPath(), "logo".getBytes());
        Files.write(imageFile.toPath(), "image".getBytes());

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        InOrder inOrder = inOrder(itemRepository, environment);
        inOrder.verify(itemRepository).delete(item);
        inOrder.verify(environment).getProperty("item.logos.directory");
        inOrder.verify(environment).getProperty("item.images.directory");
    }

    @Test
    void deleteItem_shouldNotDeleteFilesFromOtherItems() throws IOException {
        // Arrange
        long itemId = 1313L;
        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        Item item = new Item();
        item.setId(itemId);

        // Create files for the item to delete
        File logoToDelete = new File(logosDirectory + "logo1313");
        File imageToDelete = new File(imagesDirectory + "image1313.1");
        Files.write(logoToDelete.toPath(), "logo".getBytes());
        Files.write(imageToDelete.toPath(), "image".getBytes());

        // Create files for other items
        File otherLogo1 = new File(logosDirectory + "logo1314");
        File otherLogo2 = new File(logosDirectory + "logo1");
        File otherImage1 = new File(imagesDirectory + "image1314.1");
        File otherImage2 = new File(imagesDirectory + "image1.1");
        Files.write(otherLogo1.toPath(), "other logo 1".getBytes());
        Files.write(otherLogo2.toPath(), "other logo 2".getBytes());
        Files.write(otherImage1.toPath(), "other image 1".getBytes());
        Files.write(otherImage2.toPath(), "other image 2".getBytes());

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        assertFalse(logoToDelete.exists());
        assertFalse(imageToDelete.exists());
        assertTrue(otherLogo1.exists());
        assertTrue(otherLogo2.exists());
        assertTrue(otherImage1.exists());
        assertTrue(otherImage2.exists());
    }

    @Test
    void deleteItem_shouldDeleteLargeFiles() throws IOException {
        // Arrange
        long itemId = 1919L;
        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        Item item = new Item();
        item.setId(itemId);

        // Create large files (1MB each)
        File largeLogo = new File(logosDirectory + "logo1919");
        File largeImage = new File(imagesDirectory + "image1919.1");
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        Files.write(largeLogo.toPath(), largeContent);
        Files.write(largeImage.toPath(), largeContent);

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        assertFalse(largeLogo.exists());
        assertFalse(largeImage.exists());
    }

    @Test
    void deleteItem_shouldDeleteEmptyFiles() throws IOException {
        // Arrange
        long itemId = 2020L;
        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        Item item = new Item();
        item.setId(itemId);

        // Create empty files
        File emptyLogo = new File(logosDirectory + "logo2020");
        File emptyImage = new File(imagesDirectory + "image2020.1");
        Files.write(emptyLogo.toPath(), new byte[0]);
        Files.write(emptyImage.toPath(), new byte[0]);

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        assertFalse(emptyLogo.exists());
        assertFalse(emptyImage.exists());
    }

    @Test
    void deleteItem_shouldNotInteractWithItemMapperOrEnvironmentPropertiesWhenNoFiles() throws IOException {
        // Arrange
        long itemId = 2323L;
        String logosDirectory = tempDir.toString() + File.separator;
        String imagesDirectory = tempDir.toString() + File.separator;

        Item item = new Item();
        item.setId(itemId);

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(environment.getProperty("item.logos.directory")).thenReturn(logosDirectory);
        when(environment.getProperty("item.images.directory")).thenReturn(imagesDirectory);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        verifyNoInteractions(itemMapper);
        verify(environment).getProperty("item.logos.directory");
        verify(environment).getProperty("item.images.directory");
    }

}
