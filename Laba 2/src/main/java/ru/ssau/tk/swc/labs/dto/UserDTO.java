package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.*;

public class UserDTO {
    private Long id;
    private String name;
    private String login;
    private String email;
    private String role;

    public UserDTO() {}

    public UserDTO(Long id, String name, String login, String email, String role ) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.email = email;
        this.role=role;
    }
    public UserDTO(users entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.login = entity.getLogin();
        this.email = entity.getEmail();
        this.role = entity.getRole();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}