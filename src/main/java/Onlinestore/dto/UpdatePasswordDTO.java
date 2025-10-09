package Onlinestore.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDTO {
    @Getter
    @Setter
    @NotBlank(message = "should not be blank")
    @Size(min = 8, max = 64, message = "should be from 8 to 64 symbols")
    private String password;

    @Getter
    @Setter
    @NotBlank(message = "should not be blank")
    private String repeatedPassword;
}
