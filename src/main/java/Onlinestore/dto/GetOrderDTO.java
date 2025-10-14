package Onlinestore.dto;

import Onlinestore.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class GetOrderDTO {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private Integer userId;

    @Getter
    @Setter
    private Item item;

    @Getter
    @Setter
    private Integer amount;
}
