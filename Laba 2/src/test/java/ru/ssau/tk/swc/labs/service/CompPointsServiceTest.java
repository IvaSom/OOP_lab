package ru.ssau.tk.swc.labs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.entity.comp_points;
import ru.ssau.tk.swc.labs.repository.CompFunRepository;
import ru.ssau.tk.swc.labs.repository.CompPointsRepository;

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
class CompPointsServiceTest {

    @Autowired
    private CompPointsRepository compPointsRepository;

    @Autowired
    private CompFunRepository compFunRepository;

    private CompPointsService compPointsService;

    private compFun compositeFunction1;
    private compFun compositeFunction2;
    private comp_points point1;
    private comp_points point2;
    private comp_points point3;
    private comp_points point4;
    private comp_points point5;
    private comp_points point6;

    @BeforeEach
    void setUp() {
        compPointsService = new CompPointsService(compPointsRepository);

        compPointsRepository.deleteAll();
        compFunRepository.deleteAll();

        compositeFunction1 = new compFun("composite_sin_cos");
        compositeFunction2 = new compFun("composite_sqr_sin");

        compositeFunction1 = compFunRepository.save(compositeFunction1);
        compositeFunction2 = compFunRepository.save(compositeFunction2);

        point1 = new comp_points(0.0, 0.841, compositeFunction1);
        point2 = new comp_points(Math.PI / 2, 0.0, compositeFunction1);
        point3 = new comp_points(Math.PI, -0.841, compositeFunction1);
        point4 = new comp_points(0.0, 0.0, compositeFunction2);
        point5 = new comp_points(Math.PI / 2, 1.0, compositeFunction2);
        point6 = new comp_points(Math.PI, 0.0, compositeFunction2);

        point1 = compPointsRepository.save(point1);
        point2 = compPointsRepository.save(point2);
        point3 = compPointsRepository.save(point3);
        point4 = compPointsRepository.save(point4);
        point5 = compPointsRepository.save(point5);
        point6 = compPointsRepository.save(point6);
    }

    @Test
    void testFindSinglePoint() {
        Optional<comp_points> result = compPointsService.findSinglePoint(0.0, compositeFunction1.getId());

        assertTrue(result.isPresent(), "Точка должна быть найдена");
        assertEquals(0.0, result.get().getX(), 0.001, "X координата должна быть 0.0");
        assertEquals(0.841, result.get().getY(), 0.001, "Y координата должна быть 0.841");
    }

    @Test
    void testFindSinglePointNotFound() {
        Optional<comp_points> result = compPointsService.findSinglePoint(100.0, compositeFunction1.getId());

        assertFalse(result.isPresent(), "Несуществующая точка не должна быть найдена");
    }

    @Test
    void testFindMultipleWithSortingByXAsc() {
        List<comp_points> result = compPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "x", "asc", compositeFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Первая точка должна иметь X=0.0");
        assertEquals(Math.PI / 2, result.get(1).getX(), 0.001, "Вторая точка должна иметь X=π/2");
        assertEquals(Math.PI, result.get(2).getX(), 0.001, "Третья точка должна иметь X=π");
    }

    @Test
    void testFindMultipleWithSortingByXDesc() {
        List<comp_points> result = compPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "x", "desc", compositeFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(Math.PI, result.get(0).getX(), 0.001, "Первая точка должна иметь X=π");
        assertEquals(Math.PI / 2, result.get(1).getX(), 0.001, "Вторая точка должна иметь X=π/2");
        assertEquals(0.0, result.get(2).getX(), 0.001, "Третья точка должна иметь X=0.0");
    }

    @Test
    void testFindMultipleWithSortingByYAsc() {
        List<comp_points> result = compPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "y", "asc", compositeFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(-0.841, result.get(0).getY(), 0.001, "Первая точка должна иметь Y=-0.841");
        assertEquals(0.0, result.get(1).getY(), 0.001, "Вторая точка должна иметь Y=0.0");
        assertEquals(0.841, result.get(2).getY(), 0.001, "Третья точка должна иметь Y=0.841");
    }

