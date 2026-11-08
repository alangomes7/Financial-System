package br.com.financial.account;

import br.com.financial.shared.exceptions.BusinessRuleException;
import br.com.financial.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

  private final AccountRepository accountRepository;

  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Transactional(readOnly = true)
  public AccountResponseDto getAccountById(UUID id) {
    return accountRepository
        .findById(id)
        .map(this::convertToDto)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
  }

  @Transactional(readOnly = true)
  public Page<AccountResponseDto> getAccounts(String accountNumber, Pageable pageable) {
    Page<AccountModel> accounts =
        accountNumber == null || accountNumber.isBlank()
            ? accountRepository.findAll(pageable)
            : accountRepository.findByAccountNumberContainingIgnoreCase(
                accountNumber.trim(), pageable);
    return accounts.map(this::convertToDto);
  }

  @Transactional
  public AccountResponseDto createAccount(AccountRequestDto request) {
    if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
      throw new BusinessRuleException("Account number already exists.");
    }

    AccountModel account = new AccountModel();
    account.setUserId(request.getUserId());
    account.setAccountNumber(request.getAccountNumber());
    account.setBalance(request.getBalance());

    AccountModel savedAccount = accountRepository.save(account);
    return convertToDto(savedAccount);
  }

  @Transactional
  public AccountResponseDto updateAccount(UUID id, AccountRequestDto request) {
    AccountModel account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

    if (!account.getAccountNumber().equals(request.getAccountNumber())
        && accountRepository.existsByAccountNumber(request.getAccountNumber())) {
      throw new BusinessRuleException("Account number already exists.");
    }

    account.setUserId(request.getUserId());
    account.setAccountNumber(request.getAccountNumber());
    account.setBalance(request.getBalance());

    AccountModel updatedAccount = accountRepository.save(account);
    return convertToDto(updatedAccount);
  }

  @Transactional
  public void deleteAccount(UUID id) {
    AccountModel account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

    accountRepository.delete(account);
  }

  private AccountResponseDto convertToDto(AccountModel model) {
    return new AccountResponseDto(
        model.getId(),
        model.getUserId(),
        model.getAccountNumber(),
        model.getBalance(),
        model.getCreatedAt(),
        model.getUpdatedAt());
  }
}
