package ru.ssau.tk.swc.labs.dto;

public class UserAuthDTO {
    private String login;
    private String password;

    public UserAuthDTO() {}

    public UserAuthDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}