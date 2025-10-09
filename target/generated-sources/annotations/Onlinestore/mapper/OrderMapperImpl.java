package Onlinestore.mapper;

import Onlinestore.dto.GetOrderDTO;
import Onlinestore.entity.Order;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-09T13:50:58+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public GetOrderDTO orderToOrderDTO(Order order) {
        if ( order == null ) {
            return null;
        }

        GetOrderDTO getOrderDTO = new GetOrderDTO();

        getOrderDTO.setId( order.getId() );
        if ( order.getUserId() != null ) {
            getOrderDTO.setUserId( order.getUserId().intValue() );
        }
        getOrderDTO.setItem( order.getItem() );
        getOrderDTO.setAmount( order.getAmount() );

        return getOrderDTO;
    }

    @Override
    public Set<GetOrderDTO> orderListToOrderDTOSet(Set<Order> orderList) {
        if ( orderList == null ) {
            return null;
        }

        Set<GetOrderDTO> set = new LinkedHashSet<GetOrderDTO>( Math.max( (int) ( orderList.size() / .75f ) + 1, 16 ) );
        for ( Order order : orderList ) {
            set.add( orderToOrderDTO( order ) );
        }

        return set;
    }
}
