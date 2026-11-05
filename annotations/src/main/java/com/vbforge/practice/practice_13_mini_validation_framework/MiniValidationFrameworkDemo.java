package com.vbforge.practice.practice_13_mini_validation_framework;

import java.util.List;

/**
 * Demonstrates custom annotations in a practical scenario:
 * a miniature runtime validation framework.
 *
 * Real-world use cases for custom annotations:
 *   - Validation (Hibernate Validator, Jakarta Bean Validation)
 *   - Dependency injection (@Autowired, @Inject)
 *   - Routing (@GetMapping, @PostMapping in Spring)
 *   - Serialization control (@JsonIgnore, @JsonProperty in Jackson)
 *   - Security (@PreAuthorize, @RolesAllowed)
 *   - Caching (@Cacheable)
 *   - Test configuration (@BeforeEach, @ParameterizedTest)
 *
 * This demo builds a simple {@link Validator} that:
 *   1. Iterates over all fields of a given object via reflection
 *   2. Reads constraint annotations (@NonNull, @MinLength, @Range)
 *   3. Evaluates each constraint against the field's value
 *   4. Collects and returns validation errors
 *
 * This is the exact pattern Hibernate Validator (Jakarta Bean Validation)
 * uses internally — just far more sophisticated.
 */
public class MiniValidationFrameworkDemo {

    public static void main(String[] args) throws IllegalAccessException {
        System.out.println("=== Custom Annotations — Mini Validation Framework ===\n");

        // Valid user
        UserForm validUser = new UserForm("alice", "secret123", 25, "alice@example.com");
        List<String> errors = Validator.validate(validUser);
        System.out.println("Valid user errors    : " + (errors.isEmpty() ? "none" : errors));

        // Invalid user — blank username, short password, age out of range
        UserForm invalidUser = new UserForm("", "abc", 15, null);
        List<String> invalidErrors = Validator.validate(invalidUser);
        System.out.println("Invalid user errors  :");
        invalidErrors.forEach(e -> System.out.println("  - " + e));
    }
}
