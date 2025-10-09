package Onlinestore.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "items")
public class Item
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    
    @Getter
    @Setter
    @Column(nullable = false)
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
    
    public Item()
    {
        imageNames = new HashSet<>();
        specs = new LinkedHashMap<>();
    }
    
    public Item(String name, double price, int amount, String description, String logoName, Set<String> imageNames, Map<String, String> specs)
    {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.logoName = logoName;
        this.imageNames = imageNames;
        this.specs = specs;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return Objects.equals(id, ((Item) obj).getId());
    }
}