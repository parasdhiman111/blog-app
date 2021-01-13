package com.paras.boot.bloggingapplication.dto;


import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author 1460344
 */
public class LoginDto {

    @NotBlank(message = "should not be empty")
    @Length(max = 25)
    @Valid
    String email;
    @NotBlank(message = "should not be empty")
    @Length(max = 25)
    @Valid
    String password;

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
