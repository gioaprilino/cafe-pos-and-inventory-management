package com.terracafe.terracafe_backend.dto;

import java.time.LocalDateTime;

public class LoginResponse {

    private Long userId;
    private String username;
    private String roleId;
    private String roleName;
    private String message;
    private String token;
    private LocalDateTime loginTime;
    private LocalDateTime tokenExpiry;

    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(Long userId, String username, String roleId, String roleName, String message) {
        this.userId = userId;
        this.username = username;
        this.roleId = roleId;
        this.roleName = roleName;
        this.message = message;
        this.loginTime = LocalDateTime.now();
    }

    public LoginResponse(Long userId, String username, String roleId, String roleName, String token, String message) {
        this.userId = userId;
        this.username = username;
        this.roleId = roleId;
        this.roleName = roleName;
        this.token = token;
        this.message = message;
        this.loginTime = LocalDateTime.now();
        // Token expires in 24 hours
        this.tokenExpiry = LocalDateTime.now().plusHours(24);
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(LocalDateTime tokenExpiry) { this.tokenExpiry = tokenExpiry; }
}
