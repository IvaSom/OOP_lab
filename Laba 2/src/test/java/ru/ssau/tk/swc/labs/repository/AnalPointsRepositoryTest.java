package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.entity.anal_points;
import ru.ssau.tk.swc.labs.functions.*;

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
class AnalPointsRepositoryTest {

    @Autowired
    private AnalPointsRepository analPointsRepository;

    @Autowired
    private AnalFunRepository analFunRepository;

    private analFun sinFunction;
    private analFun cosFunction;
    private analFun sqrFunction;

    private SinFunction sin = new SinFunction();
    private CosFunction cos = new CosFunction();
    private SqrFunction sqr = new SqrFunction();

    @BeforeEach
    void setUp() {
        analPointsRepository.deleteAll();
        analFunRepository.deleteAll();

        sinFunction = new analFun("sin", 1);
        cosFunction = new analFun("cos", 2);
        sqrFunction = new analFun("x^2", 3);

        sinFunction = analFunRepository.save(sinFunction);
        cosFunction = analFunRepository.save(cosFunction);
        sqrFunction = analFunRepository.save(sqrFunction);
    }

    @Test
    void testGenerateFindDelete() {
        anal_points point1 = new anal_points(0.0, sin.apply(0.0), sinFunction);
        anal_points point2 = new anal_points(Math.PI / 2, sin.apply(Math.PI / 2), sinFunction);
        anal_points point3 = new anal_points(0.0, cos.apply(0.0), cosFunction);
        anal_points point4 = new anal_points(2.0, sqr.apply(2.0), sqrFunction);
        anal_points point5 = new anal_points(3.0, sqr.apply(3.0), sqrFunction);

        anal_points saved1 = analPointsRepository.save(point1);
        anal_points saved2 = analPointsRepository.save(point2);
        anal_points saved3 = analPointsRepository.save(point3);
        anal_points saved4 = analPointsRepository.save(point4);
        anal_points saved5 = analPointsRepository.save(point5);

        assertEquals(5, analPointsRepository.count(), "Должно быть 5 точек в базе");

        List<anal_points> pointsWithX0 = analPointsRepository.findByX(0.0);
        assertEquals(2, pointsWithX0.size(), "Должно быть 2 точки с x=0.0");
        assertTrue(pointsWithX0.stream().anyMatch(p -> p.getY().equals(0.0)));
        assertTrue(pointsWithX0.stream().anyMatch(p -> p.getY().equals(1.0)));

        List<anal_points> pointsWithY1 = analPointsRepository.findByY(1.0);
        assertEquals(2, pointsWithY1.size(), "Должно быть 2 точки с y=1.0");

        List<anal_points> sinPoints = analPointsRepository.findByFunctionId(sinFunction.getId());
        assertEquals(2, sinPoints.size(), "Должно быть 2 точки для функции sin");

        List<anal_points> cosPoints = analPointsRepository.findByFunctionId(cosFunction.getId());
        assertEquals(1, cosPoints.size(), "Должна быть 1 точка для функции cos");

        List<anal_points> sqrPoints = analPointsRepository.findByFunctionId(sqrFunction.getId());
        assertEquals(2, sqrPoints.size(), "Должно быть 2 точки для функции x^2");

        Optional<anal_points> specificPoint = analPointsRepository.findByXAndFunctionId(Math.PI / 2, sinFunction.getId());
        assertTrue(specificPoint.isPresent(), "Должна быть найдена точка с x=pi/2 для функции sin");
        assertEquals(1.0, specificPoint.get().getY(), 0.001, "Y координата должна быть 1.0");

        Optional<anal_points> nonExistentPoint = analPointsRepository.findByXAndFunctionId(100.0, sinFunction.getId());
        assertFalse(nonExistentPoint.isPresent(), "Не должна быть найдена несуществующая точка");

        analPointsRepository.deleteById(saved1.getId());
        List<anal_points> remainingSinPoints = analPointsRepository.findByFunctionId(sinFunction.getId());
        assertEquals(1, remainingSinPoints.size(), "После удаления должна остаться 1 точка для функции sin");

        analPointsRepository.delete(saved2);
        remainingSinPoints = analPointsRepository.findByFunctionId(sinFunction.getId());
        assertTrue(remainingSinPoints.isEmpty(), "После удаления всех точек sin не должно остаться точек");

        analPointsRepository.deleteAll(sqrPoints);
        List<anal_points> remainingSqrPoints = analPointsRepository.findByFunctionId(sqrFunction.getId());
        assertTrue(remainingSqrPoints.isEmpty(), "После удаления не должно остаться точек для функции x^2");

        assertEquals(1, analPointsRepository.count(), "Должна остаться только 1 точка (cos)");
        List<anal_points> allRemainingPoints = analPointsRepository.findAll();
        assertEquals(1, allRemainingPoints.size());
        assertEquals(cosFunction.getId(), allRemainingPoints.get(0).getFunction().getId());

        analPointsRepository.deleteAll();
        assertEquals(0, analPointsRepository.count(), "После полной очистки не должно остаться точек");
    }

    @Test
    void testFindAllAndDeleteAll() {
        assertTrue(analPointsRepository.findAll().isEmpty(), "База должна быть пустой");

        anal_points point1 = new anal_points(1.0, sin.apply(1.0), sinFunction);
        anal_points point2 = new anal_points(2.0, sqr.apply(2.0), sqrFunction);

        analPointsRepository.save(point1);
        analPointsRepository.save(point2);

        List<anal_points> allPoints = analPointsRepository.findAll();
        assertEquals(2, allPoints.size(), "Должны быть найдены все 2 точки");


        analPointsRepository.deleteAll();
        analFunRepository.deleteAll();
        assertTrue(analPointsRepository.findAll().isEmpty(), "После deleteAll база должна быть пустой");
    }
}