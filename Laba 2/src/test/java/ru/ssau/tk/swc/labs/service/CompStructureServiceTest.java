package ru.ssau.tk.swc.labs.service;

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
import ru.ssau.tk.swc.labs.repository.AnalFunRepository;
import ru.ssau.tk.swc.labs.repository.CompFunRepository;
import ru.ssau.tk.swc.labs.repository.CompStructureRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class CompStructureServiceTest {

    @Autowired
    private CompStructureRepository compStructureRepository;

    @Autowired
    private CompFunRepository compFunRepository;

    @Autowired
    private AnalFunRepository analFunRepository;

    private CompStructureService compStructureService;

    private compFun compositeFunction1;
    private compFun compositeFunction2;
    private analFun sinFunction;
    private analFun cosFunction;
    private analFun sqrFunction;
    private composite_structure structure1;
    private composite_structure structure2;
    private composite_structure structure3;
    private composite_structure structure4;

    @BeforeEach
    void setUp() {
        compStructureService = new CompStructureService(compStructureRepository);

        compStructureRepository.deleteAll();
        compFunRepository.deleteAll();
        analFunRepository.deleteAll();

        compositeFunction1 = new compFun("composite_sin_cos");
        compositeFunction2 = new compFun("composite_sqr_sin");

        sinFunction = new analFun("sin", 1);
        cosFunction = new analFun("cos", 2);
        sqrFunction = new analFun("sqr", 3);

        compositeFunction1 = compFunRepository.save(compositeFunction1);
        compositeFunction2 = compFunRepository.save(compositeFunction2);
        sinFunction = analFunRepository.save(sinFunction);
        cosFunction = analFunRepository.save(cosFunction);
        sqrFunction = analFunRepository.save(sqrFunction);

        structure1 = new composite_structure(compositeFunction1, cosFunction, 1);
        structure2 = new composite_structure(compositeFunction1, sinFunction, 2);
        structure3 = new composite_structure(compositeFunction2, sinFunction, 1);
        structure4 = new composite_structure(compositeFunction2, sqrFunction, 2);

        structure1 = compStructureRepository.save(structure1);
        structure2 = compStructureRepository.save(structure2);
        structure3 = compStructureRepository.save(structure3);
        structure4 = compStructureRepository.save(structure4);
    }

    @Test
    void testFindSingleStructure() {
        Optional<composite_structure> result = compStructureService.findSingleStructure(structure1.getId());

        assertTrue(result.isPresent(), "Структура должна быть найдена");
        assertEquals(compositeFunction1.getId(), result.get().getCompFun().getId(), "ID композитной функции должен совпадать");
        assertEquals(cosFunction.getId(), result.get().getAnalFun().getId(), "ID аналитической функции должен совпадать");
        assertEquals(1, result.get().getExecutionOrder(), "Порядок выполнения должен быть 1");
    }

    @Test
    void testFindSingleStructureNotFound() {
        Optional<composite_structure> result = compStructureService.findSingleStructure(52L);

        assertFalse(result.isPresent(), "Несуществующая структура не должна быть найдена");
    }

    @Test
    void testFindByCompositeFunctionId() {
        List<composite_structure> result = compStructureService.findByCompositeFunctionId(compositeFunction1.getId());

        assertEquals(2, result.size(), "Должно быть найдено 2 структуры для compositeFunction1");
        assertTrue(result.stream().allMatch(s -> s.getCompFun().getId().equals(compositeFunction1.getId())),
                "Все структуры должны принадлежать compositeFunction1");
    }

    @Test
    void testFindByCompositeFunctionIdNotFound() {
        List<composite_structure> result = compStructureService.findByCompositeFunctionId(999L);

        assertTrue(result.isEmpty(), "Не должно быть найдено структур для несуществующей композитной функции");
    }

    @Test
    void testFindMultipleWithSortingByOrderAsc() {
        List<composite_structure> result = compStructureService.findMultipleWithSorting(
                compositeFunction1.getId(), "order", "asc");

        assertEquals(2, result.size(), "Должно быть найдено 2 структуры");
        assertEquals(1, result.get(0).getExecutionOrder(), "Первая структура должна иметь порядок 1");
        assertEquals(2, result.get(1).getExecutionOrder(), "Вторая структура должна иметь порядок 2");
    }

    @Test
    void testFindMultipleWithSortingByOrderDesc() {
        List<composite_structure> result = compStructureService.findMultipleWithSorting(
                compositeFunction1.getId(), "order", "desc");

        assertEquals(2, result.size(), "Должно быть найдено 2 структуры");
        assertEquals(2, result.get(0).getExecutionOrder(), "Первая структура должна иметь порядок 2");
        assertEquals(1, result.get(1).getExecutionOrder(), "Вторая структура должна иметь порядок 1");
    }

    @Test
    void testFindMultipleWithSortingByCompositeAsc() {
        List<composite_structure> result = compStructureService.findMultipleWithSorting(
                null, "composite", "asc");

        assertEquals(4, result.size(), "Должно быть найдено 4 структуры");
        assertEquals(compositeFunction1.getId(), result.get(0).getCompFun().getId(), "Первая структура должна принадлежать compositeFunction1");
        assertEquals(compositeFunction1.getId(), result.get(1).getCompFun().getId(), "Вторая структура должна принадлежать compositeFunction1");
        assertEquals(compositeFunction2.getId(), result.get(2).getCompFun().getId(), "Третья структура должна принадлежать compositeFunction2");
        assertEquals(compositeFunction2.getId(), result.get(3).getCompFun().getId(), "Четвертая структура должна принадлежать compositeFunction2");
    }

    @Test
    void testFindMultipleWithSortingByAnalyticAsc() {
        List<composite_structure> result = compStructureService.findMultipleWithSorting(
                compositeFunction1.getId(), "analytic", "asc");

        assertEquals(2, result.size(), "Должно быть найдено 2 структуры");
        assertEquals(sinFunction.getId(), result.get(0).getAnalFun().getId(), "Первая структура должна содержать sin");
        assertEquals(cosFunction.getId(), result.get(1).getAnalFun().getId(), "Вторая структура должна содержать cos");

    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<composite_structure> result = compStructureService.findMultipleWithSorting(
                compositeFunction1.getId(), "invalid", "asc");

        assertEquals(2, result.size(), "Должно быть найдено 2 структуры");
        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithNullCompositeFunctionId() {
        List<composite_structure> result = compStructureService.findMultipleWithSorting(
                null, "order", "asc");

        assertEquals(4, result.size(), "Должно быть найдено все 4 структуры при null compositeFunctionId");
    }

    @Test
    void testFindByAnalyticFunctionId() {
        List<composite_structure> result = compStructureService.findByAnalyticFunctionId(sinFunction.getId());

        assertEquals(2, result.size(), "Должно быть найдено 2 структуры с sin функцией");
        assertTrue(result.stream().allMatch(s -> s.getAnalFun().getId().equals(sinFunction.getId())),
                "Все структуры должны содержать sin функцию");
    }

    @Test
    void testFindByAnalyticFunctionIdNotFound() {
        List<composite_structure> result = compStructureService.findByAnalyticFunctionId(999L);

        assertTrue(result.isEmpty(), "Не должно быть найдено структур для несуществующей аналитической функции");
    }

    @Test
    void testGetFlattenedHierarchy() {
        List<Map<String, Object>> result = compStructureService.getFlattenedHierarchy(compositeFunction1.getId());

        assertEquals(2, result.size(), "Должно быть 2 компонента в иерархии");

        Map<String, Object> firstComponent = result.get(0);
        assertEquals(1, firstComponent.get("executionOrder"), "Первый компонент должен иметь порядок 1");
        assertEquals("ANALYTIC", firstComponent.get("type"), "Тип должен быть ANALYTIC");
        assertEquals(cosFunction, firstComponent.get("function"), "Функция должна быть cos");

        Map<String, Object> secondComponent = result.get(1);
        assertEquals(2, secondComponent.get("executionOrder"), "Второй компонент должен иметь порядок 2");
        assertEquals("ANALYTIC", secondComponent.get("type"), "Тип должен быть ANALYTIC");
        assertEquals(sinFunction, secondComponent.get("function"), "Функция должна быть sin");
    }

    @Test
    void testGetFlattenedHierarchyOrder() {
        List<Map<String, Object>> result = compStructureService.getFlattenedHierarchy(compositeFunction2.getId());

        assertEquals(2, result.size(), "Должно быть 2 компонента в иерархии");
        assertEquals(1, result.get(0).get("executionOrder"), "Первый компонент должен иметь порядок 1");
        assertEquals(2, result.get(1).get("executionOrder"), "Второй компонент должен иметь порядок 2");
        assertEquals(sinFunction, result.get(0).get("function"), "Первый компонент должен быть sin");
        assertEquals(sqrFunction, result.get(1).get("function"), "Второй компонент должен быть sqr");
    }

    @Test
    void testFindAllStructures() {
        List<composite_structure> result = compStructureService.findAllStructures();

        assertEquals(4, result.size(), "Должно быть найдено 4 структуры");
    }

    @Test
    void testSave() {
        composite_structure newStructure = new composite_structure(compositeFunction1, sqrFunction, 3);

        composite_structure result = compStructureService.save(newStructure);

        assertNotNull(result, "Сохраненная структура не должна быть null");
        assertEquals(compositeFunction1.getId(), result.getCompFun().getId(), "ID композитной функции должен совпадать");
        assertEquals(sqrFunction.getId(), result.getAnalFun().getId(), "ID аналитической функции должен совпадать");
        assertEquals(3, result.getExecutionOrder(), "Порядок выполнения должен быть 3");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<composite_structure> found = compStructureService.findSingleStructure(result.getId());
        assertTrue(found.isPresent(), "Новая структура должна быть найдена в БД");
    }

    @Test
    void testDeleteById() {
        Optional<composite_structure> beforeDelete = compStructureService.findSingleStructure(structure1.getId());
        assertTrue(beforeDelete.isPresent(), "Структура должна существовать до удаления");

        compStructureService.deleteById(structure1.getId());

        Optional<composite_structure> afterDelete = compStructureService.findSingleStructure(structure1.getId());
        assertFalse(afterDelete.isPresent(), "Структура должна быть удалена");

        List<composite_structure> remainingStructures = compStructureService.findAllStructures();
        assertEquals(3, remainingStructures.size(), "После удаления должно остаться 3 структуры");
    }
}