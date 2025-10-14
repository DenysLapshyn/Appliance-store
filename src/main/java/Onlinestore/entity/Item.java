package Onlinestore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 64)
    private String name;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    @Min(0)
    private Integer amount;

    private String description;

    private String logoName; // only filename without directory

    @ElementCollection
    private Set<String> imageNames; // only filenames without directories

    @ElementCollection
    private Map<String, String> specs;

    public Item() {
        imageNames = new HashSet<>();
        specs = new LinkedHashMap<>();
    }

    public Item(String name, double price, int amount, String description, String logoName, Set<String> imageNames, Map<String, String> specs) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.logoName = logoName;
        this.imageNames = imageNames;
        this.specs = specs;
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(id, ((Item) obj).getId());
    }
}