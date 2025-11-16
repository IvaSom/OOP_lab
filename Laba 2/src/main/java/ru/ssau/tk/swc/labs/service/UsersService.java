package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.users;
import ru.ssau.tk.swc.labs.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    private final UserRepository userRepository;

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<users> findUserByLogin(String login) {
        logger.info("Поиск пользователя по логину: {}", login);
        Optional<users> result = userRepository.findByLogin(login);
        logger.info("Поиск по логину завершен. Найдено: {}", result.isPresent() ? "1 пользователь" : "0 пользователей");
        return result;
    }

    public Optional<users> findUserByEmail(String email) {
        logger.info("Поиск пользователя по email: {}", email);
        Optional<users> result = userRepository.findByEmail(email);
        logger.info("Поиск по email завершен. Найдено: {}", result.isPresent() ? "1 пользователь" : "0 пользователей");
        return result;
    }

    public Optional<users> findUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        Optional<users> result = userRepository.findById(id);
        logger.info("Поиск по ID завершен. Найдено: {}", result.isPresent() ? "1 пользователь" : "0 пользователей");
        return result;
    }

    public Optional<users> authenticateUser(String login, String password) {
        logger.info("Аутентификация пользователя: login={}", login);
        Optional<users> result = userRepository.findByLoginAndPassword(login, password);
        if (result.isPresent()) {
            logger.info("Аутентификация успешна для пользователя: {}", login);
        } else {
            logger.warn("Аутентификация не удалась для пользователя: {}", login);
        }
        return result;
    }

    public List<users> findAllUsers() {
        logger.info("Получение всех пользователей");
        List<users> result = userRepository.findAll();
        logger.info("Найдено всех пользователей: {}", result.size());
        return result;
    }

    public List<users> findMultipleWithSorting(String[] names, String sortBy, String direction) {
        logger.info("Множественный поиск пользователей: имена {}, сортировка по {} {}",
                names != null ? String.join(", ", names) : "все", sortBy, direction);

        List<users> allUsers = userRepository.findAll();

        List<users> filtered = allUsers.stream()
                .filter(u -> names == null || names.length == 0 || Arrays.asList(names).contains(u.getName()))
                .toList();

        Comparator<users> comparator = createComparator(sortBy, direction);

        List<users> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск пользователей завершен. Найдено пользователей: {}", result.size());
        return result;
    }



    public boolean isLoginExists(String login) {
        logger.info("Проверка существования логина: {}", login);
        boolean exists = userRepository.findByLogin(login).isPresent();
        logger.info("Логин {} существует: {}", login, exists);
        return exists;
    }

    public boolean isEmailExists(String email) {
        logger.info("Проверка существования email: {}", email);
        boolean exists = userRepository.findByEmail(email).isPresent();
        logger.info("Email {} существует: {}", email, exists);
        return exists;
    }

    private Comparator<users> createComparator(String sortBy, String direction) {
        Comparator<users> comparator = switch(sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(users::getName);
            case "login" -> Comparator.comparing(users::getLogin);
            case "email" -> Comparator.comparing(users::getEmail);
            default -> Comparator.comparing(users::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}