package com.edtech.crm.dto;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private AdminProfileResponse admin;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, long expiresIn, AdminProfileResponse admin) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.admin = admin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public AdminProfileResponse getAdmin() {
        return admin;
    }

    public void setAdmin(AdminProfileResponse admin) {
        this.admin = admin;
    }
}
