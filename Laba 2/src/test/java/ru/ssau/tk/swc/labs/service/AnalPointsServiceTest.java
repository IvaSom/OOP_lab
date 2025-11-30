package ru.ssau.tk.swc.labs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.entity.anal_points;
import ru.ssau.tk.swc.labs.repository.AnalFunRepository;
import ru.ssau.tk.swc.labs.repository.AnalPointsRepository;

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
class AnalPointsServiceTest {

    @Autowired
    private AnalPointsRepository analPointsRepository;

    @Autowired
    private AnalFunRepository analFunRepository;

    private AnalPointsService analPointsService;

    private analFun sinFunction;
    private analFun cosFunction;
    private anal_points point1;
    private anal_points point2;
    private anal_points point3;
    private anal_points point4;
    private anal_points point5;

    @BeforeEach
    void setUp() {
        analPointsService = new AnalPointsService(analPointsRepository);

        analPointsRepository.deleteAll();
        analFunRepository.deleteAll();

        sinFunction = new analFun("sin", 1);
        cosFunction = new analFun("cos", 2);

        sinFunction = analFunRepository.save(sinFunction);
        cosFunction = analFunRepository.save(cosFunction);

        point1 = new anal_points(0.0, 0.0, sinFunction);
        point2 = new anal_points(Math.PI / 2, 1.0, sinFunction);
        point3 = new anal_points(Math.PI, 0.0, sinFunction);
        point4 = new anal_points(0.0, 1.0, cosFunction);
        point5 = new anal_points(Math.PI, -1.0, cosFunction);

        point1 = analPointsRepository.save(point1);
        point2 = analPointsRepository.save(point2);
        point3 = analPointsRepository.save(point3);
        point4 = analPointsRepository.save(point4);
        point5 = analPointsRepository.save(point5);
    }

    @Test
    void testFindSinglePoint() {
        Optional<anal_points> result = analPointsService.findSinglePoint(0.0, sinFunction.getId());

        assertTrue(result.isPresent(), "Точка должна быть найдена");
        assertEquals(0.0, result.get().getX(), 0.001, "X координата должна быть 0.0");
        assertEquals(0.0, result.get().getY(), 0.001, "Y координата должна быть 0.0");
    }

    @Test
    void testFindSinglePointNotFound() {
        Optional<anal_points> result = analPointsService.findSinglePoint(100.0, sinFunction.getId());

        assertFalse(result.isPresent(), "Несуществующая точка не должна быть найдена");
    }

    @Test
    void testFindMultipleWithSortingByXAsc() {
        List<anal_points> result = analPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "x", "asc", sinFunction.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Первая точка должна иметь X=0.0");
        assertEquals(Math.PI / 2, result.get(1).getX(), 0.001, "Вторая точка должна иметь X=π/2");
        assertEquals(Math.PI, result.get(2).getX(), 0.001, "Третья точка должна иметь X=π");
    }

    @Test
    void testFindMultipleWithSortingByXDesc() {
        List<anal_points> result = analPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "x", "desc", sinFunction.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(Math.PI, result.get(0).getX(), 0.001, "Первая точка должна иметь X=π");
        assertEquals(Math.PI / 2, result.get(1).getX(), 0.001, "Вторая точка должна иметь X=π/2");
        assertEquals(0.0, result.get(2).getX(), 0.001, "Третья точка должна иметь X=0.0");
    }

    @Test
    void testFindMultipleWithSortingByYAsc() {
        List<anal_points> result = analPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "y", "asc", sinFunction.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(0.0, result.get(0).getY(), 0.001, "Первая точка должна иметь Y=0.0");
        assertEquals(0.0, result.get(1).getY(), 0.001, "Вторая точка должна иметь Y=0.0");
        assertEquals(1.0, result.get(2).getY(), 0.001, "Третья точка должна иметь Y=1.0");
    }

