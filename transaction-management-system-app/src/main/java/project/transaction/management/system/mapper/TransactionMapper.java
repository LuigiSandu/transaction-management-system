package project.transaction.management.system.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.dao.entity.TransactionEntity;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "sourceAccountNumber", source = "sourceAccount.accountNumber")
    @Mapping(target = "targetAccountNumber", source = "targetAccount.accountNumber")
    TransactionResponseResource fromEntity(TransactionEntity source);

    TransactionEntity toEntity(TransactionRequestResource source);
}
