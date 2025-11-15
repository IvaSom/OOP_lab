package ru.ssau.tk.swc.labs.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserDTO {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 1, max = 50, message = "Имя должно быть от 1 до 50 символов")
    private String name;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 1, max = 50, message = "Логин должен быть от 1 до 50 символов")
    private String login;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть валидным")
    @Size(max = 100, message = "Email должен быть не более 100 символов")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 1, max = 100, message = "Пароль должен быть от 1 до 100 символов")
    private String password;

    private static final Logger logger = LoggerFactory.getLogger(CreateUserDTO.class);

    public CreateUserDTO() {
        logger.info("Создан пустой CreateUserDTO");
    }

    public CreateUserDTO(String name, String login, String email, String password) {
        this.name = name;
        this.login = login;
        this.email = email;
        this.password = password;
        logger.info("Создан CreateUserDTO по значениям");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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