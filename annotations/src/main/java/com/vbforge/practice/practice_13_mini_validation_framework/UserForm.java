package com.vbforge.practice.practice_13_mini_validation_framework;

/**
 * A simple form/DTO annotated with custom constraint annotations.
 *
 * The {@link Validator} will inspect these annotations at runtime
 * and enforce the declared constraints on the field values.
 *
 * Constraints applied:
 *   username  — @NonNull + @MinLength(3)
 *   password  — @NonNull + @MinLength(8)
 *   age       — @Range(min=18, max=99)
 *   email     — @NonNull (no length constraint)
 */
public class UserForm {

    @NonNull(message = "username must not be null")
    @MinLength(3)
    private String username;

    @NonNull(message = "password must not be null")
    @MinLength(8)
    private String password;

    @Range(min = 18, max = 99)
    private int age;

    @NonNull(message = "email must not be null")
    private String email;

    public UserForm(String username, String password, int age, String email) {
        this.username = username;
        this.password = password;
        this.age      = age;
        this.email    = email;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int    getAge()      { return age; }
    public String getEmail()    { return email; }
}