package com.nessathon.dto;

public class UserAuthenticationStatus {

    private String status;

    public UserAuthenticationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
