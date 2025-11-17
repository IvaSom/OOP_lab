package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.analFun;

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
class AnalFunRepositoryTest {

    @Autowired
    private AnalFunRepository analFunRepository;

    @BeforeEach
    void setUp() {
        //очистка бд
        analFunRepository.deleteAll();
    }

    @Test
    void testGenerateFindDelete() {

        analFun function1 = new analFun("sin", 1);
        analFun function2 = new analFun("cos", 2);
        analFun function3 = new analFun("x^2", 3);

        analFun saved1 = analFunRepository.save(function1);
        analFun saved2 = analFunRepository.save(function2);
        analFun saved3 = analFunRepository.save(function3);

        assertEquals(3, analFunRepository.count(), "должно быть 3 функции в базе");

        
        Optional<analFun> foundSin = analFunRepository.findByName("sin");
        assertTrue(foundSin.isPresent(), "sin должен быть");
        assertEquals(1, foundSin.get().getType(), "Тип sin должен быть 1");

        Optional<analFun> foundCos = analFunRepository.findByName("cos");
        assertTrue(foundCos.isPresent(), "cos должен быть");
        assertEquals(2, foundCos.get().getType(), "Тип cos должен быть 2");

        Optional<analFun> foundSquare = analFunRepository.findByName("x^2");
        assertTrue(foundSquare.isPresent(), "x^2 должен быть");
        assertEquals(3, foundSquare.get().getType(), "Тип x^2 должен быть 3");

        Optional<analFun> notFound = analFunRepository.findByName("qwerty");
        assertFalse(notFound.isPresent(), "Несуществующая функция не должна быть найдена");

        List<analFun> allFunctions = analFunRepository.findAll();
        assertEquals(3, allFunctions.size(), "Должны быть найдены все 3 функции");

        //удаление
        analFunRepository.deleteById(foundSin.get().getId());
        Optional<analFun> deletedSin = analFunRepository.findByName("sin");
        assertFalse(deletedSin.isPresent(), "sin должен быть удален");

        assertEquals(2, analFunRepository.count(), "После удаления должно остаться 2 функции");

        analFunRepository.delete(foundCos.get());
        Optional<analFun> deletedCos = analFunRepository.findByName("cos");
        assertFalse(deletedCos.isPresent(), "cos должен быть удален");

        assertEquals(1, analFunRepository.count(), "Должна остаться только 1 функция");
        Optional<analFun> remainingFunction = analFunRepository.findByName("x^2");
        assertTrue(remainingFunction.isPresent(), "x^2 должен остаться в базе");

        analFunRepository.delete(foundSquare.get());
        Optional<analFun> deletedSquare = analFunRepository.findByName("x^2");
        assertFalse(deletedSquare.isPresent(), "x^2 должен быть удален");

        analFunRepository.deleteAll();
    }

}