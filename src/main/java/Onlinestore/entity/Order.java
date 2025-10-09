package Onlinestore.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    
    @Getter
    @Setter
    @JoinColumn
    private Long userId;
    
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;
    
    @Getter
    @Setter
    @Min(0)
    private Integer amount;
    
    public Order(Item item, int amount, long userId)
    {
        this.item = item;
        this.amount = amount;
        this.userId = userId;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Order))
        {
            return false;
        }
        
        Order secondOrder = (Order) obj;
        return userId == secondOrder.getUserId() && item.equals(secondOrder.getItem());
    }
}
