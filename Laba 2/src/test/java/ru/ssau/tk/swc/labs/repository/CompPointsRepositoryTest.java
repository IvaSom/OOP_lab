package ru.ssau.tk.swc.labs.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.entity.comp_points;
import ru.ssau.tk.swc.labs.entity.composite_structure;
import ru.ssau.tk.swc.labs.functions.MathFunction;
import ru.ssau.tk.swc.labs.service.CreateCompositeFunctionService;
import ru.ssau.tk.swc.labs.service.MathFunctionFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import({CreateCompositeFunctionService.class, MathFunctionFactory.class}) // Добавляем импорт сервисов
@Transactional
class CompPointsRepositoryTest {

    @Autowired
    private CompPointsRepository compPointsRepository;

    @Autowired
    private CompFunRepository compFunRepository;

    @Autowired
    private AnalFunRepository analFunRepository;

    @Autowired
    private CompStructureRepository compStructureRepository;

    @Autowired
    private CreateCompositeFunctionService createCompositeFunctionService;

    private compFun compositeFunction1; // sin(cos(x))
    private compFun compositeFunction2; // (sin(x))^2

    private analFun sinFunction;
    private analFun cosFunction;
    private analFun sqrFunction;

    @BeforeEach
    void setUp() {
        compPointsRepository.deleteAll();
        compStructureRepository.deleteAll();
        compFunRepository.deleteAll();
        analFunRepository.deleteAll();

        sinFunction = new analFun("sin", 1);
        cosFunction = new analFun("cos", 2);
        sqrFunction = new analFun("x^2", 3);

        sinFunction = analFunRepository.save(sinFunction);
        cosFunction = analFunRepository.save(cosFunction);
        sqrFunction = analFunRepository.save(sqrFunction);

        //создание композитных функций
        compositeFunction1 = new compFun("composite_sin_cos"); // sin(cos(x))
        compositeFunction2 = new compFun("composite_sqr_sin"); // (sin(x))^2

        compositeFunction1 = compFunRepository.save(compositeFunction1);
        compositeFunction2 = compFunRepository.save(compositeFunction2);

        //создание структур для композитных функций
        //compositeFunction1: сначала cos, потом sin (sin(cos(x)))
        composite_structure structure1 = new composite_structure(compositeFunction1, cosFunction, 1);
        composite_structure structure2 = new composite_structure(compositeFunction1, sinFunction, 2);

        //compositeFunction2: сначала sin, потом x^2 ((sin(x))^2)
        composite_structure structure3 = new composite_structure(compositeFunction2, sinFunction, 1);
        composite_structure structure4 = new composite_structure(compositeFunction2, sqrFunction, 2);

        compStructureRepository.save(structure1);
        compStructureRepository.save(structure2);
        compStructureRepository.save(structure3);
        compStructureRepository.save(structure4);
    }

