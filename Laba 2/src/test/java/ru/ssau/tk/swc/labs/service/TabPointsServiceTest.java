package ru.ssau.tk.swc.labs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.repository.TabFunRepository;
import ru.ssau.tk.swc.labs.repository.TabPointsRepository;

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
class TabPointsServiceTest {

    @Autowired
    private TabPointsRepository tabPointsRepository;

    @Autowired
    private TabFunRepository tabFunRepository;

    private TabPointsService tabPointsService;

    private tabFun tabFunction1;
    private tabFun tabFunction2;
    private tab_points point1;
    private tab_points point2;
    private tab_points point3;
    private tab_points point4;
    private tab_points point5;
    private tab_points point6;

    @BeforeEach
    void setUp() {
        tabPointsService = new TabPointsService(tabPointsRepository);

        tabPointsRepository.deleteAll();
        tabFunRepository.deleteAll();

        tabFunction1 = new tabFun();
        tabFunction1.setName("tabulated_sin");
        tabFunction2 = new tabFun();
        tabFunction2.setName("tabulated_cos");

        tabFunction1 = tabFunRepository.save(tabFunction1);
        tabFunction2 = tabFunRepository.save(tabFunction2);

        point1 = new tab_points(0.0, 0.0, 1.0, tabFunction1);
        point2 = new tab_points(Math.PI / 2, 1.0, 0.0, tabFunction1);
        point3 = new tab_points(Math.PI, 0.0, -1.0, tabFunction1);
        point4 = new tab_points(0.0, 1.0, 0.0, tabFunction2);
        point5 = new tab_points(Math.PI / 2, 0.0, -1.0, tabFunction2);
        point6 = new tab_points(Math.PI, -1.0, 0.0, tabFunction2);

        point1 = tabPointsRepository.save(point1);
        point2 = tabPointsRepository.save(point2);
        point3 = tabPointsRepository.save(point3);
        point4 = tabPointsRepository.save(point4);
        point5 = tabPointsRepository.save(point5);
        point6 = tabPointsRepository.save(point6);
    }

    @Test
    void testFindSinglePoint() {
        Optional<tab_points> result = tabPointsService.findSinglePoint(0.0, tabFunction1.getId());

        assertTrue(result.isPresent(), "Точка должна быть найдена");
        assertEquals(0.0, result.get().getX(), 0.001, "X координата должна быть 0.0");
        assertEquals(0.0, result.get().getY(), 0.001, "Y координата должна быть 0.0");
        assertEquals(1.0, result.get().getDerive(), 0.001, "Производная должна быть 1.0");
    }

    @Test
    void testFindSinglePointNotFound() {
        Optional<tab_points> result = tabPointsService.findSinglePoint(100.0, tabFunction1.getId());

        assertFalse(result.isPresent(), "Несуществующая точка не должна быть найдена");
    }

