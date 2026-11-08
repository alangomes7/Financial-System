package br.com.financial.account;

import br.com.financial.shared.exceptions.InvalidRequestException;
import br.com.financial.shared.pagination.PageResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

  private static final Set<String> SORTABLE_PROPERTIES =
      Set.of("accountNumber", "balance", "createdAt");

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping
  public ResponseEntity<PageResponse<AccountResponseDto>> getAccounts(
      @RequestParam(required = false) String accountNumber,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    pageable.getSort().stream()
        .map(order -> order.getProperty())
        .filter(property -> !SORTABLE_PROPERTIES.contains(property))
        .findFirst()
        .ifPresent(
            property -> {
              throw new InvalidRequestException("Unsupported sort property: " + property);
            });
    Page<AccountResponseDto> accounts = accountService.getAccounts(accountNumber, pageable);
    return ResponseEntity.ok(PageResponse.from(accounts));
  }

  // POST /api/v1/accounts -> 201 Created
  @PostMapping
  public ResponseEntity<AccountResponseDto> createAccount(
      @Valid @RequestBody AccountRequestDto request) {
    AccountResponseDto createdAccount = accountService.createAccount(request);

    // Build the Location header pointing to the newly created resource
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdAccount.getId())
            .toUri();

    return ResponseEntity.created(location).body(createdAccount);
  }

  // GET /api/v1/accounts/{id} -> 200 OK
  @GetMapping("/{id}")
  public ResponseEntity<AccountResponseDto> getAccount(@PathVariable UUID id) {
    // If not found, the service throws ResourceNotFoundException -> Handled as 404
    AccountResponseDto account = accountService.getAccountById(id);
    return ResponseEntity.ok(account);
  }

  // PUT /api/v1/accounts/{id} -> 200 OK
  @PutMapping("/{id}")
  public ResponseEntity<AccountResponseDto> updateAccount(
      @PathVariable UUID id, @Valid @RequestBody AccountRequestDto request) {

    AccountResponseDto updatedAccount = accountService.updateAccount(id, request);
    return ResponseEntity.ok(updatedAccount);
  }

  // DELETE /api/v1/accounts/{id} -> 204 No Content
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }
}
