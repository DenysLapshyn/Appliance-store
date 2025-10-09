package Onlinestore.mapper;

import Onlinestore.dto.GetOrderDTO;
import Onlinestore.entity.Order;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    public abstract GetOrderDTO orderToOrderDTO(Order order);

    public abstract Set<GetOrderDTO> orderListToOrderDTOSet(Set<Order> orderList);

}
