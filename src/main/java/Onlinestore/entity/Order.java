package Onlinestore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;

    @Min(0)
    private Integer amount;

    public Order(Item item, int amount, long userId) {
        this.item = item;
        this.amount = amount;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Order)) {
            return false;
        }

        Order secondOrder = (Order) obj;
        return userId == secondOrder.getUserId() && item.equals(secondOrder.getItem());
    }
}
