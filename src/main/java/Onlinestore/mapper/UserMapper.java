package Onlinestore.mapper;

import Onlinestore.dto.GetUserDTO;
import Onlinestore.dto.UserRegistrationDTO;
import Onlinestore.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public abstract User userRegistrationDTOToUser(UserRegistrationDTO userRegistrationDTO);

    public abstract GetUserDTO userToGetUserDTO(User user);

    public abstract User getUserDTOToUser(GetUserDTO getUserDTO);

}
