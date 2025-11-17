package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.entity.tab_points;

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
class TabPointsRepositoryTest {

    @Autowired
    private TabPointsRepository tabPointsRepository;

    @Autowired
    private TabFunRepository tabFunRepository;

    private tabFun tableFunction1;
    private tabFun tableFunction2;

    @BeforeEach
    void setUp() {
        tabPointsRepository.deleteAll();
        tabFunRepository.deleteAll();

        tableFunction1 = new tabFun();
        tableFunction1.setName("table_sin");

        tableFunction2 = new tabFun();
        tableFunction2.setName("table_cos");

        tableFunction1 = tabFunRepository.save(tableFunction1);
        tableFunction2 = tabFunRepository.save(tableFunction2);
    }

    @Test
    void testGenerateFindDelete() {

        tab_points point1 = new tab_points(0.0, 0.0, 1.0, tableFunction1);
        tab_points point2 = new tab_points(1.0, 0.84, 0.54, tableFunction1);
        tab_points point3 = new tab_points(0.0, 1.0, 0.0, tableFunction2);
        tab_points point4 = new tab_points(2.0, -0.42, -0.91, tableFunction2);
        tab_points point5 = new tab_points(3.0, 0.14, -0.99, tableFunction2);

        tab_points saved1 = tabPointsRepository.save(point1);
        tab_points saved2 = tabPointsRepository.save(point2);
        tab_points saved3 = tabPointsRepository.save(point3);
        tab_points saved4 = tabPointsRepository.save(point4);
        tab_points saved5 = tabPointsRepository.save(point5);

        assertEquals(5, tabPointsRepository.count(), "должно быть 5 точек в базе");

        //поиск по X и functionId
        Optional<tab_points> foundPoint1 = tabPointsRepository.findByXAndFunctionId(0.0, tableFunction1.getId());
        assertTrue(foundPoint1.isPresent(), "Точка с x=0.0 для tableFunction1 должна быть найдена");
        assertEquals(0.0, foundPoint1.get().getY(), 0.001, "Y координата должна быть 0.0");
        assertEquals(1.0, foundPoint1.get().getDerive(), 0.001, "Производная должна быть 1.0");

        Optional<tab_points> foundPoint2 = tabPointsRepository.findByXAndFunctionId(1.0, tableFunction1.getId());
        assertTrue(foundPoint2.isPresent(), "Точка с x=1.0 для tableFunction1 должна быть найдена");
        assertEquals(0.84, foundPoint2.get().getY(), 0.001, "Y координата должна быть 0.84");
        assertEquals(0.54, foundPoint2.get().getDerive(), 0.001, "Производная должна быть 0.54");

        Optional<tab_points> foundPoint3 = tabPointsRepository.findByXAndFunctionId(0.0, tableFunction2.getId());
        assertTrue(foundPoint3.isPresent(), "Точка с x=0.0 для tableFunction2 должна быть найдена");
        assertEquals(1.0, foundPoint3.get().getY(), 0.001, "Y координата должна быть 1.0");
        assertEquals(0.0, foundPoint3.get().getDerive(), 0.001, "Производная должна быть 0.0");

        Optional<tab_points> foundPoint4 = tabPointsRepository.findByXAndFunctionId(2.0, tableFunction2.getId());
        assertTrue(foundPoint4.isPresent(), "Точка с x=2.0 для tableFunction2 должна быть найдена");
        assertEquals(-0.42, foundPoint4.get().getY(), 0.001, "Y координата должна быть -0.42");
        assertEquals(-0.91, foundPoint4.get().getDerive(), 0.001, "Производная должна быть -0.91");

        Optional<tab_points> foundPoint5 = tabPointsRepository.findByXAndFunctionId(3.0, tableFunction2.getId());
        assertTrue(foundPoint5.isPresent(), "Точка с x=3.0 для tableFunction2 должна быть найдена");
        assertEquals(0.14, foundPoint5.get().getY(), 0.001, "Y координата должна быть 0.14");
        assertEquals(-0.99, foundPoint5.get().getDerive(), 0.001, "Производная должна быть -0.99");

        //поиск несуществующей точки
        Optional<tab_points> notFound = tabPointsRepository.findByXAndFunctionId(100.0, tableFunction1.getId());
        assertFalse(notFound.isPresent(), "Несуществующая точка не должна быть найдена");

        //поиск всех точек
        List<tab_points> allPoints = tabPointsRepository.findAll();
        assertEquals(5, allPoints.size(), "Должны быть найдены все 5 точек");

        //удаление
        tabPointsRepository.deleteById(foundPoint1.get().getId());
        Optional<tab_points> deletedPoint1 = tabPointsRepository.findByXAndFunctionId(0.0, tableFunction1.getId());
        assertFalse(deletedPoint1.isPresent(), "Точка с x=0.0 для tableFunction1 должна быть удалена");

        assertEquals(4, tabPointsRepository.count(), "После удаления должно остаться 4 точки");

        tabPointsRepository.delete(foundPoint2.get());
        Optional<tab_points> deletedPoint2 = tabPointsRepository.findByXAndFunctionId(1.0, tableFunction1.getId());
        assertFalse(deletedPoint2.isPresent(), "Точка с x=1.0 для tableFunction1 должна быть удалена");

        assertEquals(3, tabPointsRepository.count(), "После удаления должно остаться 3 точки");

        tabPointsRepository.delete(foundPoint3.get());
        Optional<tab_points> deletedPoint3 = tabPointsRepository.findByXAndFunctionId(0.0, tableFunction2.getId());
        assertFalse(deletedPoint3.isPresent(), "Точка с x=0.0 для tableFunction2 должна быть удалена");

        assertEquals(2, tabPointsRepository.count(), "Должно остаться 2 точки");
        Optional<tab_points> remainingPoint1 = tabPointsRepository.findByXAndFunctionId(2.0, tableFunction2.getId());
        assertTrue(remainingPoint1.isPresent(), "Точка с x=2.0 для tableFunction2 должна остаться в базе");

        Optional<tab_points> remainingPoint2 = tabPointsRepository.findByXAndFunctionId(3.0, tableFunction2.getId());
        assertTrue(remainingPoint2.isPresent(), "Точка с x=3.0 для tableFunction2 должна остаться в базе");

        tabPointsRepository.delete(foundPoint4.get());
        Optional<tab_points> deletedPoint4 = tabPointsRepository.findByXAndFunctionId(2.0, tableFunction2.getId());
        assertFalse(deletedPoint4.isPresent(), "Точка с x=2.0 для tableFunction2 должна быть удалена");

        tabPointsRepository.delete(foundPoint5.get());
        Optional<tab_points> deletedPoint5 = tabPointsRepository.findByXAndFunctionId(3.0, tableFunction2.getId());
        assertFalse(deletedPoint5.isPresent(), "Точка с x=3.0 для tableFunction2 должна быть удалена");

        tabPointsRepository.deleteAll();
        tabFunRepository.deleteAll();
    }

}