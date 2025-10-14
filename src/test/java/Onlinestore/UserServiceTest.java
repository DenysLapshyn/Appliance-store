package Onlinestore;

import Onlinestore.dto.GetUserDTO;
import Onlinestore.dto.UpdatePasswordDTO;
import Onlinestore.dto.UserRegistrationDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.UserMapper;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.RoleNames;
import Onlinestore.security.UserDetailsImpl;
import Onlinestore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetailsImpl;

    @Mock
    private BindingResult bindingResult;

    private User user;
    private GetUserDTO getUserDTO;
    private GetUserDTO getUserDTO2;
    private UserRegistrationDTO userRegistrationDTO;
    private User currentUser;
    private User mappedUser;
    private UpdatePasswordDTO updatePasswordDTO;

    @BeforeEach
    void setUp() {
        userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("john.doe@example.com");
        userRegistrationDTO.setTelephoneNumber("+1234567890");
        userRegistrationDTO.setPassword("password123");
        userRegistrationDTO.setRepeatedPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
        user.setTelephoneNumber("+1234567890");
        user.setCountry("USA");
        user.setAddress("123 Main St");
        user.setRoleNames(RoleNames.ROLE_USER);

        getUserDTO = new GetUserDTO();
        getUserDTO.setName("John");
        getUserDTO.setSurname("Doe");
        getUserDTO.setEmail("john.doe@example.com");
        getUserDTO.setTelephoneNumber("+1234567890");
        getUserDTO.setCountry("USA");
        getUserDTO.setAddress("123 Main St");

        getUserDTO2 = new GetUserDTO();
        getUserDTO2.setName("Jane");
        getUserDTO2.setSurname("Smith");
        getUserDTO2.setEmail("jane.smith@example.com");
        getUserDTO2.setTelephoneNumber("+9876543210");
        getUserDTO2.setCountry("Canada");
        getUserDTO2.setAddress("456 Oak Ave");

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("John");
        currentUser.setSurname("Doe");
        currentUser.setEmail("john.doe@example.com");
        currentUser.setTelephoneNumber("+1234567890");
        currentUser.setCountry("USA");
        currentUser.setAddress("123 Main St");
        currentUser.setPassword("encodedPassword");
        currentUser.setRoleNames(RoleNames.ROLE_USER);

        mappedUser = new User();
        mappedUser.setName("Jane");
        mappedUser.setSurname("Smith");
        mappedUser.setEmail("jane.smith@example.com");
        mappedUser.setTelephoneNumber("+9876543210");
        mappedUser.setCountry("Canada");
        mappedUser.setAddress("456 Oak Ave");

        updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setPassword("newPassword123");
        updatePasswordDTO.setRepeatedPassword("newPassword123");
    }

    // Tests for isEmailUnique method
    @Test
    void isEmailUnique_WhenEmailDoesNotExist_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Act
        boolean result = userService.isEmailUnique(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
    }

    @Test
    void isEmailUnique_WhenEmailExists_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act
        boolean result = userService.isEmailUnique(user);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
    }

    @Test
    void isEmailUnique_WithNullEmail_ShouldCheckRepository() {
        // Arrange
        user.setEmail(null);
        when(userRepository.existsByEmail(null)).thenReturn(false);

        // Act
        boolean result = userService.isEmailUnique(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(null);
    }

    @Test
    void isEmailUnique_WithEmptyEmail_ShouldCheckRepository() {
        // Arrange
        user.setEmail("");
        when(userRepository.existsByEmail("")).thenReturn(false);

        // Act
        boolean result = userService.isEmailUnique(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("");
    }

    // Tests for isTelephoneNumberUnique method
    @Test
    void isTelephoneNumberUnique_WhenTelephoneNumberDoesNotExist_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByTelephoneNumber(user.getTelephoneNumber())).thenReturn(false);

        // Act
        boolean result = userService.isTelephoneNumberUnique(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByTelephoneNumber(user.getTelephoneNumber());
    }

    @Test
    void isTelephoneNumberUnique_WhenTelephoneNumberExists_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByTelephoneNumber(user.getTelephoneNumber())).thenReturn(true);

        // Act
        boolean result = userService.isTelephoneNumberUnique(user);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByTelephoneNumber(user.getTelephoneNumber());
    }

    @Test
    void isTelephoneNumberUnique_WithNullTelephoneNumber_ShouldCheckRepository() {
        // Arrange
        user.setTelephoneNumber(null);
        when(userRepository.existsByTelephoneNumber(null)).thenReturn(false);

        // Act
        boolean result = userService.isTelephoneNumberUnique(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByTelephoneNumber(null);
    }

    @Test
    void isTelephoneNumberUnique_WithEmptyTelephoneNumber_ShouldCheckRepository() {
        // Arrange
        user.setTelephoneNumber("");
        when(userRepository.existsByTelephoneNumber("")).thenReturn(false);

        // Act
        boolean result = userService.isTelephoneNumberUnique(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByTelephoneNumber("");
    }

    @Test
    void isTelephoneNumberUnique_WithDifferentFormats_ShouldCheckRepository() {
        // Arrange
        String phoneNumber = "123-456-7890";
        user.setTelephoneNumber(phoneNumber);
        when(userRepository.existsByTelephoneNumber(phoneNumber)).thenReturn(false);

        // Act
        boolean result = userService.isTelephoneNumberUnique(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByTelephoneNumber(phoneNumber);
    }

    @Test
    void registerUser_WhenRepeatedPasswordIsNull_ShouldNotCheckPasswordMatch() {
        // Arrange
        user.setRepeatedPassword(null);
        when(userMapper.userRegistrationDTOToUser(userRegistrationDTO)).thenReturn(user);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByTelephoneNumber(user.getTelephoneNumber())).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        // Act
        String result = userService.registerUser(userRegistrationDTO, bindingResult);

        // Assert
        assertEquals("redirect:/login", result);
        verify(bindingResult, never()).addError(
                argThat(error -> error instanceof FieldError &&
                        ((FieldError) error).getField().equals("repeatedPassword"))
        );
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void registerUser_WithValidData_ShouldEncodePasswordBeforeSaving() {
        // Arrange
        String rawPassword = "mySecurePassword";
        String encodedPassword = "encodedSecurePassword";
        user.setPassword(rawPassword);

        when(userMapper.userRegistrationDTOToUser(userRegistrationDTO)).thenReturn(user);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByTelephoneNumber(user.getTelephoneNumber())).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Act
        String result = userService.registerUser(userRegistrationDTO, bindingResult);

        // Assert
        assertEquals("redirect:/login", result);
        verify(passwordEncoder, times(1)).encode(rawPassword);
        assertEquals(encodedPassword, user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getProfilePage_ShouldRetrieveUserFromSecurityContextAndAddToModel() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            String result = userService.getProfilePage(model);

            // Assert
            assertEquals("profile", result);
            verify(userMapper, times(1)).userToGetUserDTO(user);
            verify(model, times(1)).addAttribute(getUserDTO);
        }
    }

    @Test
    void getProfilePage_ShouldMapUserCorrectly() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            String result = userService.getProfilePage(model);

            // Assert
            assertEquals("profile", result);
            verify(userMapper, times(1)).userToGetUserDTO(user);
            verify(model, times(1)).addAttribute(getUserDTO);

            // Verify the correct user was mapped
            assertEquals("John", getUserDTO.getName());
            assertEquals("Doe", getUserDTO.getSurname());
            assertEquals("john.doe@example.com", getUserDTO.getEmail());
        }
    }

    @Test
    void getProfilePage_ShouldReturnProfileViewName() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            String result = userService.getProfilePage(model);

            // Assert
            assertEquals("profile", result);
        }
    }

    @Test
    void getProfilePage_WithDifferentUser_ShouldHandleCorrectly() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setName("Jane");
        differentUser.setSurname("Smith");
        differentUser.setEmail("jane.smith@example.com");
        differentUser.setTelephoneNumber("+9876543210");

        GetUserDTO differentGetUserDTO = new GetUserDTO();
        differentGetUserDTO.setName("Jane");
        differentGetUserDTO.setSurname("Smith");
        differentGetUserDTO.setEmail("jane.smith@example.com");
        differentGetUserDTO.setTelephoneNumber("+9876543210");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(differentUser);
            when(userMapper.userToGetUserDTO(differentUser)).thenReturn(differentGetUserDTO);

            // Act
            String result = userService.getProfilePage(model);

            // Assert
            assertEquals("profile", result);
            verify(userMapper, times(1)).userToGetUserDTO(differentUser);
            verify(model, times(1)).addAttribute(differentGetUserDTO);
        }
    }

    // Tests for getChangeProfileDataPage method
    @Test
    void getChangeProfileDataPage_ShouldRetrieveUserFromSecurityContextAndAddToModel() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            String result = userService.getChangeProfileDataPage(model);

            // Assert
            assertEquals("change-profile-data", result);
            verify(userMapper, times(1)).userToGetUserDTO(user);
            verify(model, times(1)).addAttribute(getUserDTO);
        }
    }

    @Test
    void getChangeProfileDataPage_ShouldMapUserCorrectly() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            String result = userService.getChangeProfileDataPage(model);

            // Assert
            assertEquals("change-profile-data", result);
            verify(userMapper, times(1)).userToGetUserDTO(user);
            verify(model, times(1)).addAttribute(getUserDTO);

            // Verify the correct user data was mapped
            assertEquals("John", getUserDTO.getName());
            assertEquals("Doe", getUserDTO.getSurname());
            assertEquals("john.doe@example.com", getUserDTO.getEmail());
            assertEquals("+1234567890", getUserDTO.getTelephoneNumber());
        }
    }

    @Test
    void getChangeProfileDataPage_ShouldReturnChangeProfileDataViewName() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            String result = userService.getChangeProfileDataPage(model);

            // Assert
            assertEquals("change-profile-data", result);
        }
    }

    @Test
    void getChangeProfileDataPage_WithDifferentUser_ShouldHandleCorrectly() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(3L);
        differentUser.setName("Alice");
        differentUser.setSurname("Johnson");
        differentUser.setEmail("alice.johnson@example.com");
        differentUser.setTelephoneNumber("+1122334455");
        differentUser.setCountry("Canada");
        differentUser.setAddress("456 Oak Ave");

        GetUserDTO differentGetUserDTO = new GetUserDTO();
        differentGetUserDTO.setName("Alice");
        differentGetUserDTO.setSurname("Johnson");
        differentGetUserDTO.setEmail("alice.johnson@example.com");
        differentGetUserDTO.setTelephoneNumber("+1122334455");
        differentGetUserDTO.setCountry("Canada");
        differentGetUserDTO.setAddress("456 Oak Ave");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(differentUser);
            when(userMapper.userToGetUserDTO(differentUser)).thenReturn(differentGetUserDTO);

            // Act
            String result = userService.getChangeProfileDataPage(model);

            // Assert
            assertEquals("change-profile-data", result);
            verify(userMapper, times(1)).userToGetUserDTO(differentUser);
            verify(model, times(1)).addAttribute(differentGetUserDTO);
        }
    }

    @Test
    void getChangeProfileDataPage_ShouldUseSecurityContextToGetAuthenticatedUser() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            userService.getChangeProfileDataPage(model);

            // Assert
            mockedSecurityContextHolder.verify(SecurityContextHolder::getContext, times(1));
            verify(securityContext, times(1)).getAuthentication();
            verify(authentication, times(1)).getPrincipal();
            verify(userDetailsImpl, times(1)).getUser();
        }
    }

    @Test
    void getProfilePage_ShouldUseSecurityContextToGetAuthenticatedUser() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(user);
            when(userMapper.userToGetUserDTO(user)).thenReturn(getUserDTO);

            // Act
            userService.getProfilePage(model);

            // Assert
            mockedSecurityContextHolder.verify(SecurityContextHolder::getContext, times(1));
            verify(securityContext, times(1)).getAuthentication();
            verify(authentication, times(1)).getPrincipal();
            verify(userDetailsImpl, times(1)).getUser();
        }
    }

    @Test
    void changeProfileData_WithValidData_ShouldUpdateUserAndRedirectToProfile() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(userMapper.getUserDTOToUser(getUserDTO2)).thenReturn(mappedUser);
            when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(false);
            when(userRepository.existsByTelephoneNumber(mappedUser.getTelephoneNumber())).thenReturn(false);
            when(bindingResult.hasErrors()).thenReturn(false);

            // Act
            String result = userService.changeProfileData(getUserDTO2, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            assertEquals("Jane", currentUser.getName());
            assertEquals("Smith", currentUser.getSurname());
            assertEquals("jane.smith@example.com", currentUser.getEmail());
            assertEquals("+9876543210", currentUser.getTelephoneNumber());
            assertEquals("Canada", currentUser.getCountry());
            assertEquals("456 Oak Ave", currentUser.getAddress());
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Test
    void changeProfileData_WhenNewEmailAlreadyExists_ShouldAddErrorAndReturnChangeProfileDataPage() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(userMapper.getUserDTOToUser(getUserDTO2)).thenReturn(mappedUser);
            when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(true);
            when(bindingResult.hasErrors()).thenReturn(true);

            // Act
            String result = userService.changeProfileData(getUserDTO2, bindingResult);

            // Assert
            assertEquals("change-profile-data", result);
            verify(bindingResult, times(1)).addError(
                    argThat(error -> error instanceof FieldError &&
                            ((FieldError) error).getField().equals("email") &&
                            ((FieldError) error).getDefaultMessage().equals("email address already in use"))
            );
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void changeProfileData_WhenEmailUnchanged_ShouldNotAddEmailError() {
        // Arrange
        mappedUser.setEmail("john.doe@example.com"); // Same as current user
        getUserDTO2.setEmail("john.doe@example.com");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(userMapper.getUserDTOToUser(getUserDTO2)).thenReturn(mappedUser);
            when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(true);
            when(userRepository.existsByTelephoneNumber(mappedUser.getTelephoneNumber())).thenReturn(false);
            when(bindingResult.hasErrors()).thenReturn(false);

            // Act
            String result = userService.changeProfileData(getUserDTO2, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            verify(bindingResult, never()).addError(any(FieldError.class));
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Test
    void changeProfileData_WhenNewTelephoneNumberAlreadyExists_ShouldAddErrorAndReturnChangeProfileDataPage() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(userMapper.getUserDTOToUser(getUserDTO2)).thenReturn(mappedUser);
            when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(false);
            when(userRepository.existsByTelephoneNumber(mappedUser.getTelephoneNumber())).thenReturn(true);
            when(bindingResult.hasErrors()).thenReturn(true);

            // Act
            String result = userService.changeProfileData(getUserDTO2, bindingResult);

            // Assert
            assertEquals("change-profile-data", result);
            verify(bindingResult, times(1)).addError(
                    argThat(error -> error instanceof FieldError &&
                            ((FieldError) error).getField().equals("telephoneNumber") &&
                            ((FieldError) error).getDefaultMessage().equals("telephone number already in use"))
            );
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void changeProfileData_WhenTelephoneNumberUnchanged_ShouldNotAddTelephoneNumberError() {
        // Arrange
        mappedUser.setTelephoneNumber("+1234567890"); // Same as current user
        getUserDTO.setTelephoneNumber("+1234567890");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(userMapper.getUserDTOToUser(getUserDTO2)).thenReturn(mappedUser);
            when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(false);
            when(userRepository.existsByTelephoneNumber(mappedUser.getTelephoneNumber())).thenReturn(true);
            when(bindingResult.hasErrors()).thenReturn(false);

            // Act
            String result = userService.changeProfileData(getUserDTO2, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            verify(bindingResult, never()).addError(any(FieldError.class));
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Test
    void changeProfileData_WithMultipleErrors_ShouldAddAllErrorsAndReturnChangeProfileDataPage() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(userMapper.getUserDTOToUser(getUserDTO2)).thenReturn(mappedUser);
            when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(true);
            when(userRepository.existsByTelephoneNumber(mappedUser.getTelephoneNumber())).thenReturn(true);
            when(bindingResult.hasErrors()).thenReturn(true);

            // Act
            String result = userService.changeProfileData(getUserDTO2, bindingResult);

            // Assert
            assertEquals("change-profile-data", result);
            verify(bindingResult, times(2)).addError(any(FieldError.class));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    // Tests for getChangePasswordPage method
    @Test
    void getChangePasswordPage_ShouldCreateNewUpdatePasswordDTOAndAddToModel() {
        // Act
        String result = userService.getChangePasswordPage(model);

        // Assert
        assertEquals("change-password", result);
        verify(model, times(1)).addAttribute(eq("updatePasswordDTO"), any(UpdatePasswordDTO.class));
    }

    @Test
    void getChangePasswordPage_ShouldReturnChangePasswordViewName() {
        // Act
        String result = userService.getChangePasswordPage(model);

        // Assert
        assertEquals("change-password", result);
    }

    // Tests for changePassword method
    @Test
    void changePassword_WithMatchingPasswords_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

            // Act
            String result = userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            verify(passwordEncoder, times(1)).encode("newPassword123");
            assertEquals("encodedNewPassword", currentUser.getPassword());
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Test
    void changePassword_WhenPasswordsDontMatch_ShouldAddErrorAndReturnChangePasswordPage() {
        // Arrange
        updatePasswordDTO.setRepeatedPassword("differentPassword");
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userService.changePassword(updatePasswordDTO, bindingResult);

        // Assert
        assertEquals("change-password", result);
        verify(bindingResult, times(1)).addError(
                argThat(error -> error instanceof FieldError &&
                        ((FieldError) error).getField().equals("repeatedPassword") &&
                        ((FieldError) error).getDefaultMessage().equals("passwords doesn't match"))
        );
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_WhenPasswordsMatch_ShouldNotAddError() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            // Act
            String result = userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            verify(bindingResult, times(0)).addError(any(FieldError.class));
        }
    }

    @Test
    void changePassword_WithPreexistingErrors_ShouldReturnChangePasswordPage() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userService.changePassword(updatePasswordDTO, bindingResult);

        // Assert
        assertEquals("change-password", result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ShouldRetrieveCurrentUserFromSecurityContext() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            // Act
            userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            mockedSecurityContextHolder.verify(SecurityContextHolder::getContext, times(1));
            verify(securityContext, times(1)).getAuthentication();
            verify(authentication, times(1)).getPrincipal();
            verify(userDetailsImpl, times(1)).getUser();
        }
    }

    @Test
    void changePassword_WithEmptyPassword_ShouldStillProcessIfNoErrors() {
        // Arrange
        updatePasswordDTO.setPassword("");
        updatePasswordDTO.setRepeatedPassword("");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode("")).thenReturn("encodedEmptyPassword");

            // Act
            String result = userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            verify(passwordEncoder, times(1)).encode("");
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Test
    void changeProfileData_ShouldNotUpdatePasswordOrRole() {
        // Arrange
        String originalPassword = "encodedPassword";
        RoleNames originalRole = RoleNames.ROLE_USER;
        currentUser.setPassword(originalPassword);
        currentUser.setRoleNames(originalRole);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(userMapper.getUserDTOToUser(getUserDTO2)).thenReturn(mappedUser);
            when(userRepository.existsByEmail(mappedUser.getEmail())).thenReturn(false);
            when(userRepository.existsByTelephoneNumber(mappedUser.getTelephoneNumber())).thenReturn(false);
            when(bindingResult.hasErrors()).thenReturn(false);

            // Act
            userService.changeProfileData(getUserDTO2, bindingResult);

            // Assert
            assertEquals(originalPassword, currentUser.getPassword());
            assertEquals(originalRole, currentUser.getRoleNames());
        }
    }

    @Test
    void getChangePasswordPage_ShouldAddUpdatePasswordDTOWithCorrectAttributeName() {
        // Act
        userService.getChangePasswordPage(model);

        // Assert
        ArgumentCaptor<String> attributeNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UpdatePasswordDTO> attributeValueCaptor = ArgumentCaptor.forClass(UpdatePasswordDTO.class);

        verify(model, times(1)).addAttribute(attributeNameCaptor.capture(), attributeValueCaptor.capture());
        assertEquals("updatePasswordDTO", attributeNameCaptor.getValue());
        assertNotNull(attributeValueCaptor.getValue());
    }

    @Test
    void getChangePasswordPage_ShouldCreateFreshUpdatePasswordDTO() {
        // Act
        userService.getChangePasswordPage(model);

        // Assert
        ArgumentCaptor<UpdatePasswordDTO> captor = ArgumentCaptor.forClass(UpdatePasswordDTO.class);
        verify(model, times(1)).addAttribute(eq("updatePasswordDTO"), captor.capture());

        UpdatePasswordDTO capturedDTO = captor.getValue();
        assertNotNull(capturedDTO);
        assertNull(capturedDTO.getPassword());
        assertNull(capturedDTO.getRepeatedPassword());
    }

    @Test
    void getChangePasswordPage_ShouldNotInteractWithSecurityContext() {
        // This test ensures the method doesn't unnecessarily access security context
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Act
            String result = userService.getChangePasswordPage(model);

            // Assert
            assertEquals("change-password", result);
            mockedSecurityContextHolder.verifyNoInteractions();
        }
    }

    @Test
    void getChangePasswordPage_ShouldNotInteractWithRepository() {
        // Act
        userService.getChangePasswordPage(model);

        // Assert
        verifyNoInteractions(userRepository);
    }

    @Test
    void getChangePasswordPage_ShouldNotInteractWithPasswordEncoder() {
        // Act
        userService.getChangePasswordPage(model);

        // Assert
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getChangePasswordPage_CalledMultipleTimes_ShouldCreateNewDTOEachTime() {
        // Act
        userService.getChangePasswordPage(model);
        userService.getChangePasswordPage(model);

        // Assert
        ArgumentCaptor<UpdatePasswordDTO> captor = ArgumentCaptor.forClass(UpdatePasswordDTO.class);
        verify(model, times(2)).addAttribute(eq("updatePasswordDTO"), captor.capture());

        List<UpdatePasswordDTO> capturedDTOs = captor.getAllValues();
        assertEquals(2, capturedDTOs.size());
        assertNotSame(capturedDTOs.get(0), capturedDTOs.get(1));
    }

    @Test
    void changePassword_WithNonNullPasswordAndNullRepeated_ShouldAddError() {
        // Arrange
        updatePasswordDTO.setPassword("somePassword");
        updatePasswordDTO.setRepeatedPassword(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userService.changePassword(updatePasswordDTO, bindingResult);

        // Assert
        assertEquals("change-password", result);
        verify(bindingResult, times(1)).addError(any(FieldError.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ShouldVerifyPasswordEncodingOccursBeforeSaving() {
        // Arrange
        String rawPassword = "myNewPassword";
        String encodedPassword = "encodedMyNewPassword";
        updatePasswordDTO.setPassword(rawPassword);
        updatePasswordDTO.setRepeatedPassword(rawPassword);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

            // Act
            userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            InOrder inOrder = inOrder(passwordEncoder, userRepository);
            inOrder.verify(passwordEncoder).encode(rawPassword);
            inOrder.verify(userRepository).save(currentUser);
        }
    }

    @Test
    void changePassword_ShouldUpdatePasswordOnCurrentUserObject() {
        // Arrange
        String oldPassword = "oldEncodedPassword";
        String newPassword = "newPassword123";
        String newEncodedPassword = "newEncodedPassword123";

        currentUser.setPassword(oldPassword);
        updatePasswordDTO.setPassword(newPassword);
        updatePasswordDTO.setRepeatedPassword(newPassword);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);

            // Act
            userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertNotEquals(oldPassword, currentUser.getPassword());
            assertEquals(newEncodedPassword, currentUser.getPassword());
        }
    }

    @Test
    void changePassword_WithValidationErrors_ShouldNotAccessSecurityContext() {
        // Arrange
        updatePasswordDTO.setPassword("password1");
        updatePasswordDTO.setRepeatedPassword("password2");
        when(bindingResult.hasErrors()).thenReturn(true);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Act
            String result = userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals("change-password", result);
            mockedSecurityContextHolder.verifyNoInteractions();
        }
    }

    @Test
    void changePassword_WithPasswordMismatch_ShouldNotAccessSecurityContext() {
        // Arrange
        updatePasswordDTO.setPassword("password1");
        updatePasswordDTO.setRepeatedPassword("password2");
        when(bindingResult.hasErrors()).thenReturn(true);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Act
            String result = userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals("change-password", result);
            mockedSecurityContextHolder.verifyNoInteractions();
        }
    }

    @Test
    void changePassword_WithCaseSensitivePasswordMismatch_ShouldAddError() {
        // Arrange
        updatePasswordDTO.setPassword("Password123");
        updatePasswordDTO.setRepeatedPassword("password123"); // Different case
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userService.changePassword(updatePasswordDTO, bindingResult);

        // Assert
        assertEquals("change-password", result);
        verify(bindingResult, times(1)).addError(
                argThat(error -> error instanceof FieldError &&
                        ((FieldError) error).getField().equals("repeatedPassword") &&
                        ((FieldError) error).getDefaultMessage().equals("passwords doesn't match"))
        );
    }

    @Test
    void changePassword_WithWhitespaceInPasswords_ShouldTreatAsDistinct() {
        // Arrange
        updatePasswordDTO.setPassword("password 123");
        updatePasswordDTO.setRepeatedPassword("password123"); // No space
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userService.changePassword(updatePasswordDTO, bindingResult);

        // Assert
        assertEquals("change-password", result);
        verify(bindingResult, times(1)).addError(any(FieldError.class));
    }

    @Test
    void changePassword_ShouldNotModifyOtherUserProperties() {
        // Arrange
        String originalEmail = "user@example.com";
        String originalName = "John";
        Long originalId = 1L;
        RoleNames originalRole = RoleNames.ROLE_USER;

        currentUser.setEmail(originalEmail);
        currentUser.setName(originalName);
        currentUser.setId(originalId);
        currentUser.setRoleNames(originalRole);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            // Act
            userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals(originalEmail, currentUser.getEmail());
            assertEquals(originalName, currentUser.getName());
            assertEquals(originalId, currentUser.getId());
            assertEquals(originalRole, currentUser.getRoleNames());
        }
    }

    @Test
    void changePassword_WithVeryLongPassword_ShouldEncodeAndSaveSuccessfully() {
        // Arrange
        String longPassword = "a".repeat(1000);
        updatePasswordDTO.setPassword(longPassword);
        updatePasswordDTO.setRepeatedPassword(longPassword);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(longPassword)).thenReturn("encodedLongPassword");

            // Act
            String result = userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            verify(passwordEncoder, times(1)).encode(longPassword);
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Test
    void changePassword_WithSpecialCharactersInPassword_ShouldHandleCorrectly() {
        // Arrange
        String specialPassword = "P@ssw0rd!#$%^&*()";
        updatePasswordDTO.setPassword(specialPassword);
        updatePasswordDTO.setRepeatedPassword(specialPassword);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(specialPassword)).thenReturn("encodedSpecialPassword");

            // Act
            String result = userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            assertEquals("redirect:/profile", result);
            verify(passwordEncoder, times(1)).encode(specialPassword);
        }
    }

    @Test
    void changePassword_WhenBindingResultHasErrorsBeforeValidation_ShouldReturnChangePasswordPage() {
        // Arrange
        // Simulate validation errors already present (e.g., from @Valid annotation)
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userService.changePassword(updatePasswordDTO, bindingResult);

        // Assert
        assertEquals("change-password", result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ShouldOnlyCallSaveOnceOnSuccess() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
            when(userDetailsImpl.getUser()).thenReturn(currentUser);
            when(bindingResult.hasErrors()).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            // Act
            userService.changePassword(updatePasswordDTO, bindingResult);

            // Assert
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Test
    void changePassword_WithPasswordsMismatchErrorMessage_ShouldHaveCorrectFieldAndMessage() {
        // Arrange
        updatePasswordDTO.setPassword("password1");
        updatePasswordDTO.setRepeatedPassword("password2");
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        userService.changePassword(updatePasswordDTO, bindingResult);

        // Assert
        ArgumentCaptor<FieldError> errorCaptor = ArgumentCaptor.forClass(FieldError.class);
        verify(bindingResult, times(1)).addError(errorCaptor.capture());

        FieldError capturedError = errorCaptor.getValue();
        assertEquals("updatePasswordDTO", capturedError.getObjectName());
        assertEquals("repeatedPassword", capturedError.getField());
        assertEquals("passwords doesn't match", capturedError.getDefaultMessage());
    }
}
