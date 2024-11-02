package project.transaction.management.system.mapper;


import org.mapstruct.Mapper;
import project.transaction.management.system.api.resource.transaction.TransactionRequestResource;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;
import project.transaction.management.system.dao.entity.TransactionEntity;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponseResource fromEntity(TransactionEntity source);

    TransactionEntity toEntity(TransactionRequestResource source);
}
