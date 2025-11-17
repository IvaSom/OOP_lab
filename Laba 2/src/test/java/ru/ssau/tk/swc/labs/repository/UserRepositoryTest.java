package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.users;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        //очистка бд
        userRepository.deleteAll();
    }

    @Test
    void testGenerateFindDelete() {

        users user1 = new users("Иван Иванов", "ivanov", "ivanov@mail.ru", "password123");
        users user2 = new users("Петр Петров", "petrov", "petrov@mail.ru", "qwerty456");
        users user3 = new users("Мария Сидорова", "sidorova", "sidorova@mail.ru", "secret789");

        users saved1 = userRepository.save(user1);
        users saved2 = userRepository.save(user2);
        users saved3 = userRepository.save(user3);

        assertEquals(3, userRepository.count(), "должно быть 3 пользователя в базе");


        Optional<users> foundByLogin1 = userRepository.findByLogin("ivanov");
        assertTrue(foundByLogin1.isPresent(), "Пользователь с логином ivanov должен быть");
        assertEquals("Иван Иванов", foundByLogin1.get().getName(), "Имя должно быть Иван Иванов");
        assertEquals("ivanov@mail.ru", foundByLogin1.get().getEmail(), "Email должен быть ivanov@mail.ru");

        Optional<users> foundByLogin2 = userRepository.findByLogin("petrov");
        assertTrue(foundByLogin2.isPresent(), "Пользователь с логином petrov должен быть");
        assertEquals("Петр Петров", foundByLogin2.get().getName(), "Имя должно быть Петр Петров");

        Optional<users> foundByLogin3 = userRepository.findByLogin("sidorova");
        assertTrue(foundByLogin3.isPresent(), "Пользователь с логином sidorova должен быть");
        assertEquals("Мария Сидорова", foundByLogin3.get().getName(), "Имя должно быть Мария Сидорова");

        Optional<users> foundByEmail1 = userRepository.findByEmail("ivanov@mail.ru");
        assertTrue(foundByEmail1.isPresent(), "Пользователь с email ivanov@mail.ru должен быть");
        assertEquals("ivanov", foundByEmail1.get().getLogin(), "Логин должен быть ivanov");

        Optional<users> foundByEmail2 = userRepository.findByEmail("petrov@mail.ru");
        assertTrue(foundByEmail2.isPresent(), "Пользователь с email petrov@mail.ru должен быть");
        assertEquals("petrov", foundByEmail2.get().getLogin(), "Логин должен быть petrov");

        Optional<users> foundByLoginAndPassword1 = userRepository.findByLoginAndPassword("ivanov", "password123");
        assertTrue(foundByLoginAndPassword1.isPresent(), "Пользователь с логином ivanov и паролем password123 должен быть найден");
        assertEquals("Иван Иванов", foundByLoginAndPassword1.get().getName(), "Имя должно быть Иван Иванов");

        Optional<users> foundByLoginAndPassword2 = userRepository.findByLoginAndPassword("petrov", "qwerty456");
        assertTrue(foundByLoginAndPassword2.isPresent(), "Пользователь с логином petrov и паролем qwerty456 должен быть найден");

        Optional<users> wrongPassword = userRepository.findByLoginAndPassword("ivanov", "wrongpassword");
        assertFalse(wrongPassword.isPresent(), "Пользователь с неправильным паролем не должен быть найден");

        Optional<users> notFoundByLogin = userRepository.findByLogin("nonexistent");
        assertFalse(notFoundByLogin.isPresent(), "Несуществующий пользователь не должен быть найден");

        Optional<users> notFoundByEmail = userRepository.findByEmail("nonexistent@mail.ru");
        assertFalse(notFoundByEmail.isPresent(), "Несуществующий email не должен быть найден");

        List<users> allUsers = userRepository.findAll();
        assertEquals(3, allUsers.size(), "Должны быть найдены все 3 пользователя");

        //удаление
        userRepository.deleteById(foundByLogin1.get().getId());
        Optional<users> deletedUser1 = userRepository.findByLogin("ivanov");
        assertFalse(deletedUser1.isPresent(), "Пользователь ivanov должен быть удален");

        assertEquals(2, userRepository.count(), "После удаления должно остаться 2 пользователя");

        userRepository.delete(foundByLogin2.get());
        Optional<users> deletedUser2 = userRepository.findByLogin("petrov");
        assertFalse(deletedUser2.isPresent(), "Пользователь petrov должен быть удален");

        assertEquals(1, userRepository.count(), "Должен остаться только 1 пользователь");
        Optional<users> remainingUser = userRepository.findByLogin("sidorova");
        assertTrue(remainingUser.isPresent(), "Пользователь sidorova должен остаться в базе");

        userRepository.delete(foundByLogin3.get());
        Optional<users> deletedUser3 = userRepository.findByLogin("sidorova");
        assertFalse(deletedUser3.isPresent(), "Пользователь sidorova должен быть удален");

        userRepository.deleteAll();
    }

}