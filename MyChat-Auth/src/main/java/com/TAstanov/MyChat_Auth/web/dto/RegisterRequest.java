package com.TAstanov.MyChat_Auth.web.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.regex.Pattern;

@Data
public class RegisterRequest {

    @NotBlank(message = "name must be not blank")
    private String name;

    @NotBlank(message = "name must be not blank")
    @Email(message = "it must have email pattern")
    private String email;

    @NotBlank(message = "tag must be not blank")
    private String tag;

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotBlank(message = "password confirmation must be not blank")
    private String passwordConfirmation;

    @AssertTrue(message = "Password and password confirmation must match")
    public boolean isPasswordMatching() {
        if (password == null || passwordConfirmation == null) {
            return false;
        }
        return password.equals(passwordConfirmation);
    }

    @AssertTrue(message = "Password should have length superior to 10, and contain at least one special symbol, one capital letter, and one digit")
    public boolean isPasswordValid() {
        if (this.password == null) return false;

        // Check for length, capital letter, special symbol, and digit
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();

        return password.length() >= 10 && hasUpperCase && hasDigit && hasSpecialChar;
    }
}
