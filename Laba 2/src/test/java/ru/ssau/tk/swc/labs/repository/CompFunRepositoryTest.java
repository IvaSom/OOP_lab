package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.compFun;

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
class CompFunRepositoryTest {

    @Autowired
    private CompFunRepository compFunRepository;

    @BeforeEach
    void setUp() {
        //очистка бд
        compFunRepository.deleteAll();
    }

    @Test
    void testGenerateFindDelete() {

        compFun function1 = new compFun("composite_sin_cos");
        compFun function2 = new compFun("composite_sqr_sin");
        compFun function3 = new compFun("composite_log_exp");

        compFun saved1 = compFunRepository.save(function1);
        compFun saved2 = compFunRepository.save(function2);
        compFun saved3 = compFunRepository.save(function3);

        assertEquals(3, compFunRepository.count(), "должно быть 3 функции в базе");


        List<compFun> foundSinCos = compFunRepository.findByName("composite_sin_cos");
        assertFalse(foundSinCos.isEmpty(), "composite_sin_cos должен быть");
        assertEquals("composite_sin_cos", foundSinCos.get(0).getName(), "Имя должно быть composite_sin_cos");

        List<compFun> foundSqrSin = compFunRepository.findByName("composite_sqr_sin");
        assertFalse(foundSqrSin.isEmpty(), "composite_sqr_sin должен быть");
        assertEquals("composite_sqr_sin", foundSqrSin.get(0).getName(), "Имя должно быть composite_sqr_sin");

        List<compFun> foundLogExp = compFunRepository.findByName("composite_log_exp");
        assertFalse(foundLogExp.isEmpty(), "composite_log_exp должен быть");
        assertEquals("composite_log_exp", foundLogExp.get(0).getName(), "Имя должно быть composite_log_exp");

        List<compFun> notFound = compFunRepository.findByName("qwerty");
        assertTrue(notFound.isEmpty(), "Несуществующая функция не должна быть найдена");

        List<compFun> allFunctions = compFunRepository.findAll();
        assertEquals(3, allFunctions.size(), "Должны быть найдены все 3 функции");

        //удаление
        compFunRepository.deleteById(foundSinCos.get(0).getId());
        List<compFun> deletedSinCos = compFunRepository.findByName("composite_sin_cos");
        assertTrue(deletedSinCos.isEmpty(), "composite_sin_cos должен быть удален");

        assertEquals(2, compFunRepository.count(), "После удаления должно остаться 2 функции");

        compFunRepository.delete(foundSqrSin.get(0));
        List<compFun> deletedSqrSin = compFunRepository.findByName("composite_sqr_sin");
        assertTrue(deletedSqrSin.isEmpty(), "composite_sqr_sin должен быть удален");

        assertEquals(1, compFunRepository.count(), "Должна остаться только 1 функция");
        List<compFun> remainingFunction = compFunRepository.findByName("composite_log_exp");
        assertFalse(remainingFunction.isEmpty(), "composite_log_exp должен остаться в базе");

        compFunRepository.delete(foundLogExp.get(0));
        List<compFun> deletedLogExp = compFunRepository.findByName("composite_log_exp");
        assertTrue(deletedLogExp.isEmpty(), "composite_log_exp должен быть удален");

        compFunRepository.deleteAll();
    }

}