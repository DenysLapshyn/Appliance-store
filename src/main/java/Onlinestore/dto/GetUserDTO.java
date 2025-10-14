package Onlinestore.dto;

import Onlinestore.entity.Order;
import Onlinestore.security.RoleNames;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class GetUserDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

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
    private Set<Order> orders;

    @Getter
    @Setter
    private RoleNames roleNames;

    public GetUserDTO() {
        orders = new HashSet<>();
    }
}
