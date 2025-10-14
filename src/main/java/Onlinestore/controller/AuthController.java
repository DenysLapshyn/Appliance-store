package Onlinestore.controller;

import Onlinestore.dto.UserRegistrationDTO;
import Onlinestore.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@AllArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @GetMapping("/login")
    public String getLoginPage() {
        logger.info("User trying to log in");
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(@ModelAttribute("userRegistrationDTO") UserRegistrationDTO userRegistrationDTO) {
        logger.info("Someone trying to receive registration page");
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute("userRegistrationDTO") @Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {
        logger.info("User trying to register");
        return userService.registerUser(userRegistrationDTO, bindingResult);
    }
}
