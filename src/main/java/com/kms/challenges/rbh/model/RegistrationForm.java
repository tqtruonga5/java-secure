package com.kms.challenges.rbh.model;

import com.kms.challenges.rbh.annotation.FormField;
import com.kms.challenges.rbh.annotation.MatchWith;
import com.kms.challenges.rbh.annotation.Require;

/**
 * @author tkhuu.
 */
public class RegistrationForm {
    @FormField("email")
    @Require(errorMessage = "Email is required")
    private String email;
    @FormField("first_name")
    @Require(errorMessage = "First name is required")
    private String firstName;
    @FormField("last_name")
    @Require(errorMessage = "Last name is required")
    private String lastName;
    @FormField("password")
    @Require(errorMessage = "Password is required")
    @MatchWith(fieldName = "password_confirm", errorMessage = "Password not match")
    private String password;
    @FormField("password_confirm")
    @Require(errorMessage = "Password confirm is required")
    private String passwordConfirm;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
