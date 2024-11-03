package project.transaction.management.system.dao.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.transaction.management.system.dao.entity.TransactionEntity;

import java.util.List;

@Repository
public interface TransactionReposittory extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByAccount_User_Id(Long userId);
}