    @Test
    void testFindMultipleWithSortingByYDesc() {
        List<comp_points> result = compPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "y", "desc", compositeFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(0.841, result.get(0).getY(), 0.001, "Первая точка должна иметь Y=0.841");
        assertEquals(0.0, result.get(1).getY(), 0.001, "Вторая точка должна иметь Y=0.0");
        assertEquals(-0.841, result.get(2).getY(), 0.001, "Третья точка должна иметь Y=-0.841");
    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<comp_points> result = compPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "invalid", "asc", compositeFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(1).getId() < result.get(2).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithXFilter() {
        List<comp_points> result = compPointsService.findMultipleWithSorting(
                0.0, 1.0, -2.0, 2.0, "x", "asc", compositeFunction1.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка в диапазоне X [0.0, 1.0]");
        assertEquals(0.0, result.get(0).getX(), 0.001, "X координата должна быть 0.0");
    }

    @Test
    void testFindMultipleWithYFilter() {
        List<comp_points> result = compPointsService.findMultipleWithSorting(
                0.0, Math.PI, 0.5, 1.5, "x", "asc", compositeFunction2.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка в диапазоне Y [0.5, 1.5]");
        assertEquals(1.0, result.get(0).getY(), 0.001, "Y координата должна быть 1.0");
    }

    @Test
    void testBreadthFirstSearch() {
        List<comp_points> result = compPointsService.breadthFirstSearch(0.0, 2.0, compositeFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки поиском в ширину");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Первая точка должна иметь X=0.0");
    }

    @Test
    void testBreadthFirstSearchWithSmallRadius() {
        List<comp_points> result = compPointsService.breadthFirstSearch(0.0, 0.1, compositeFunction1.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка при малом радиусе");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Точка должна иметь X=0.0");
    }

    @Test
    void testBreadthFirstSearchStartPointNotFound() {
        List<comp_points> result = compPointsService.breadthFirstSearch(100.0, 2.0, compositeFunction1.getId());

        assertTrue(result.isEmpty(), "Поиск должен вернуть пустой список при отсутствии стартовой точки");
    }

    @Test
    void testFindByFunctionId() {
        List<comp_points> result = compPointsService.findByFunctionId(compositeFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки для функции composite_sin_cos");
        assertTrue(result.stream().allMatch(p -> p.getFunction().getId().equals(compositeFunction1.getId())),
                "Все точки должны принадлежать функции composite_sin_cos");
    }

    @Test
    void testFindByFunctionIdNotFound() {
        List<comp_points> result = compPointsService.findByFunctionId(999L);

        assertTrue(result.isEmpty(), "Не должно быть найдено точек для несуществующей функции");
    }

    @Test
    void testFindAll() {
        List<comp_points> result = compPointsService.findAll();

        assertEquals(6, result.size(), "Должно быть найдено 6 точек");
    }

    @Test
    void testFindById() {
        Optional<comp_points> result = compPointsService.findById(point1.getId());

        assertTrue(result.isPresent(), "Точка должна быть найдена по ID");
        assertEquals(point1.getId(), result.get().getId(), "ID точки должен совпадать");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<comp_points> result = compPointsService.findById(999L);

        assertFalse(result.isPresent(), "Несуществующая точка не должна быть найдена по ID");
    }

    @Test
    void testSave() {
        comp_points newPoint = new comp_points(1.0, 2.0, compositeFunction1);

        comp_points result = compPointsService.save(newPoint);

        assertNotNull(result, "Сохраненная точка не должна быть null");
        assertEquals(1.0, result.getX(), 0.001, "X координата должна быть 1.0");
        assertEquals(2.0, result.getY(), 0.001, "Y координата должна быть 2.0");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<comp_points> found = compPointsService.findById(result.getId());
        assertTrue(found.isPresent(), "Новая точка должна быть найдена в БД");
    }

    @Test
    void testDeleteById() {
        Optional<comp_points> beforeDelete = compPointsService.findById(point1.getId());
        assertTrue(beforeDelete.isPresent(), "Точка должна существовать до удаления");

        compPointsService.deleteById(point1.getId());

        Optional<comp_points> afterDelete = compPointsService.findById(point1.getId());
        assertFalse(afterDelete.isPresent(), "Точка должна быть удалена");

        List<comp_points> remainingPoints = compPointsService.findAll();
        assertEquals(5, remainingPoints.size(), "После удаления должно остаться 5 точек");
    }
}