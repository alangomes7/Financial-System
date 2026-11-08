package br.com.financial.shared.exceptions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  static class DummyRequest {
    @NotBlank(message = "Field name cannot be blank")
    private String name;

    @NotNull(message = "Field ID cannot be null")
    private UUID id;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public UUID getId() {
      return id;
    }

    public void setId(UUID id) {
      this.id = id;
    }
  }

  @RestController
  @RequestMapping("/test/exceptions")
  static class ExceptionTriggerController {

    @PostMapping("/validation")
    public void triggerValidation(@Valid @RequestBody DummyRequest request) {}

    @GetMapping("/not-found")
    public void triggerNotFound() {
      throw new ResourceNotFoundException("Entity not found with ID: 123");
    }

    @GetMapping("/business-rule")
    public void triggerBusinessRule() {
      throw new BusinessRuleException("Insufficient funds to complete operation");
    }

    @GetMapping("/invalid-request")
    public void triggerInvalidRequest() {
      throw new InvalidRequestException("Invalid parameter value provided");
    }

    @GetMapping("/unhandled")
    public void triggerUnhandled() {
      throw new RuntimeException("Unexpected fatal failure");
    }
  }

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new ExceptionTriggerController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void handlesMultipleBeanValidationErrorsWithBadRequestAndDetails() throws Exception {
    mockMvc
        .perform(
            post("/test/exceptions/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\" \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Bad Request")))
        .andExpect(jsonPath("$.message", is("Validation failed")))
        .andExpect(jsonPath("$.path", is("/test/exceptions/validation")))
        .andExpect(jsonPath("$.validationErrors.name", is("Field name cannot be blank")))
        .andExpect(jsonPath("$.validationErrors.id", is("Field ID cannot be null")));
  }

  @Test
  void acceptsRequestWhenBeanValidationConstraintsAreSatisfied() throws Exception {
    mockMvc
        .perform(
            post("/test/exceptions/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"valid\",\"id\":\"" + UUID.randomUUID() + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.validationErrors").doesNotExist());
  }

  @Test
  void handlesResourceNotFoundExceptionWithNotFound() throws Exception {
    mockMvc
        .perform(get("/test/exceptions/not-found"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.error", is("Not Found")))
        .andExpect(jsonPath("$.message", is("Entity not found with ID: 123")))
        .andExpect(jsonPath("$.path", is("/test/exceptions/not-found")));
  }

  @Test
  void handlesBusinessRuleExceptionWithUnprocessableContent() throws Exception {
    mockMvc
        .perform(get("/test/exceptions/business-rule"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.status", is(422)))
        .andExpect(jsonPath("$.error", is("Unprocessable Content")))
        .andExpect(jsonPath("$.message", is("Insufficient funds to complete operation")))
        .andExpect(jsonPath("$.path", is("/test/exceptions/business-rule")));
  }

  @Test
  void handlesInvalidRequestExceptionWithBadRequest() throws Exception {
    mockMvc
        .perform(get("/test/exceptions/invalid-request"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Bad Request")))
        .andExpect(jsonPath("$.message", is("Invalid parameter value provided")))
        .andExpect(jsonPath("$.path", is("/test/exceptions/invalid-request")));
  }

  @Test
  void handlesGenericExceptionWithInternalServerError() throws Exception {
    mockMvc
        .perform(get("/test/exceptions/unhandled"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.error", is("Internal Server Error")))
        .andExpect(jsonPath("$.message", containsString("unexpected internal server error")))
        .andExpect(jsonPath("$.path", is("/test/exceptions/unhandled")));
  }
}