    @Test
    void testFindMultipleWithSortingByYDesc() {
        List<anal_points> result = analPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "y", "desc", sinFunction.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(1.0, result.get(0).getY(), 0.001, "Первая точка должна иметь Y=1.0");
        assertEquals(0.0, result.get(1).getY(), 0.001, "Вторая точка должна иметь Y=0.0");
        assertEquals(0.0, result.get(2).getY(), 0.001, "Третья точка должна иметь Y=0.0");
    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<anal_points> result = analPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "invalid", "asc", sinFunction.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(1).getId() < result.get(2).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithXFilter() {
        List<anal_points> result = analPointsService.findMultipleWithSorting(
                0.0, 1.0, -2.0, 2.0, "x", "asc", sinFunction.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка в диапазоне X [0.0, 1.0]");
        assertEquals(0.0, result.get(0).getX(), 0.001, "X координата должна быть 0.0");
    }

    @Test
    void testFindMultipleWithYFilter() {
        List<anal_points> result = analPointsService.findMultipleWithSorting(
                0.0, Math.PI, 0.5, 1.5, "x", "asc", sinFunction.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка в диапазоне Y [0.5, 1.5]");
        assertEquals(1.0, result.get(0).getY(), 0.001, "Y координата должна быть 1.0");
    }

    @Test
    void testBreadthFirstSearch() {
        List<anal_points> result = analPointsService.breadthFirstSearch(0.0, 2.0, sinFunction.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки поиском в ширину");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Первая точка должна иметь X=0.0");
    }

    @Test
    void testBreadthFirstSearchWithSmallRadius() {
        List<anal_points> result = analPointsService.breadthFirstSearch(0.0, 0.1, sinFunction.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка при малом радиусе");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Точка должна иметь X=0.0");
    }

    @Test
    void testBreadthFirstSearchStartPointNotFound() {
        List<anal_points> result = analPointsService.breadthFirstSearch(100.0, 2.0, sinFunction.getId());

        assertTrue(result.isEmpty(), "Поиск должен вернуть пустой список при отсутствии стартовой точки");
    }

    @Test
    void testFindByFunctionId() {
        List<anal_points> result = analPointsService.findByFunctionId(sinFunction.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки для функции sin");
        assertTrue(result.stream().allMatch(p -> p.getFunction().getId().equals(sinFunction.getId())),
                "Все точки должны принадлежать функции sin");
    }

    @Test
    void testFindByFunctionIdNotFound() {
        List<anal_points> result = analPointsService.findByFunctionId(999L);

        assertTrue(result.isEmpty(), "Не должно быть найдено точек для несуществующей функции");
    }

    @Test
    void testFindAll() {
        List<anal_points> result = analPointsService.findAll();

        assertEquals(5, result.size(), "Должно быть найдено 5 точек");
    }

    @Test
    void testFindById() {
        Optional<anal_points> result = analPointsService.findById(point1.getId());

        assertTrue(result.isPresent(), "Точка должна быть найдена по ID");
        assertEquals(point1.getId(), result.get().getId(), "ID точки должен совпадать");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<anal_points> result = analPointsService.findById(999L);

        assertFalse(result.isPresent(), "Несуществующая точка не должна быть найдена по ID");
    }

    @Test
    void testSave() {
        anal_points newPoint = new anal_points(1.0, 2.0, sinFunction);

        anal_points result = analPointsService.save(newPoint);

        assertNotNull(result, "Сохраненная точка не должна быть null");
        assertEquals(1.0, result.getX(), 0.001, "X координата должна быть 1.0");
        assertEquals(2.0, result.getY(), 0.001, "Y координата должна быть 2.0");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<anal_points> found = analPointsService.findById(result.getId());
        assertTrue(found.isPresent(), "Новая точка должна быть найдена в БД");
    }

    @Test
    void testDeleteById() {
        Optional<anal_points> beforeDelete = analPointsService.findById(point1.getId());
        assertTrue(beforeDelete.isPresent(), "Точка должна существовать до удаления");

        analPointsService.deleteById(point1.getId());

        Optional<anal_points> afterDelete = analPointsService.findById(point1.getId());
        assertFalse(afterDelete.isPresent(), "Точка должна быть удалена");

        List<anal_points> remainingPoints = analPointsService.findAll();
        assertEquals(4, remainingPoints.size(), "После удаления должно остаться 4 точки");
    }
}