package br.com.financial.transaction;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

  @PostMapping
  public void createTransaction(@Valid @RequestBody TransactionDto transactionDto) {
    // If the DTO fails validation, Spring Web will automatically throw a 400 Bad Request
  }
}
