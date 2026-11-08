package br.com.financial.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public class AccountRequestDto {

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotBlank(message = "Account number cannot be empty")
  @Size(min = 5, max = 50, message = "Account number must be between 5 and 50 characters")
  private String accountNumber;

  @NotNull(message = "Initial balance is required")
  @PositiveOrZero(message = "Initial balance cannot be negative")
  private BigDecimal balance;

  // Getters and Setters
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
