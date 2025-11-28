package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.UserAuthDTO;
import ru.ssau.tk.swc.labs.dto.UserDTO;
import ru.ssau.tk.swc.labs.entity.users;
import ru.ssau.tk.swc.labs.service.UsersService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService service;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        logger.info("GET /api/users - Получение всех пользователей");
        List<UserDTO> users = service.findAllUsers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/users - Найдено {} пользователей", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Получение пользователя по ID", id);
        Optional<users> user = service.findUserById(id);
        if (user.isPresent()) {
            logger.info("GET /api/users/{} - Пользователь найден", id);
            return ResponseEntity.ok(new UserDTO(user.get()));
        } else {
            logger.warn("GET /api/users/{} - Пользователь не найден", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/login")
    public ResponseEntity<UserDTO> getUserByLogin(@RequestParam String login) {
        logger.info("GET /api/users/search/login - Поиск пользователя по логину: {}", login);
        Optional<users> user = service.findUserByLogin(login);
        if (user.isPresent()) {
            logger.info("GET /api/users/search/login - Пользователь с логином '{}' найден", login);
            return ResponseEntity.ok(new UserDTO(user.get()));
        } else {
            logger.warn("GET /api/users/search/login - Пользователь с логином '{}' не найден", login);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        logger.info("GET /api/users/search/email - Поиск пользователя по email: {}", email);
        Optional<users> user = service.findUserByEmail(email);
        if (user.isPresent()) {
            logger.info("GET /api/users/search/email - Пользователь с email '{}' найден", email);
            return ResponseEntity.ok(new UserDTO(user.get()));
        } else {
            logger.warn("GET /api/users/search/email - Пользователь с email '{}' не найден", email);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<UserDTO> authenticateUser(@RequestBody UserAuthDTO authDTO) {
        logger.info("POST /api/users/auth - Аутентификация пользователя с логином: {}", authDTO.getLogin());
        Optional<users> user = service.authenticateUser(authDTO.getLogin(), authDTO.getPassword());
        if (user.isPresent()) {
            logger.info("POST /api/users/auth - Пользователь '{}' успешно аутентифицирован", authDTO.getLogin());
            return ResponseEntity.ok(new UserDTO(user.get()));
        } else {
            logger.warn("POST /api/users/auth - Неудачная аутентификация для логина: {}", authDTO.getLogin());
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/check-login")
    public ResponseEntity<Boolean> checkLoginExists(@RequestParam String login) {
        logger.info("POST /api/users/check-login - Проверка существования логина: {}", login);
        boolean exists = service.isLoginExists(login);
        logger.info("POST /api/users/check-login - Логин '{}' {}существует", login, exists ? "" : "не ");
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        logger.info("POST /api/users/check-email - Проверка существования email: {}", email);
        boolean exists = service.isEmailExists(email);
        logger.info("POST /api/users/check-email - Email '{}' {}существует", email, exists ? "" : "не ");
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody users user) {
        logger.info("POST /api/users/register - Регистрация нового пользователя с логином: {}", user.getLogin());

        if (service.isLoginExists(user.getLogin())) {
            logger.warn("POST /api/users/register - Логин '{}' уже существует", user.getLogin());
            return ResponseEntity.badRequest().build();
        }
        if (service.isEmailExists(user.getEmail())) {
            logger.warn("POST /api/users/register - Email '{}' уже существует", user.getEmail());
            return ResponseEntity.badRequest().build();
        }

        users savedUser = service.save(user);
        logger.info("POST /api/users/register - Пользователь успешно зарегистрирован с ID: {}", savedUser.getId());
        return ResponseEntity.ok(new UserDTO(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody users user) {
        logger.info("PUT /api/users/{} - Обновление данных пользователя", id);
        user.setId(id);
        users updated = service.save(user);
        logger.info("PUT /api/users/{} - Данные пользователя успешно обновлены", id);
        return ResponseEntity.ok(new UserDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Удаление пользователя", id);
        service.deleteById(id);
        logger.info("DELETE /api/users/{} - Пользователь успешно удален", id);
        return ResponseEntity.ok().build();
    }
}