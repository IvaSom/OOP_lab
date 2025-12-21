package ru.ssau.tk.swc.labs.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.users;
import ru.ssau.tk.swc.labs.service.UsersService;
import org.springframework.security.core.Authentication;
import java.util.Collections;

import org.springframework.http.HttpStatus;

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
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserCreateDTO userCreateDTO) {
        logger.info("POST /api/users/register - Регистрация нового пользователя с логином: {}", userCreateDTO.getLogin());

        if (service.isLoginExists(userCreateDTO.getLogin())) {
            logger.warn("POST /api/users/register - Логин '{}' уже существует", userCreateDTO.getLogin());
            return ResponseEntity.badRequest().build();
        }
        if (service.isEmailExists(userCreateDTO.getEmail())) {
            logger.warn("POST /api/users/register - Email '{}' уже существует", userCreateDTO.getEmail());
            return ResponseEntity.badRequest().build();
        }

        users user = new users();
        user.setName(userCreateDTO.getName());
        user.setLogin(userCreateDTO.getLogin());
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(userCreateDTO.getPassword());
        user.setRole(userCreateDTO.getRole());

        users savedUser = service.save(user);
        logger.info("POST /api/users/register - Пользователь успешно зарегистрирован с ID: {}", savedUser.getId());
        return ResponseEntity.ok(new UserDTO(savedUser));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserAuthDTO authRequest, HttpServletRequest request) {
        logger.info("POST /api/users/auth - Аутентификация пользователя с логином: {}", authRequest.getLogin());

        // Проверка наличия логина и пароля
        if (authRequest.getLogin() == null || authRequest.getLogin().trim().isEmpty()) {
            logger.warn("POST /api/users/auth - Логин не указан");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (authRequest.getPassword() == null || authRequest.getPassword().trim().isEmpty()) {
            logger.warn("POST /api/users/auth - Пароль не указан для пользователя '{}'", authRequest.getLogin());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<users> userOptional = service.findUserByLogin(authRequest.getLogin());

        if (userOptional.isEmpty()) {
            logger.warn("POST /api/users/auth - Пользователь с логином '{}' не найден", authRequest.getLogin());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        users user = userOptional.get();

        // Проверка пароля (безопасность: в реальном приложении используйте шифрование!)
        if (!user.getPassword().equals(authRequest.getPassword())) {
            logger.warn("POST /api/users/auth - Неверный пароль для пользователя '{}'", authRequest.getLogin());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ✅ СОЗДАЁМ АВТОРИЗАЦИЮ ДЛЯ SPRING SECURITY
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getLogin(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );

// ✅ УСТАНАВЛИВАЕМ АВТОРИЗАЦИЮ В КОНТЕКСТ БЕЗОПАСНОСТИ
        SecurityContextHolder.getContext().setAuthentication(authentication);

// ✅ СОЗДАЁМ СЕССИЮ
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        logger.info("✅ Пользователь '{}' аутентифицирован. Роль: {}, Сессия: {}",
                user.getLogin(),
                user.getRole(),
                session.getId());

        return ResponseEntity.ok(new UserDTO(user));
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Попытка получить текущего пользователя без аутентификации");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String login = authentication.getName();
        logger.info("Получение текущего пользователя: {}", login);

        Optional<users> user = service.findUserByLogin(login);
        return user.map(u -> ResponseEntity.ok(new UserDTO(u)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("POST /api/users/logout - Выход пользователя из системы");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            logger.info("Сессия инвалидирована: {}", session.getId());
        }
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        logger.info("Пользователь вышел из системы");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody users user) {
        logger.info("PUT /api/users/{} - Обновление данных пользователя", id);
        user.setId(id);
        users updated = service.save(user);
        logger.info("PUT /api/users/{} - Данные пользователя успешно обновлены", id);
        return ResponseEntity.ok(new UserDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Удаление пользователя", id);
        service.deleteById(id);
        logger.info("DELETE /api/users/{} - Пользователь успешно удален", id);
        return ResponseEntity.ok().build();
    }
}