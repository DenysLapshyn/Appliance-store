package Onlinestore.controller;

import Onlinestore.dto.GetUserDTO;
import Onlinestore.dto.UpdatePasswordDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.UserMapper;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.service.UserService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class ProfileController
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @GetMapping("/profile")
    public String getProfilePage(Model model)
    {
        logger.info("Someone is requesting profile page");
        return userService.getProfilePage(model);
    }
    
    @GetMapping("/profile/change-profile-data")
    public String getChangeProfileDataPage(Model model)
    {
        logger.info("Someone is requesting change profile data page");
        return userService.getChangeProfileDataPage(model);
    }
    
    @PostMapping("/profile/change-profile-data")
    public String changeProfileData(@ModelAttribute("getUserDTO") @Valid GetUserDTO getUserDTO, BindingResult bindingResult)
    {
        logger.info("Someone trying to change his profile");
        return userService.changeProfileData(getUserDTO, bindingResult);
    }
    
    @GetMapping("/profile/change-password")
    public String getChangePasswordPage(Model model)
    {
        logger.info("User or admin is trying to get change password page");
        return userService.getChangePasswordPage(model);
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute("updatePasswordDTO") @Valid UpdatePasswordDTO updatePasswordDTO, BindingResult bindingResult)
    {
        logger.info("Someone is trying change his password");
        return userService.changePassword(updatePasswordDTO, bindingResult);
    }
    
    @GetMapping("/profile/delete-account")
    public String getDeleteAccountPage()
    {
        logger.info("User or admin trying to get delete account page");
        return "delete-account";
    }
    
    @PostMapping("/profile/delete-account")
    public String deleteAccount()
    {
        logger.info("Someone is trying to delete his account");
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        userRepository.delete(user);
        
        return "redirect:/logout";
    }
}
