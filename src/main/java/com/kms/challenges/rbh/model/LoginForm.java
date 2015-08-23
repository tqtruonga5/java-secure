package com.kms.challenges.rbh.model;

import com.kms.challenges.rbh.annotation.FormField;
import com.kms.challenges.rbh.annotation.Require;

/**
 * @author tkhuu.
 */
public class LoginForm {
    @FormField("email")
    @Require(errorMessage = "Email is required")
    private String email;

    @FormField("password")
    @Require(errorMessage = "Password is required")
    private String password;

    public LoginForm() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
