package project.transaction.management.system.mapper;


import org.mapstruct.Mapper;
import project.transaction.management.system.api.resource.AccountRequestResource;
import project.transaction.management.system.api.resource.AccountResponseResource;
import project.transaction.management.system.dao.entity.AccountEntity;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponseResource fromAccountEntity(AccountEntity source);

    AccountEntity toEntity(AccountRequestResource source);

}
