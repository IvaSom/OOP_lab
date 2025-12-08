package ru.ssau.tk.swc.labs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.repository.AnalFunRepository;

import java.util.Arrays;
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
class AnalFunServiceTest {

    @Autowired
    private AnalFunRepository analFunRepository;

    private AnalFunService analFunService;

    private analFun sinFunction;
    private analFun cosFunction;
    private analFun sqrFunction;
    private analFun constantFunction;

    @BeforeEach
    void setUp() {
        analFunService = new AnalFunService(analFunRepository);

        analFunRepository.deleteAll();

        sinFunction = new analFun("sin", 1);
        cosFunction = new analFun("cos", 2);
        sqrFunction = new analFun("sqr", 3);
        constantFunction = new analFun("constant", 4);

        sinFunction = analFunRepository.save(sinFunction);
        cosFunction = analFunRepository.save(cosFunction);
        sqrFunction = analFunRepository.save(sqrFunction);
        constantFunction = analFunRepository.save(constantFunction);
    }

    @Test
    void testFindSingleFunction() {
        Optional<analFun> result = analFunService.findSingleFunction("sin");

        assertTrue(result.isPresent(), "Функция sin должна быть найдена");
        assertEquals("sin", result.get().getName(), "Имя функции должно быть 'sin'");
        assertEquals(1, result.get().getType(), "Тип функции должен быть 1");
    }

    @Test
    void testFindSingleFunctionNotFound() {
        Optional<analFun> result = analFunService.findSingleFunction("unknown");

        assertFalse(result.isPresent(), "Несуществующая функция не должна быть найдена");
    }

    @Test
    void testFindSingleFunctionById() {
        Optional<analFun> result = analFunService.findSingleFunctionById(sinFunction.getId());

        assertTrue(result.isPresent(), "Функция с ID=" + sinFunction.getId() + " должна быть найдена");
        assertEquals("sin", result.get().getName(), "Имя функции должно быть 'sin'");
        assertEquals(1, result.get().getType(), "Тип функции должен быть 1");
    }

    @Test
    void testFindSingleFunctionByIdNotFound() {
        Optional<analFun> result = analFunService.findSingleFunctionById(52L);

        assertFalse(result.isPresent(), "Несуществующая функция по ID не должна быть найдена");
    }

