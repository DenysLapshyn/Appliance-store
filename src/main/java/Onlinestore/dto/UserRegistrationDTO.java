package Onlinestore.dto;

import Onlinestore.entity.Order;
import Onlinestore.security.RoleNames;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
public class UserRegistrationDTO {
    @Getter
    @Setter
    @NotBlank(message = "should not be blank")
    @Size(min = 1, max = 30, message = "should be from 1 to 30 symbols")
    private String name;

    @Getter
    @Setter
    @NotBlank(message = "should not be blank")
    @Size(min = 1, max = 30, message = "should be from 1 to 30 symbols")
    private String surname;

    @Getter
    @Setter
    @NotBlank(message = "should not be blank")
    @Size(min = 5, max = 32, message = "should be from 5 to 32 symbols")
    @Email(message = "this is not an email")
    private String email;

    @Getter
    @Setter
    @NotBlank(message = "should not be blank")
    @Size(min = 8, max = 64, message = "should be from 8 to 64 symbols")
    private String password;

    @Getter
    @Setter
    @NotBlank(message = "should not be blank")
    private String repeatedPassword;

    @Getter
    @Setter
    @NotNull(message = "should not be blank")
    @Pattern(regexp = "\\d{6,12}", message = "telephone number must be from 6 to 12 digits")
    private String telephoneNumber;

    @Getter
    @Setter
    @Size(min = 3, max = 50, message = "should be from 3 to 50 symbols")
    private String country;

    @Getter
    @Setter
    @Size(min = 10, max = 100, message = "should be from 10 to 100 symbols")
    private String address;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private RoleNames roleNames;

    public UserRegistrationDTO() {
        repeatedPassword = "";
    }

    public UserRegistrationDTO(String name, String surname, String email, String password, String repeatedPassword, String telephoneNumber, String country, String address, Set<Order> orders, RoleNames roleNames) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.telephoneNumber = telephoneNumber;
        this.country = country;
        this.address = address;
        this.roleNames = roleNames;
    }
}