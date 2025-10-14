package Onlinestore.controller;

import Onlinestore.dto.GetUserDTO;
import Onlinestore.dto.UpdatePasswordDTO;
import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserDetailsImpl;
import Onlinestore.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        logger.info("Someone is requesting profile page");
        return userService.getProfilePage(model);
    }

    @GetMapping("/profile/change-profile-data")
    public String getChangeProfileDataPage(Model model) {
        logger.info("Someone is requesting change profile data page");
        return userService.getChangeProfileDataPage(model);
    }

    @PostMapping("/profile/change-profile-data")
    public String changeProfileData(@ModelAttribute("getUserDTO") @Valid GetUserDTO getUserDTO, BindingResult bindingResult) {
        logger.info("Someone trying to change his profile");
        return userService.changeProfileData(getUserDTO, bindingResult);
    }

    @GetMapping("/profile/change-password")
    public String getChangePasswordPage(Model model) {
        logger.info("User or admin is trying to get change password page");
        return userService.getChangePasswordPage(model);
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute("updatePasswordDTO") @Valid UpdatePasswordDTO updatePasswordDTO, BindingResult bindingResult) {
        logger.info("Someone is trying change his password");
        return userService.changePassword(updatePasswordDTO, bindingResult);
    }

    @GetMapping("/profile/delete-account")
    public String getDeleteAccountPage() {
        logger.info("User or admin trying to get delete account page");
        return "delete-account";
    }

    @PostMapping("/profile/delete-account")
    public String deleteAccount() {
        logger.info("Someone is trying to delete his account");
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        userRepository.delete(user);

        return "redirect:/logout";
    }
}
