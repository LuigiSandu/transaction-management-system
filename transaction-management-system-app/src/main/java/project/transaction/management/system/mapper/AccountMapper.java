package project.transaction.management.system.mapper;


import org.mapstruct.Mapper;
import project.transaction.management.system.api.resource.UserRequestResource;
import project.transaction.management.system.api.resource.UserResponseResource;
import project.transaction.management.system.dao.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    UserResponseResource fromUserRequestResource(UserEntity source);

    UserRequestResource fromUserResponseResource(UserResponseResource source);

    UserEntity toEntity(UserRequestResource source);

}
