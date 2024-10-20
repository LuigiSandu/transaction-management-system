package project.transaction.management.system.dao.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.transaction.management.system.dao.entity.TransactionEntity;
import project.transaction.management.system.dao.entity.UserEntity;

@Repository
public interface TransactionReposittory extends JpaRepository<TransactionEntity, Long> {

}
