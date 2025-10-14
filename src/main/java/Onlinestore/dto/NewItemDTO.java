package Onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
public class NewItemDTO {
    @Getter
    @Setter
    @Size(min = 1, max = 64)
    private String name;

    @Getter
    @Setter
    @NotNull
    @Min(0)
    private Double price;

    @Getter
    @Setter
    @NotNull
    @Min(0)
    private Integer amount;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String logoName; // only filename without directory

    @Getter
    @Setter
    private Set<String> imageNames; // only filenames without directories

    @Getter
    @Setter
    private Map<String, String> specs;
}