    @Test
    void testFindMultipleWithSortingByNameAsc() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{}, "name", "asc");

        assertEquals(4, result.size(), "Должно быть найдено 4 функции");

        assertEquals("constant", result.get(0).getName(), "Первая функция должна быть 'constant'");
        assertEquals("cos", result.get(1).getName(), "Вторая функция должна быть 'cos'");
        assertEquals("sin", result.get(2).getName(), "Третья функция должна быть 'sin'");
        assertEquals("sqr", result.get(3).getName(), "Четвертая функция должна быть 'sqr'");
    }

    @Test
    void testFindMultipleWithSortingByNameDesc() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{}, "name", "desc");

        assertEquals(4, result.size(), "Должно быть найдено 4 функции");

        assertEquals("sqr", result.get(0).getName(), "Первая функция должна быть 'sqr'");
        assertEquals("sin", result.get(1).getName(), "Вторая функция должна быть 'sin'");
        assertEquals("cos", result.get(2).getName(), "Третья функция должна быть 'cos'");
        assertEquals("constant", result.get(3).getName(), "Четвертая функция должна быть 'constant'");
    }

    @Test
    void testFindMultipleWithSortingByTypeAsc() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{}, "type", "asc");

        assertEquals(4, result.size(), "Должно быть найдено 4 функции");

        assertEquals(1, result.get(0).getType(), "Первая функция должна иметь тип 1 (sin)");
        assertEquals("sin", result.get(0).getName(), "Первая функция должна быть 'sin'");

        assertEquals(2, result.get(1).getType(), "Вторая функция должна иметь тип 2 (cos)");
        assertEquals("cos", result.get(1).getName(), "Вторая функция должна быть 'cos'");

        assertEquals(3, result.get(2).getType(), "Третья функция должна иметь тип 3 (sqr)");
        assertEquals("sqr", result.get(2).getName(), "Третья функция должна быть 'sqr'");

        assertEquals(4, result.get(3).getType(), "Четвертая функция должна иметь тип 4 (constant)");
        assertEquals("constant", result.get(3).getName(), "Четвертая функция должна быть 'constant'");
    }

    @Test
    void testFindMultipleWithSortingByTypeDesc() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{}, "type", "desc");

        assertEquals(4, result.size(), "Должно быть найдено 4 функции");

        assertEquals(4, result.get(0).getType(), "Первая функция должна иметь тип 4 (constant)");
        assertEquals("constant", result.get(0).getName(), "Первая функция должна быть 'constant'");

        assertEquals(3, result.get(1).getType(), "Вторая функция должна иметь тип 3 (sqr)");
        assertEquals("sqr", result.get(1).getName(), "Вторая функция должна быть 'sqr'");

        assertEquals(2, result.get(2).getType(), "Третья функция должна иметь тип 2 (cos)");
        assertEquals("cos", result.get(2).getName(), "Третья функция должна быть 'cos'");

        assertEquals(1, result.get(3).getType(), "Четвертая функция должна иметь тип 1 (sin)");
        assertEquals("sin", result.get(3).getName(), "Четвертая функция должна быть 'sin'");
    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{}, "invalid", "asc");

        assertEquals(4, result.size(), "Должно быть найдено 4 функции");

        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(1).getId() < result.get(2).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(2).getId() < result.get(3).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithTypeFiltering() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{1, 3}, "name", "asc");

        assertEquals(2, result.size(), "Должно быть найдено 2 функции (типы 1 и 3)");

        assertTrue(result.stream().allMatch(f -> f.getType() == 1 || f.getType() == 3),
                "Все функции должны иметь тип 1 или 3");

        assertEquals("sin", result.get(0).getName(), "Первая функция должна быть 'sin'");
        assertEquals("sqr", result.get(1).getName(), "Вторая функция должна быть 'sqr'");
    }

    @Test
    void testFindMultipleWithSingleTypeFilter() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{2}, "name", "asc");

        assertEquals(1, result.size(), "Должна быть найдена 1 функция (тип 2)");
        assertEquals("cos", result.get(0).getName(), "Функция должна быть 'cos'");
        assertEquals(2, result.get(0).getType(), "Тип функции должен быть 2");
    }

    @Test
    void testFindMultipleWithEmptyTypeFilter() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{}, "name", "asc");

        assertEquals(4, result.size(), "Должно быть найдено все 4 функции при пустом фильтре");
    }

    @Test
    void testFindMultipleWithNullTypeFilter() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                null, "name", "asc");

        assertEquals(4, result.size(), "Должно быть найдено все 4 функции при null фильтре");
    }

    @Test
    void testFindMultipleWithNonExistingTypeFilter() {
        List<analFun> result = analFunService.findMultipleWithSorting(
                new Integer[]{42}, "name", "asc");

        assertEquals(0, result.size(), "Не должно быть найдено функций при фильтре по несуществующему типу");
    }

    @Test
    void testFindAllFunctions() {
        List<analFun> result = analFunService.findAllFunctions();

        assertEquals(4, result.size(), "Должно быть возвращено 4 функции");

        List<String> functionNames = result.stream().map(analFun::getName).toList();
        assertTrue(functionNames.contains("sin"), "Должна присутствовать функция 'sin'");
        assertTrue(functionNames.contains("cos"), "Должна присутствовать функция 'cos'");
        assertTrue(functionNames.contains("sqr"), "Должна присутствовать функция 'sqr'");
        assertTrue(functionNames.contains("constant"), "Должна присутствовать функция 'constant'");
    }

    @Test
    void testSaveFunction() {
        analFun newFunction = new analFun("newFunc", 5);

        analFun result = analFunService.save(newFunction);

        assertNotNull(result, "Сохраненная функция не должна быть null");
        assertEquals("newFunc", result.getName(), "Имя функции должно быть 'newFunc'");
        assertEquals(5, result.getType(), "Тип функции должен быть 5");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<analFun> found = analFunService.findSingleFunction("newFunc");
        assertTrue(found.isPresent(), "Новая функция должна быть найдена в БД");
        assertEquals("newFunc", found.get().getName(), "Имя найденной функции должно быть 'newFunc'");
    }

    @Test
    void testDeleteById() {
        Optional<analFun> beforeDelete = analFunService.findSingleFunctionById(sinFunction.getId());
        assertTrue(beforeDelete.isPresent(), "Функция должна существовать до удаления");

        analFunService.deleteById(sinFunction.getId());

        Optional<analFun> afterDelete = analFunService.findSingleFunctionById(sinFunction.getId());
        assertFalse(afterDelete.isPresent(), "Функция должна быть удалена");

        List<analFun> remainingFunctions = analFunService.findAllFunctions();
        assertEquals(3, remainingFunctions.size(), "После удаления должно остаться 3 функции");
    }

}