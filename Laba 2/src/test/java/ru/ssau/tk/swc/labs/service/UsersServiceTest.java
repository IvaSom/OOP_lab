package ru.ssau.tk.swc.labs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.users;
import ru.ssau.tk.swc.labs.repository.UserRepository;

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
class UsersServiceTest {

    @Autowired
    private UserRepository userRepository;

    private UsersService usersService;

    private users user1;
    private users user2;
    private users user3;

    @BeforeEach
    void setUp() {
        usersService = new UsersService(userRepository);

        userRepository.deleteAll();

        user1 = new users("Иван Иванов", "ivanov", "ivanov@mail.ru", "password123");
        user2 = new users("Петр Петров", "petrov", "petrov@mail.ru", "qwerty");
        user3 = new users("Сергей Сергеев", "sergeev", "sergeev@mail.ru", "123456");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);
    }

    @Test
    void testFindUserByLogin() {
        Optional<users> result = usersService.findUserByLogin("ivanov");

        assertTrue(result.isPresent(), "Пользователь должен быть найден по логину");
        assertEquals("ivanov", result.get().getLogin(), "Логин должен быть 'ivanov'");
        assertEquals("Иван Иванов", result.get().getName(), "Имя должно быть 'Иван Иванов'");
    }

    @Test
    void testFindUserByLoginNotFound() {
        Optional<users> result = usersService.findUserByLogin("unknown");

        assertFalse(result.isPresent(), "Несуществующий пользователь не должен быть найден по логину");
    }

    @Test
    void testFindUserByEmail() {
        Optional<users> result = usersService.findUserByEmail("petrov@mail.ru");

        assertTrue(result.isPresent(), "Пользователь должен быть найден по email");
        assertEquals("petrov@mail.ru", result.get().getEmail(), "Email должен быть 'petrov@mail.ru'");
        assertEquals("Петр Петров", result.get().getName(), "Имя должно быть 'Петр Петров'");
    }

    @Test
    void testFindUserByEmailNotFound() {
        Optional<users> result = usersService.findUserByEmail("unknown@mail.ru");

        assertFalse(result.isPresent(), "Несуществующий пользователь не должен быть найден по email");
    }

    @Test
    void testFindUserById() {
        Optional<users> result = usersService.findUserById(user1.getId());

        assertTrue(result.isPresent(), "Пользователь должен быть найден по ID");
        assertEquals(user1.getId(), result.get().getId(), "ID должен совпадать");
        assertEquals("ivanov", result.get().getLogin(), "Логин должен быть 'ivanov'");
    }

    @Test
    void testFindUserByIdNotFound() {
        Optional<users> result = usersService.findUserById(52L);

        assertFalse(result.isPresent(), "Несуществующий пользователь не должен быть найден по ID");
    }

    @Test
    void testAuthenticateUserSuccess() {
        Optional<users> result = usersService.authenticateUser("ivanov", "password123");

        assertTrue(result.isPresent(), "Аутентификация должна быть успешной");
        assertEquals("ivanov", result.get().getLogin(), "Логин должен быть 'ivanov'");
    }

    @Test
    void testAuthenticateUserWrongPassword() {
        Optional<users> result = usersService.authenticateUser("ivanov", "wrongpassword");

        assertFalse(result.isPresent(), "Аутентификация должна провалиться при неверном пароле");
    }

    @Test
    void testAuthenticateUserWrongLogin() {
        Optional<users> result = usersService.authenticateUser("unknown", "password123");

        assertFalse(result.isPresent(), "Аутентификация должна провалиться при неверном логине");
    }

    @Test
    void testFindMultipleWithSortingByNameAsc() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{}, "name", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 пользователя");

        assertEquals("Иван Иванов", result.get(0).getName(), "Первый пользователь должен быть 'Иван Иванов'");
        assertEquals("Петр Петров", result.get(1).getName(), "Второй пользователь должен быть 'Петр Петров'");
        assertEquals("Сергей Сергеев", result.get(2).getName(), "Третий пользователь должен быть 'Сергей Сергеев'");
    }

    @Test
    void testFindMultipleWithSortingByNameDesc() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{}, "name", "desc");

        assertEquals(3, result.size(), "Должно быть найдено 3 пользователя");

        assertEquals("Сергей Сергеев", result.get(0).getName(), "Первый пользователь должен быть 'Сергей Сергеев'");
        assertEquals("Петр Петров", result.get(1).getName(), "Второй пользователь должен быть 'Петр Петров'");
        assertEquals("Иван Иванов", result.get(2).getName(), "Третий пользователь должен быть 'Иван Иванов'");
    }

    @Test
    void testFindMultipleWithSortingByLoginAsc() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{}, "login", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 пользователя");

        assertEquals("ivanov", result.get(0).getLogin(), "Первый пользователь должен иметь логин 'ivanov'");
        assertEquals("petrov", result.get(1).getLogin(), "Второй пользователь должен иметь логин 'petrov'");
        assertEquals("sergeev", result.get(2).getLogin(), "Третий пользователь должен иметь логин 'sergeev'");
    }

    @Test
    void testFindMultipleWithSortingByEmailAsc() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{}, "email", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 пользователя");

        assertEquals("ivanov@mail.ru", result.get(0).getEmail(), "Первый пользователь должен иметь email 'ivanov@mail.ru'");
        assertEquals("petrov@mail.ru", result.get(1).getEmail(), "Второй пользователь должен иметь email 'petrov@mail.ru'");
        assertEquals("sergeev@mail.ru", result.get(2).getEmail(), "Третий пользователь должен иметь email 'sergeev@mail.ru'");
    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{}, "invalid", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 пользователя");

        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(1).getId() < result.get(2).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithNameFiltering() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{"Иван Иванов", "Петр Петров"}, "name", "asc");

        assertEquals(2, result.size(), "Должно быть найдено 2 пользователя");

        assertTrue(result.stream().allMatch(u -> u.getName().equals("Иван Иванов") || u.getName().equals("Петр Петров")),
                "Все пользователи должны иметь имя 'Иван Иванов' или 'Петр Петров'");

        assertEquals("Иван Иванов", result.get(0).getName(), "Первый пользователь должен быть 'Иван Иванов'");
        assertEquals("Петр Петров", result.get(1).getName(), "Второй пользователь должен быть 'Петр Петров'");
    }

    @Test
    void testFindMultipleWithSingleNameFilter() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{"Сергей Сергеев"}, "name", "asc");

        assertEquals(1, result.size(), "Должен быть найден 1 пользователь");
        assertEquals("Сергей Сергеев", result.get(0).getName(), "Пользователь должен быть 'Сергей Сергеев'");
    }

    @Test
    void testFindMultipleWithEmptyNameFilter() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{}, "name", "asc");

        assertEquals(3, result.size(), "Должно быть найдено все 3 пользователя при пустом фильтре");
    }

    @Test
    void testFindMultipleWithNullNameFilter() {
        List<users> result = usersService.findMultipleWithSorting(
                null, "name", "asc");

        assertEquals(3, result.size(), "Должно быть найдено все 3 пользователя при null фильтре");
    }

    @Test
    void testFindMultipleWithNonExistingNameFilter() {
        List<users> result = usersService.findMultipleWithSorting(
                new String[]{"Несуществующий"}, "name", "asc");

        assertEquals(0, result.size(), "Не должно быть найдено пользователей при фильтре по несуществующему имени");
    }

    @Test
    void testFindAllUsers() {
        List<users> result = usersService.findAllUsers();

        assertEquals(3, result.size(), "Должно быть возвращено 3 пользователя");

        List<String> userLogins = result.stream().map(users::getLogin).toList();
        assertTrue(userLogins.contains("ivanov"), "Должен присутствовать пользователь 'ivanov'");
        assertTrue(userLogins.contains("petrov"), "Должен присутствовать пользователь 'petrov'");
        assertTrue(userLogins.contains("sergeev"), "Должен присутствовать пользователь 'sergeev'");
    }

    @Test
    void testIsLoginExists() {
        boolean result = usersService.isLoginExists("ivanov");

        assertTrue(result, "Логин 'ivanov' должен существовать");
    }

    @Test
    void testIsLoginNotExists() {
        boolean result = usersService.isLoginExists("unknown");

        assertFalse(result, "Логин 'unknown' не должен существовать");
    }

    @Test
    void testIsEmailExists() {
        boolean result = usersService.isEmailExists("petrov@mail.ru");

        assertTrue(result, "Email 'petrov@mail.ru' должен существовать");
    }

    @Test
    void testIsEmailNotExists() {
        boolean result = usersService.isEmailExists("unknown@mail.ru");

        assertFalse(result, "Email 'unknown@mail.ru' не должен существовать");
    }

    @Test
    void testSave() {
        users newUser = new users("Новый Пользователь", "newuser", "newuser@mail.ru", "newpass");

        users result = usersService.save(newUser);

        assertNotNull(result, "Сохраненный пользователь не должен быть null");
        assertEquals("Новый Пользователь", result.getName(), "Имя должно быть 'Новый Пользователь'");
        assertEquals("newuser", result.getLogin(), "Логин должен быть 'newuser'");
        assertEquals("newuser@mail.ru", result.getEmail(), "Email должен быть 'newuser@mail.ru'");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<users> found = usersService.findUserByLogin("newuser");
        assertTrue(found.isPresent(), "Новый пользователь должен быть найден в БД");
        assertEquals("newuser", found.get().getLogin(), "Логин найденного пользователя должен быть 'newuser'");
    }

    @Test
    void testDeleteById() {
        Optional<users> beforeDelete = usersService.findUserById(user1.getId());
        assertTrue(beforeDelete.isPresent(), "Пользователь должен существовать до удаления");

        usersService.deleteById(user1.getId());

        Optional<users> afterDelete = usersService.findUserById(user1.getId());
        assertFalse(afterDelete.isPresent(), "Пользователь должен быть удален");

        List<users> remainingUsers = usersService.findAllUsers();
        assertEquals(2, remainingUsers.size(), "После удаления должно остаться 2 пользователя");
    }
}