package Onlinestore.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GetItemDTO
{
    @Getter
    @Setter
    private Long id;

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

    public GetItemDTO()
    {
        imageNames = new HashSet<>();
        specs = new LinkedHashMap<>();
    }

    public GetItemDTO(Long id, String name, Double price, Integer amount, String description, String logoName, Set<String> imageNames, Map<String, String> specs) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.logoName = logoName;
        this.imageNames = imageNames;
        this.specs = specs;
    }
}
