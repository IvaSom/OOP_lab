package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.User;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDTO {
    private Long id;
    private String login;
    private String email;
    private String name;
    private String password;

    private static final Logger logger = LoggerFactory.getLogger(UserDTO.class);

    public UserDTO() {
        logger.info("Создан пустой UserDTO");
    }

    public UserDTO(Long id, String login, String email, String name, String password) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.name = name;
        this.password = password;
        logger.info("Создан UserDTO по значениям");
    }

    public static UserDTO fromEntity(User entity) {
        if (entity == null) {
            return null;
        }
        UserDTO temp = new UserDTO(entity.getId(), entity.getLogin(), entity.getEmail(), entity.getName(), entity.getPassword());
        logger.info("Создан UserDTO по entity");
        return temp;
    }

    public User toEntity() {
        User entity = new User();
        entity.setId(this.id);
        entity.setLogin(this.login);
        entity.setEmail(this.email);
        entity.setName(this.name);
        entity.setPassword(this.password);
        logger.info("UserDTO переведен в entity");
        return entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        logger.info("Производится сравнение UserDTO");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO that = (UserDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(login, that.login) &&
                Objects.equals(email, that.email) &&
                Objects.equals(name, that.name) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        logger.info("Возвращается хэш-код UserDTO");
        return Objects.hash(id, login, email, name, password);
    }
}