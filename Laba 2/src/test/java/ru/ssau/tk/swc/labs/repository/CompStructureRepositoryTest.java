package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.entity.composite_structure;

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
class CompStructureRepositoryTest {

    @Autowired
    private CompStructureRepository compStructureRepository;

    @Autowired
    private CompFunRepository compFunRepository;

    @Autowired
    private AnalFunRepository analFunRepository;

    private compFun compositeFunction1;
    private compFun compositeFunction2;
    private analFun analyticFunction1;
    private analFun analyticFunction2;
    private analFun analyticFunction3;

    @BeforeEach
    void setUp() {
        //очистка бд
        compStructureRepository.deleteAll();
        compFunRepository.deleteAll();
        analFunRepository.deleteAll();

        compositeFunction1 = new compFun("composite_sin_cos");
        compositeFunction2 = new compFun("composite_sqr_sin");

        analyticFunction1 = new analFun("sin", 1);
        analyticFunction2 = new analFun("cos", 2);
        analyticFunction3 = new analFun("x^2", 3);

        compositeFunction1 = compFunRepository.save(compositeFunction1);
        compositeFunction2 = compFunRepository.save(compositeFunction2);

        analyticFunction1 = analFunRepository.save(analyticFunction1);
        analyticFunction2 = analFunRepository.save(analyticFunction2);
        analyticFunction3 = analFunRepository.save(analyticFunction3);
    }

    @Test
    void testGenerateFindDelete() {

        composite_structure structure1 = new composite_structure(compositeFunction1, analyticFunction1, 1);
        composite_structure structure2 = new composite_structure(compositeFunction1, analyticFunction2, 2);
        composite_structure structure3 = new composite_structure(compositeFunction2, analyticFunction3, 1);
        composite_structure structure4 = new composite_structure(compositeFunction2, analyticFunction1, 2);

        composite_structure saved1 = compStructureRepository.save(structure1);
        composite_structure saved2 = compStructureRepository.save(structure2);
        composite_structure saved3 = compStructureRepository.save(structure3);
        composite_structure saved4 = compStructureRepository.save(structure4);

        assertEquals(4, compStructureRepository.count(), "должно быть 4 структуры в базе");

        List<composite_structure> allStructures = compStructureRepository.findAll();
        assertEquals(4, allStructures.size(), "Должны быть найдены все 4 структуры");

        Optional<composite_structure> foundStructure1 = compStructureRepository.findById(saved1.getId());
        assertTrue(foundStructure1.isPresent(), "Структура 1 должна быть найдена");
        assertEquals(compositeFunction1.getId(), foundStructure1.get().getCompFun().getId());
        assertEquals(analyticFunction1.getId(), foundStructure1.get().getAnalFun().getId());
        assertEquals(1, foundStructure1.get().getExecutionOrder());

        Optional<composite_structure> foundStructure2 = compStructureRepository.findById(saved2.getId());
        assertTrue(foundStructure2.isPresent(), "Структура 2 должна быть найдена");
        assertEquals(compositeFunction1.getId(), foundStructure2.get().getCompFun().getId());
        assertEquals(analyticFunction2.getId(), foundStructure2.get().getAnalFun().getId());
        assertEquals(2, foundStructure2.get().getExecutionOrder());

        Optional<composite_structure> foundStructure3 = compStructureRepository.findById(saved3.getId());
        assertTrue(foundStructure3.isPresent(), "Структура 3 должна быть найдена");
        assertEquals(compositeFunction2.getId(), foundStructure3.get().getCompFun().getId());
        assertEquals(analyticFunction3.getId(), foundStructure3.get().getAnalFun().getId());
        assertEquals(1, foundStructure3.get().getExecutionOrder());

        Optional<composite_structure> foundStructure4 = compStructureRepository.findById(saved4.getId());
        assertTrue(foundStructure4.isPresent(), "Структура 4 должна быть найдена");
        assertEquals(compositeFunction2.getId(), foundStructure4.get().getCompFun().getId());
        assertEquals(analyticFunction1.getId(), foundStructure4.get().getAnalFun().getId());
        assertEquals(2, foundStructure4.get().getExecutionOrder());

        //удаление
        compStructureRepository.deleteById(foundStructure1.get().getId());
        Optional<composite_structure> deletedStructure1 = compStructureRepository.findById(saved1.getId());
        assertFalse(deletedStructure1.isPresent(), "Структура 1 должна быть удалена");

        assertEquals(3, compStructureRepository.count(), "После удаления должно остаться 3 структуры");

        compStructureRepository.delete(foundStructure2.get());
        Optional<composite_structure> deletedStructure2 = compStructureRepository.findById(saved2.getId());
        assertFalse(deletedStructure2.isPresent(), "Структура 2 должна быть удалена");

        assertEquals(2, compStructureRepository.count(), "После удаления должно остаться 2 структуры");

        compStructureRepository.delete(foundStructure3.get());
        Optional<composite_structure> deletedStructure3 = compStructureRepository.findById(saved3.getId());
        assertFalse(deletedStructure3.isPresent(), "Структура 3 должна быть удалена");

        assertEquals(1, compStructureRepository.count(), "Должна остаться только 1 структура");
        Optional<composite_structure> remainingStructure = compStructureRepository.findById(saved4.getId());
        assertTrue(remainingStructure.isPresent(), "Структура 4 должна остаться в базе");

        compStructureRepository.delete(foundStructure4.get());
        Optional<composite_structure> deletedStructure4 = compStructureRepository.findById(saved4.getId());
        assertFalse(deletedStructure4.isPresent(), "Структура 4 должна быть удалена");


        compStructureRepository.deleteAll();
        compFunRepository.deleteAll();
        analFunRepository.deleteAll();
    }

}