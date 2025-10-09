package Onlinestore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class UpdateItemDTO
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
    @ElementCollection
    private Set<String> imageNames; // only filenames without directories

    @Getter
    @Setter
    @ElementCollection
    private Map<String, String> specs;

    public UpdateItemDTO()
    {
        imageNames = new HashSet<>();
        specs = new LinkedHashMap<>();
    }
}