    @Test
    void testGenerateFindDelete() {
        MathFunction compositeFunc1 = createCompositeFunctionService.buildCompositeFunction(compositeFunction1); // sin(cos(x))
        MathFunction compositeFunc2 = createCompositeFunctionService.buildCompositeFunction(compositeFunction2); // (sin(x))^2

        comp_points point1 = new comp_points(0.0, compositeFunc1.apply(0.0), compositeFunction1);
        comp_points point2 = new comp_points(Math.PI / 2, compositeFunc1.apply(Math.PI / 2), compositeFunction1);
        comp_points point3 = new comp_points(Math.PI, compositeFunc1.apply(Math.PI), compositeFunction1);
        comp_points point4 = new comp_points(0.0, compositeFunc2.apply(0.0), compositeFunction2);
        comp_points point5 = new comp_points(Math.PI / 2, compositeFunc2.apply(Math.PI / 2), compositeFunction2);
        comp_points point6 = new comp_points(Math.PI, compositeFunc2.apply(Math.PI), compositeFunction2);

        comp_points saved1 = compPointsRepository.save(point1);
        comp_points saved2 = compPointsRepository.save(point2);
        comp_points saved3 = compPointsRepository.save(point3);
        comp_points saved4 = compPointsRepository.save(point4);
        comp_points saved5 = compPointsRepository.save(point5);
        comp_points saved6 = compPointsRepository.save(point6);

        assertEquals(6, compPointsRepository.count(), "должно быть 6 точек в базе");

        Optional<comp_points> foundPoint1 = compPointsRepository.findByXAndFunctionId(0.0, compositeFunction1.getId());
        assertTrue(foundPoint1.isPresent(), "Точка с x=0.0 для compositeFunction1 должна быть найдена");
        assertEquals(Math.sin(Math.cos(0.0)), foundPoint1.get().getY(), 0.001, "Y координата должна быть sin(cos(0))");

        Optional<comp_points> foundPoint2 = compPointsRepository.findByXAndFunctionId(Math.PI / 2, compositeFunction1.getId());
        assertTrue(foundPoint2.isPresent(), "Точка с x=pi/2 для compositeFunction1 должна быть найдена");
        assertEquals(Math.sin(Math.cos(Math.PI / 2)), foundPoint2.get().getY(), 0.001, "Y координата должна быть sin(cos(pi/2))");

        Optional<comp_points> foundPoint3 = compPointsRepository.findByXAndFunctionId(Math.PI, compositeFunction1.getId());
        assertTrue(foundPoint3.isPresent(), "Точка с x=pi для compositeFunction1 должна быть найдена");
        assertEquals(Math.sin(Math.cos(Math.PI)), foundPoint3.get().getY(), 0.001, "Y координата должна быть sin(cos(pi))");


        Optional<comp_points> foundPoint4 = compPointsRepository.findByXAndFunctionId(0.0, compositeFunction2.getId());
        assertTrue(foundPoint4.isPresent(), "Точка с x=0.0 для compositeFunction2 должна быть найдена");
        assertEquals(Math.pow(Math.sin(0.0), 2), foundPoint4.get().getY(), 0.001, "Y координата должна быть (sin(0))^2");

        Optional<comp_points> foundPoint5 = compPointsRepository.findByXAndFunctionId(Math.PI / 2, compositeFunction2.getId());
        assertTrue(foundPoint5.isPresent(), "Точка с x=pi/2 для compositeFunction2 должна быть найдена");
        assertEquals(Math.pow(Math.sin(Math.PI / 2), 2), foundPoint5.get().getY(), 0.001, "Y координата должна быть (sin(pi/2))^2");

        Optional<comp_points> foundPoint6 = compPointsRepository.findByXAndFunctionId(Math.PI, compositeFunction2.getId());
        assertTrue(foundPoint6.isPresent(), "Точка с x=pi для compositeFunction2 должна быть найдена");
        assertEquals(Math.pow(Math.sin(Math.PI), 2), foundPoint6.get().getY(), 0.001, "Y координата должна быть (sin(pi))^2");

        //проверка что композиная функция создает правильные значения
        assertEquals(0.841, foundPoint1.get().getY(), 0.001, "sin(cos(0)) = 0.841");
        assertEquals(0.0, foundPoint2.get().getY(), 0.001, "sin(cos(pi/2)) = 0");
        assertEquals(-0.841, foundPoint3.get().getY(), 0.001, "sin(cos(pi)) = -0.841");
        assertEquals(0.0, foundPoint4.get().getY(), 0.001, "(sin(0))^2 = 0");
        assertEquals(1.0, foundPoint5.get().getY(), 0.001, "(sin(pi/2))^2 = 1");
        assertEquals(0.0, foundPoint6.get().getY(), 0.001, "(sin(pi))^2 = 0");

        Optional<comp_points> notFound = compPointsRepository.findByXAndFunctionId(100.0, compositeFunction1.getId());
        assertFalse(notFound.isPresent(), "Несуществующая точка не должна быть найдена");

        List<comp_points> allPoints = compPointsRepository.findAll();
        assertEquals(6, allPoints.size(), "Должны быть найдены все 6 точек");

        //удаление
        compPointsRepository.deleteById(foundPoint1.get().getId());
        Optional<comp_points> deletedPoint1 = compPointsRepository.findByXAndFunctionId(0.0, compositeFunction1.getId());
        assertFalse(deletedPoint1.isPresent(), "Точка с x=0.0 для compositeFunction1 должна быть удалена");

        assertEquals(5, compPointsRepository.count(), "После удаления должно остаться 5 точек");

        compPointsRepository.delete(foundPoint2.get());
        Optional<comp_points> deletedPoint2 = compPointsRepository.findByXAndFunctionId(Math.PI / 2, compositeFunction1.getId());
        assertFalse(deletedPoint2.isPresent(), "Точка с x=pi/2 для compositeFunction1 должна быть удалена");

        assertEquals(4, compPointsRepository.count(), "После удаления должно остаться 4 точки");

        compPointsRepository.delete(foundPoint5.get());
        Optional<comp_points> deletedPoint5 = compPointsRepository.findByXAndFunctionId(Math.PI / 2, compositeFunction2.getId());
        assertFalse(deletedPoint5.isPresent(), "Точка с x=pi/2 для compositeFunction2 должна быть удалена");

        assertEquals(3, compPointsRepository.count(), "Должно остаться 3 точки");

        Optional<comp_points> remainingPoint3 = compPointsRepository.findByXAndFunctionId(Math.PI, compositeFunction1.getId());
        assertTrue(remainingPoint3.isPresent(), "Точка с x=pi для compositeFunction1 должна остаться в базе");

        Optional<comp_points> remainingPoint4 = compPointsRepository.findByXAndFunctionId(0.0, compositeFunction2.getId());
        assertTrue(remainingPoint4.isPresent(), "Точка с x=0.0 для compositeFunction2 должна остаться в базе");

        Optional<comp_points> remainingPoint6 = compPointsRepository.findByXAndFunctionId(Math.PI, compositeFunction2.getId());
        assertTrue(remainingPoint6.isPresent(), "Точка с x=pi для compositeFunction2 должна остаться в базе");



        compPointsRepository.deleteAll();
        compStructureRepository.deleteAll();
        compFunRepository.deleteAll();
        analFunRepository.deleteAll();
        assertEquals(0, compPointsRepository.count(), "После полной очистки не должно остаться точек");
    }
}