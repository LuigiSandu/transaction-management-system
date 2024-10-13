package project.transaction.management.system.mapper;


import org.mapstruct.Mapper;
import project.transaction.management.system.api.resource.UserRequestResource;
import project.transaction.management.system.api.resource.UserResponseResource;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseResource fromUserRequestResource(UserRequestResource source);
    UserRequestResource fromUserResponseResource(UserResponseResource source);
}
