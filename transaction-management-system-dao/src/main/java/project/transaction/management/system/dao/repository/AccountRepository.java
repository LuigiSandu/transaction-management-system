package project.transaction.management.system.dao.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.transaction.management.system.dao.entity.AccountEntity;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    Optional<AccountEntity> findByAccountNumberAndUserId(String accountNumber, Long userId);

    boolean existsByAccountNumber(String accountNumber);
}
