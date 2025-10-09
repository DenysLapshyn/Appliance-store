package Onlinestore.service;

import Onlinestore.dto.GetUserDTO;
import Onlinestore.dto.UpdatePasswordDTO;
import Onlinestore.dto.UserRegistrationDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.UserMapper;
import Onlinestore.model.RoleNames;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;


@Service
@AllArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public boolean isEmailUnique(User user)
    {
        return !userRepository.existsByEmail(user.getEmail());
    }
    
    public boolean isTelephoneNumberUnique(User user)
    {
        return !userRepository.existsByTelephoneNumber(user.getTelephoneNumber());
    }

    public String registerUser(UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {
        User user = userMapper.userRegistrationDTOToUser(userRegistrationDTO);

        // check if email already in use
        if (userRepository.existsByEmail(user.getEmail()))
        {
            bindingResult.addError(new FieldError("userRegistrationDTO", "email", "email address already in use"));
        }

        // check if telephoneNumber already in use
        if (userRepository.existsByTelephoneNumber(user.getTelephoneNumber()))
        {
            bindingResult.addError(new FieldError("userRegistrationDTO", "telephoneNumber", "telephone number already in use"));
        }

        // check if passwords match
        if (user.getRepeatedPassword() != null && !user.getPassword().equals(user.getRepeatedPassword()))
        {
            bindingResult.addError(new FieldError("userRegistrationDTO", "repeatedPassword", "passwords don't match"));
        }

        if (bindingResult.hasErrors())
        {
            return "registration";
        }

        user.setRoleNames(RoleNames.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/login";
    }

    public String getProfilePage(Model model) {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        GetUserDTO getUserDTO = userMapper.userToGetUserDTO(user);
        model.addAttribute(getUserDTO);
        return "profile";
    }

    public String getChangeProfileDataPage(Model model) {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        GetUserDTO getUserDTO = userMapper.userToGetUserDTO(user);
        model.addAttribute(getUserDTO);

        return "change-profile-data";
    }

    public String changeProfileData(GetUserDTO getUserDTO, BindingResult bindingResult) {
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        User user = userMapper.getUserDTOToUser(getUserDTO);

        // check if new email already in use
        if (userRepository.existsByEmail(user.getEmail()) && !user.getEmail().equals(currentUser.getEmail()))
        {
            bindingResult.addError(new FieldError("user", "email", "email address already in use"));
        }

        // check if new telephoneNumber already in use
        if (userRepository.existsByTelephoneNumber(user.getTelephoneNumber()) && !user.getTelephoneNumber().equals(currentUser.getTelephoneNumber()))
        {
            bindingResult.addError(new FieldError("user", "telephoneNumber", "telephone number already in use"));
        }

        if (bindingResult.hasErrors())
        {
            return "change-profile-data";
        }

        currentUser.setName(user.getName());
        currentUser.setSurname(user.getSurname());
        currentUser.setEmail(user.getEmail());
        currentUser.setTelephoneNumber(user.getTelephoneNumber());
        currentUser.setCountry(user.getCountry());
        currentUser.setAddress(user.getAddress());
        userRepository.save(currentUser);

        return "redirect:/profile";
    }

    public String getChangePasswordPage(Model model) {
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        model.addAttribute("updatePasswordDTO", updatePasswordDTO);

        return "change-password";
    }

    public String changePassword(UpdatePasswordDTO updatePasswordDTO, BindingResult bindingResult) {
        if (!updatePasswordDTO.getPassword().equals(updatePasswordDTO.getRepeatedPassword()))
        {
            bindingResult.addError(new FieldError("updatePasswordDTO", "repeatedPassword", "passwords doesn't match"));
        }

        if (bindingResult.hasErrors())
        {
            return "change-password";
        }
        else
        {
            User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            currentUser.setPassword(passwordEncoder.encode(updatePasswordDTO.getPassword()));
            userRepository.save(currentUser);

            return "redirect:/profile";
        }
    }
}
