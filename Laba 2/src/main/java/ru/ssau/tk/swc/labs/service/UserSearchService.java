package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSearchService {
    private static final Logger logger = LoggerFactory.getLogger(UserSearchService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired // или даже можно не указывать с одним конструктором
    public UserSearchService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                logger.info("Пользователь найден: {}", user.get().getLogin());
            } else {
                logger.warn("Пользователь с ID {} не найден", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по ID: {}", id, e);
            throw e;
        }
    }

}
