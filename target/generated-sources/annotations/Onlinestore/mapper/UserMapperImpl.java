package Onlinestore.mapper;

import Onlinestore.dto.GetUserDTO;
import Onlinestore.dto.UserRegistrationDTO;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-09T12:35:34+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User userRegistrationDTOToUser(UserRegistrationDTO userRegistrationDTO) {
        if ( userRegistrationDTO == null ) {
            return null;
        }

        User user = new User();

        user.setName( userRegistrationDTO.getName() );
        user.setSurname( userRegistrationDTO.getSurname() );
        user.setEmail( userRegistrationDTO.getEmail() );
        user.setPassword( userRegistrationDTO.getPassword() );
        user.setRepeatedPassword( userRegistrationDTO.getRepeatedPassword() );
        user.setTelephoneNumber( userRegistrationDTO.getTelephoneNumber() );
        user.setCountry( userRegistrationDTO.getCountry() );
        user.setAddress( userRegistrationDTO.getAddress() );
        user.setRoleNames( userRegistrationDTO.getRoleNames() );

        return user;
    }

    @Override
    public GetUserDTO userToGetUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        GetUserDTO getUserDTO = new GetUserDTO();

        getUserDTO.setId( user.getId() );
        getUserDTO.setName( user.getName() );
        getUserDTO.setSurname( user.getSurname() );
        getUserDTO.setEmail( user.getEmail() );
        getUserDTO.setTelephoneNumber( user.getTelephoneNumber() );
        getUserDTO.setCountry( user.getCountry() );
        getUserDTO.setAddress( user.getAddress() );
        Set<Order> set = user.getOrders();
        if ( set != null ) {
            getUserDTO.setOrders( new LinkedHashSet<Order>( set ) );
        }
        getUserDTO.setRoleNames( user.getRoleNames() );

        return getUserDTO;
    }

    @Override
    public User getUserDTOToUser(GetUserDTO getUserDTO) {
        if ( getUserDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( getUserDTO.getId() );
        user.setName( getUserDTO.getName() );
        user.setSurname( getUserDTO.getSurname() );
        user.setEmail( getUserDTO.getEmail() );
        user.setTelephoneNumber( getUserDTO.getTelephoneNumber() );
        user.setCountry( getUserDTO.getCountry() );
        user.setAddress( getUserDTO.getAddress() );
        Set<Order> set = getUserDTO.getOrders();
        if ( set != null ) {
            user.setOrders( new LinkedHashSet<Order>( set ) );
        }
        user.setRoleNames( getUserDTO.getRoleNames() );

        return user;
    }
}
