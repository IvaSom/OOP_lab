package ru.ssau.tk.swc.labs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.repository.TabFunRepository;

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
class TabFunServiceTest {

    @Autowired
    private TabFunRepository tabFunRepository;

    private TabFunService tabFunService;

    private tabFun tabFunction1;
    private tabFun tabFunction2;
    private tabFun tabFunction3;

    @BeforeEach
    void setUp() {
        tabFunService = new TabFunService(tabFunRepository);

        tabFunRepository.deleteAll();

        tabFunction1 = new tabFun();
        tabFunction1.setName("tabulated_sin");
        tabFunction2 = new tabFun();
        tabFunction2.setName("tabulated_cos");
        tabFunction3 = new tabFun();
        tabFunction3.setName("tabulated_sqr");

        tabFunction1 = tabFunRepository.save(tabFunction1);
        tabFunction2 = tabFunRepository.save(tabFunction2);
        tabFunction3 = tabFunRepository.save(tabFunction3);
    }

    @Test
    void testFindSingleFunction() {
        Optional<tabFun> result = tabFunService.findSingleFunction("tabulated_sin");

        assertTrue(result.isPresent(), "Функция tabulated_sin должна быть найдена");
        assertEquals("tabulated_sin", result.get().getName(), "Имя функции должно быть 'tabulated_sin'");
    }

    @Test
    void testFindSingleFunctionNotFound() {
        Optional<tabFun> result = tabFunService.findSingleFunction("unknown");

        assertFalse(result.isPresent(), "Несуществующая функция не должна быть найдена");
    }

    @Test
    void testFindSingleFunctionById() {
        Optional<tabFun> result = tabFunService.findSingleFunctionById(tabFunction1.getId());

        assertTrue(result.isPresent(), "Функция с ID=" + tabFunction1.getId() + " должна быть найдена");
        assertEquals("tabulated_sin", result.get().getName(), "Имя функции должно быть 'tabulated_sin'");
    }

    @Test
    void testFindSingleFunctionByIdNotFound() {
        Optional<tabFun> result = tabFunService.findSingleFunctionById(52L);

        assertFalse(result.isPresent(), "Несуществующая функция по ID не должна быть найдена");
    }

    @Test
    void testFindMultipleWithSortingByNameAsc() {
        List<tabFun> result = tabFunService.findMultipleWithSorting(
                new Integer[]{}, "type", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 функции");

        assertEquals("tabulated_cos", result.get(0).getName(), "Первая функция должна быть 'tabulated_cos'");
        assertEquals("tabulated_sin", result.get(1).getName(), "Вторая функция должна быть 'tabulated_sin'");
        assertEquals("tabulated_sqr", result.get(2).getName(), "Третья функция должна быть 'tabulated_sqr'");
    }

    @Test
    void testFindMultipleWithSortingByNameDesc() {
        List<tabFun> result = tabFunService.findMultipleWithSorting(
                new Integer[]{}, "type", "desc");

        assertEquals(3, result.size(), "Должно быть найдено 3 функции");

        assertEquals("tabulated_sqr", result.get(0).getName(), "Первая функция должна быть 'tabulated_sqr'");
        assertEquals("tabulated_sin", result.get(1).getName(), "Вторая функция должна быть 'tabulated_sin'");
        assertEquals("tabulated_cos", result.get(2).getName(), "Третья функция должна быть 'tabulated_cos'");
    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<tabFun> result = tabFunService.findMultipleWithSorting(
                new Integer[]{}, "invalid", "asc");

        assertEquals(3, result.size(), "Должно быть найдено 3 функции");

        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(1).getId() < result.get(2).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithNameFiltering() {
        List<tabFun> result = tabFunService.findMultipleWithSorting(
                new Integer[]{1, 2}, "type", "asc");

        assertEquals(0, result.size(), "Не должно быть найдено функций при фильтрации по типам");
    }

    @Test
    void testFindMultipleWithEmptyTypeFilter() {
        List<tabFun> result = tabFunService.findMultipleWithSorting(
                new Integer[]{}, "type", "asc");

        assertEquals(3, result.size(), "Должно быть найдено все 3 функции при пустом фильтре");
    }

    @Test
    void testFindMultipleWithNullTypeFilter() {
        List<tabFun> result = tabFunService.findMultipleWithSorting(
                null, "type", "asc");

        assertEquals(3, result.size(), "Должно быть найдено все 3 функции при null фильтре");
    }

    @Test
    void testFindAllFunctions() {
        List<tabFun> result = tabFunService.findAllFunctions();

        assertEquals(3, result.size(), "Должно быть возвращено 3 функции");

        List<String> functionNames = result.stream().map(tabFun::getName).toList();
        assertTrue(functionNames.contains("tabulated_sin"), "Должна присутствовать функция 'tabulated_sin'");
        assertTrue(functionNames.contains("tabulated_cos"), "Должна присутствовать функция 'tabulated_cos'");
        assertTrue(functionNames.contains("tabulated_sqr"), "Должна присутствовать функция 'tabulated_sqr'");
    }

    @Test
    void testSaveFunction() {
        tabFun newFunction = new tabFun();
        newFunction.setName("tabulated_new");

        tabFun result = tabFunService.save(newFunction);

        assertNotNull(result, "Сохраненная функция не должна быть null");
        assertEquals("tabulated_new", result.getName(), "Имя функции должно быть 'tabulated_new'");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<tabFun> found = tabFunService.findSingleFunction("tabulated_new");
        assertTrue(found.isPresent(), "Новая функция должна быть найдена в БД");
        assertEquals("tabulated_new", found.get().getName(), "Имя найденной функции должно быть 'tabulated_new'");
    }

    @Test
    void testDeleteById() {
        Optional<tabFun> beforeDelete = tabFunService.findSingleFunctionById(tabFunction1.getId());
        assertTrue(beforeDelete.isPresent(), "Функция должна существовать до удаления");

        tabFunService.deleteById(tabFunction1.getId());

        Optional<tabFun> afterDelete = tabFunService.findSingleFunctionById(tabFunction1.getId());
        assertFalse(afterDelete.isPresent(), "Функция должна быть удалена");

        List<tabFun> remainingFunctions = tabFunService.findAllFunctions();
        assertEquals(2, remainingFunctions.size(), "После удаления должно остаться 2 функции");
    }
}