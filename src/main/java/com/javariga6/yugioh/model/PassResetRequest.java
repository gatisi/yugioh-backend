package com.javariga6.yugioh.model;

import javax.validation.constraints.Email;

public class PassResetRequest {
    private String host;
    @Email
    private String email;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "PassResetRequest{" +
                "host='" + host + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
