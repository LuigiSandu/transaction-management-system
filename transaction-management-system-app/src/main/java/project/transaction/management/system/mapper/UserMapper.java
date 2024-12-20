package project.transaction.management.system.mapper;


import org.mapstruct.Mapper;
import project.transaction.management.system.api.resource.user.UserRequestResource;
import project.transaction.management.system.api.resource.user.UserResponseResource;
import project.transaction.management.system.dao.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseResource fromEntity(UserEntity source);

    UserEntity toEntity(UserRequestResource source);
}
