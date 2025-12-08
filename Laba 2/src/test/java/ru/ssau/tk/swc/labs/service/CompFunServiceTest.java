package ru.ssau.tk.swc.labs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.repository.CompFunRepository;

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
class CompFunServiceTest {

    @Autowired
    private CompFunRepository compFunRepository;

    private CompFunService compFunService;

    private compFun compositeSinCos;
    private compFun compositeSqrSin;
    private compFun compositeLogExp;

    @BeforeEach
    void setUp() {
        compFunService = new CompFunService(compFunRepository);

        compFunRepository.deleteAll();

        compositeSinCos = new compFun("composite_sin_cos");
        compositeSqrSin = new compFun("composite_sqr_sin");
        compositeLogExp = new compFun("composite_log_exp");

        compositeSinCos = compFunRepository.save(compositeSinCos);
        compositeSqrSin = compFunRepository.save(compositeSqrSin);
        compositeLogExp = compFunRepository.save(compositeLogExp);
    }

    @Test
    void testFindSingleFunction() {
        Optional<compFun> result = compFunService.findSingleFunction("composite_sin_cos");

        assertTrue(result.isPresent(), "Функция composite_sin_cos должна быть найдена");
        assertEquals("composite_sin_cos", result.get().getName(), "Имя функции должно быть 'composite_sin_cos'");
    }

    @Test
    void testFindSingleFunctionNotFound() {
        Optional<compFun> result = compFunService.findSingleFunction("unknown");

        assertFalse(result.isPresent(), "Несуществующая функция не должна быть найдена");
    }

    @Test
    void testFindSingleFunctionById() {
        Optional<compFun> result = compFunService.findSingleFunctionById(compositeSinCos.getId());

        assertTrue(result.isPresent(), "Функция с ID=" + compositeSinCos.getId() + " должна быть найдена");
        assertEquals("composite_sin_cos", result.get().getName(), "Имя функции должно быть 'composite_sin_cos'");
    }

    @Test
    void testFindSingleFunctionByIdNotFound() {
        Optional<compFun> result = compFunService.findSingleFunctionById(52L);

        assertFalse(result.isPresent(), "Несуществующая функция по ID не должна быть найдена");
    }

    @Test
    void testFindMultipleWithSortingByNameAsc() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                new String[]{}, "name", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 функции");

        assertEquals("composite_log_exp", result.get(0).getName(), "Первая функция должна быть 'composite_log_exp'");
        assertEquals("composite_sin_cos", result.get(1).getName(), "Вторая функция должна быть 'composite_sin_cos'");
        assertEquals("composite_sqr_sin", result.get(2).getName(), "Третья функция должна быть 'composite_sqr_sin'");
    }

    @Test
    void testFindMultipleWithSortingByNameDesc() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                new String[]{}, "name", "desc");

        assertEquals(3, result.size(), "Должно быть найдено 3 функции");

        assertEquals("composite_sqr_sin", result.get(0).getName(), "Первая функция должна быть 'composite_sqr_sin'");
        assertEquals("composite_sin_cos", result.get(1).getName(), "Вторая функция должна быть 'composite_sin_cos'");
        assertEquals("composite_log_exp", result.get(2).getName(), "Третья функция должна быть 'composite_log_exp'");
    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                new String[]{}, "invalid", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 функции");

        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(1).getId() < result.get(2).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithNameFiltering() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                new String[]{"composite_sin_cos", "composite_sqr_sin"}, "name", "asc");

        assertEquals(2, result.size(), "Должно быть найдено 2 функции");

        assertTrue(result.stream().allMatch(f -> f.getName().equals("composite_sin_cos") || f.getName().equals("composite_sqr_sin")),
                "Все функции должны иметь имя composite_sin_cos или composite_sqr_sin");

        assertEquals("composite_sin_cos", result.get(0).getName(), "Первая функция должна быть 'composite_sin_cos'");
        assertEquals("composite_sqr_sin", result.get(1).getName(), "Вторая функция должна быть 'composite_sqr_sin'");
    }

    @Test
    void testFindMultipleWithSingleNameFilter() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                new String[]{"composite_log_exp"}, "name", "asc");

        assertEquals(1, result.size(), "Должна быть найдена 1 функция");
        assertEquals("composite_log_exp", result.get(0).getName(), "Функция должна быть 'composite_log_exp'");
    }

    @Test
    void testFindMultipleWithEmptyNameFilter() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                new String[]{}, "name", "asc");

        assertEquals(3, result.size(), "Должно быть найдено все 3 функции при пустом фильтре");
    }

    @Test
    void testFindMultipleWithNullNameFilter() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                null, "name", "asc");

        assertEquals(3, result.size(), "Должно быть найдено все 3 функции при null фильтре");
    }

    @Test
    void testFindMultipleWithNonExistingNameFilter() {
        List<compFun> result = compFunService.findMultipleWithSorting(
                new String[]{"unknown"}, "name", "asc");

        assertEquals(0, result.size(), "Не должно быть найдено функций при фильтре по несуществующему имени");
    }

    @Test
    void testFindAllFunctions() {
        List<compFun> result = compFunService.findAllFunctions();

        assertEquals(3, result.size(), "Должно быть возвращено 3 функции");

        List<String> functionNames = result.stream().map(compFun::getName).toList();
        assertTrue(functionNames.contains("composite_sin_cos"), "Должна присутствовать функция 'composite_sin_cos'");
        assertTrue(functionNames.contains("composite_sqr_sin"), "Должна присутствовать функция 'composite_sqr_sin'");
        assertTrue(functionNames.contains("composite_log_exp"), "Должна присутствовать функция 'composite_log_exp'");
    }

    @Test
    void testSaveFunction() {
        compFun newFunction = new compFun("composite_new_func");

        compFun result = compFunService.save(newFunction);

        assertNotNull(result, "Сохраненная функция не должна быть null");
        assertEquals("composite_new_func", result.getName(), "Имя функции должно быть 'composite_new_func'");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<compFun> found = compFunService.findSingleFunction("composite_new_func");
        assertTrue(found.isPresent(), "Новая функция должна быть найдена в БД");
        assertEquals("composite_new_func", found.get().getName(), "Имя найденной функции должно быть 'composite_new_func'");
    }

    @Test
    void testDeleteById() {
        Optional<compFun> beforeDelete = compFunService.findSingleFunctionById(compositeSinCos.getId());
        assertTrue(beforeDelete.isPresent(), "Функция должна существовать до удаления");

        compFunService.deleteById(compositeSinCos.getId());

        Optional<compFun> afterDelete = compFunService.findSingleFunctionById(compositeSinCos.getId());
        assertFalse(afterDelete.isPresent(), "Функция должна быть удалена");

        List<compFun> remainingFunctions = compFunService.findAllFunctions();
        assertEquals(2, remainingFunctions.size(), "После удаления должно остаться 2 функции");
    }
}