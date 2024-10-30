package project.transaction.management.system.mapper;


import org.mapstruct.Mapper;
import project.transaction.management.system.api.resource.account.AccountRequestResource;
import project.transaction.management.system.api.resource.account.AccountResponseResource;
import project.transaction.management.system.dao.entity.AccountEntity;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponseResource fromEntity(AccountEntity source);

    AccountEntity toEntity(AccountRequestResource source);


}
