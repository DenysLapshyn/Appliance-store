package Onlinestore.mapper;

import Onlinestore.dto.GetUserDTO;
import Onlinestore.dto.UserRegistrationDTO;
import Onlinestore.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userRegistrationDTOToUser(UserRegistrationDTO userRegistrationDTO);

    GetUserDTO userToGetUserDTO(User user);

    User getUserDTOToUser(GetUserDTO getUserDTO);

}
