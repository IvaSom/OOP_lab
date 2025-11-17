package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.tabFun;

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
class TabFunRepositoryTest {

    @Autowired
    private TabFunRepository tabFunRepository;

    @BeforeEach
    void setUp() {
        tabFunRepository.deleteAll();
    }

    @Test
    void testGenerateFindDelete() {

        tabFun function1 = new tabFun();
        function1.setName("table_function_1");

        tabFun function2 = new tabFun();
        function2.setName("table_function_2");

        tabFun function3 = new tabFun();
        function3.setName("table_function_3");

        tabFun saved1 = tabFunRepository.save(function1);
        tabFun saved2 = tabFunRepository.save(function2);
        tabFun saved3 = tabFunRepository.save(function3);

        assertEquals(3, tabFunRepository.count(), "должно быть 3 функции в базе");


        List<tabFun> foundFunction1 = tabFunRepository.findByName("table_function_1");
        assertFalse(foundFunction1.isEmpty(), "table_function_1 должен быть");
        assertEquals("table_function_1", foundFunction1.get(0).getName(), "Имя должно быть table_function_1");

        List<tabFun> foundFunction2 = tabFunRepository.findByName("table_function_2");
        assertFalse(foundFunction2.isEmpty(), "table_function_2 должен быть");
        assertEquals("table_function_2", foundFunction2.get(0).getName(), "Имя должно быть table_function_2");

        List<tabFun> foundFunction3 = tabFunRepository.findByName("table_function_3");
        assertFalse(foundFunction3.isEmpty(), "table_function_3 должен быть");
        assertEquals("table_function_3", foundFunction3.get(0).getName(), "Имя должно быть table_function_3");

        List<tabFun> notFound = tabFunRepository.findByName("qwerty");
        assertTrue(notFound.isEmpty(), "Несуществующая функция не должна быть найдена");

        List<tabFun> allFunctions = tabFunRepository.findAll();
        assertEquals(3, allFunctions.size(), "Должны быть найдены все 3 функции");

        //удаление
        tabFunRepository.deleteById(foundFunction1.get(0).getId());
        List<tabFun> deletedFunction1 = tabFunRepository.findByName("table_function_1");
        assertTrue(deletedFunction1.isEmpty(), "table_function_1 должен быть удален");

        assertEquals(2, tabFunRepository.count(), "После удаления должно остаться 2 функции");

        tabFunRepository.delete(foundFunction2.get(0));
        List<tabFun> deletedFunction2 = tabFunRepository.findByName("table_function_2");
        assertTrue(deletedFunction2.isEmpty(), "table_function_2 должен быть удален");

        assertEquals(1, tabFunRepository.count(), "Должна остаться только 1 функция");
        List<tabFun> remainingFunction = tabFunRepository.findByName("table_function_3");
        assertFalse(remainingFunction.isEmpty(), "table_function_3 должен остаться в базе");

        tabFunRepository.delete(foundFunction3.get(0));
        List<tabFun> deletedFunction3 = tabFunRepository.findByName("table_function_3");
        assertTrue(deletedFunction3.isEmpty(), "table_function_3 должен быть удален");

        tabFunRepository.deleteAll();
    }

}