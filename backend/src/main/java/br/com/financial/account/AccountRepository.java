package br.com.financial.account;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, UUID> {

  boolean existsByAccountNumber(String accountNumber);

  Optional<AccountModel> findByAccountNumber(String accountNumber);

  Page<AccountModel> findByAccountNumberContainingIgnoreCase(
      String accountNumber, Pageable pageable);
}
