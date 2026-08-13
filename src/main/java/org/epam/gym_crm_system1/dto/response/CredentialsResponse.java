package org.epam.gym_crm_system1.dto.response;

public class CredentialsResponse {

    private String username;
    private String password;

    public CredentialsResponse() {
    }

    public CredentialsResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}