    @Test
    void testFindMultipleWithSortingByXAsc() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "x", "asc", tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Первая точка должна иметь X=0.0");
        assertEquals(Math.PI / 2, result.get(1).getX(), 0.001, "Вторая точка должна иметь X=π/2");
        assertEquals(Math.PI, result.get(2).getX(), 0.001, "Третья точка должна иметь X=π");
    }

    @Test
    void testFindMultipleWithSortingByXDesc() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "x", "desc", tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(Math.PI, result.get(0).getX(), 0.001, "Первая точка должна иметь X=π");
        assertEquals(Math.PI / 2, result.get(1).getX(), 0.001, "Вторая точка должна иметь X=π/2");
        assertEquals(0.0, result.get(2).getX(), 0.001, "Третья точка должна иметь X=0.0");
    }

    @Test
    void testFindMultipleWithSortingByYAsc() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "y", "asc", tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(0.0, result.get(0).getY(), 0.001, "Первая точка должна иметь Y=0.0");
        assertEquals(0.0, result.get(1).getY(), 0.001, "Вторая точка должна иметь Y=0.0");
        assertEquals(1.0, result.get(2).getY(), 0.001, "Третья точка должна иметь Y=1.0");
    }

    @Test
    void testFindMultipleWithSortingByYDesc() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "y", "desc", tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(1.0, result.get(0).getY(), 0.001, "Первая точка должна иметь Y=1.0");
        assertEquals(0.0, result.get(1).getY(), 0.001, "Вторая точка должна иметь Y=0.0");
        assertEquals(0.0, result.get(2).getY(), 0.001, "Третья точка должна иметь Y=0.0");
    }

    @Test
    void testFindMultipleWithSortingByDeriveAsc() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "derive", "asc", tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(-1.0, result.get(0).getDerive(), 0.001, "Первая точка должна иметь производную -1.0");
        assertEquals(0.0, result.get(1).getDerive(), 0.001, "Вторая точка должна иметь производную 0.0");
        assertEquals(1.0, result.get(2).getDerive(), 0.001, "Третья точка должна иметь производную 1.0");
    }

    @Test
    void testFindMultipleWithSortingByDeriveDesc() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "derive", "desc", tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertEquals(1.0, result.get(0).getDerive(), 0.001, "Первая точка должна иметь производную 1.0");
        assertEquals(0.0, result.get(1).getDerive(), 0.001, "Вторая точка должна иметь производную 0.0");
        assertEquals(-1.0, result.get(2).getDerive(), 0.001, "Третья точка должна иметь производную -1.0");
    }

    @Test
    void testFindMultipleWithSortingByIdDefault() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, -2.0, 2.0, "invalid", "asc", tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки");
        assertTrue(result.get(0).getId() < result.get(1).getId(), "ID должны быть отсортированы по возрастанию");
        assertTrue(result.get(1).getId() < result.get(2).getId(), "ID должны быть отсортированы по возрастанию");
    }

    @Test
    void testFindMultipleWithXFilter() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, 1.0, -2.0, 2.0, "x", "asc", tabFunction1.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка в диапазоне X [0.0, 1.0]");
        assertEquals(0.0, result.get(0).getX(), 0.001, "X координата должна быть 0.0");
    }

    @Test
    void testFindMultipleWithYFilter() {
        List<tab_points> result = tabPointsService.findMultipleWithSorting(
                0.0, Math.PI, 0.5, 1.5, "x", "asc", tabFunction1.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка в диапазоне Y [0.5, 1.5]");
        assertEquals(1.0, result.get(0).getY(), 0.001, "Y координата должна быть 1.0");
    }

    @Test
    void testBreadthFirstSearch() {
        List<tab_points> result = tabPointsService.breadthFirstSearch(0.0, 2.0, tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки поиском в ширину");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Первая точка должна иметь X=0.0");
    }

    @Test
    void testBreadthFirstSearchWithSmallRadius() {
        List<tab_points> result = tabPointsService.breadthFirstSearch(0.0, 0.1, tabFunction1.getId());

        assertEquals(1, result.size(), "Должна быть найдена 1 точка при малом радиусе");
        assertEquals(0.0, result.get(0).getX(), 0.001, "Точка должна иметь X=0.0");
    }

    @Test
    void testBreadthFirstSearchStartPointNotFound() {
        List<tab_points> result = tabPointsService.breadthFirstSearch(100.0, 2.0, tabFunction1.getId());

        assertTrue(result.isEmpty(), "Поиск должен вернуть пустой список при отсутствии стартовой точки");
    }

    @Test
    void testFindByFunctionId() {
        List<tab_points> result = tabPointsService.findByFunctionId(tabFunction1.getId());

        assertEquals(3, result.size(), "Должно быть найдено 3 точки для функции tabulated_sin");
        assertTrue(result.stream().allMatch(p -> p.getFunction().getId().equals(tabFunction1.getId())),
                "Все точки должны принадлежать функции tabulated_sin");
    }

    @Test
    void testFindByFunctionIdNotFound() {
        List<tab_points> result = tabPointsService.findByFunctionId(999L);

        assertTrue(result.isEmpty(), "Не должно быть найдено точек для несуществующей функции");
    }

    @Test
    void testFindAll() {
        List<tab_points> result = tabPointsService.findAll();

        assertEquals(6, result.size(), "Должно быть найдено 6 точек");
    }

    @Test
    void testFindById() {
        Optional<tab_points> result = tabPointsService.findById(point1.getId());

        assertTrue(result.isPresent(), "Точка должна быть найдена по ID");
        assertEquals(point1.getId(), result.get().getId(), "ID точки должен совпадать");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<tab_points> result = tabPointsService.findById(999L);

        assertFalse(result.isPresent(), "Несуществующая точка не должна быть найдена по ID");
    }

    @Test
    void testSave() {
        tab_points newPoint = new tab_points(1.0, 2.0, 3.0, tabFunction1);

        tab_points result = tabPointsService.save(newPoint);

        assertNotNull(result, "Сохраненная точка не должна быть null");
        assertEquals(1.0, result.getX(), 0.001, "X координата должна быть 1.0");
        assertEquals(2.0, result.getY(), 0.001, "Y координата должна быть 2.0");
        assertEquals(3.0, result.getDerive(), 0.001, "Производная должна быть 3.0");
        assertNotNull(result.getId(), "ID должен быть установлен");

        Optional<tab_points> found = tabPointsService.findById(result.getId());
        assertTrue(found.isPresent(), "Новая точка должна быть найдена в БД");
    }

    @Test
    void testDeleteById() {
        Optional<tab_points> beforeDelete = tabPointsService.findById(point1.getId());
        assertTrue(beforeDelete.isPresent(), "Точка должна существовать до удаления");

        tabPointsService.deleteById(point1.getId());

        Optional<tab_points> afterDelete = tabPointsService.findById(point1.getId());
        assertFalse(afterDelete.isPresent(), "Точка должна быть удалена");

        List<tab_points> remainingPoints = tabPointsService.findAll();
        assertEquals(5, remainingPoints.size(), "После удаления должно остаться 5 точек");
    }
}