package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.service.TabFunService;
import ru.ssau.tk.swc.labs.service.TabPointsService;
import ru.ssau.tk.swc.labs.operations.TabulatedFunctionOperationService;
import ru.ssau.tk.swc.labs.functions.TabulatedFunction;
import ru.ssau.tk.swc.labs.functions.Point;
import ru.ssau.tk.swc.labs.functions.factory.ArrayTabulatedFunctionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/functions/operations")
public class FunctionOperationsController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionOperationsController.class);

    @Autowired
    private TabFunService tabFunService;

    @Autowired
    private TabPointsService tabPointsService;

    @Autowired
    private TabulatedFunctionOperationService operationService;

    /**
     * Выполнение операции над двумя табулированными функциями
     */
    @PostMapping("/tabulated")
    public ResponseEntity<TabulatedFunctionWithPointsDTO> performTabulatedOperation(
            @RequestBody FunctionOperationRequest request) {

        logger.info("POST /api/functions/operations/tabulated - Операция: {} между функциями {} и {}",
                request.getOperation(), request.getFunctionId1(), request.getFunctionId2());

        try {
            // 1. Получаем функции из БД
            Optional<tabFun> func1Opt = tabFunService.findSingleFunctionById(request.getFunctionId1());
            Optional<tabFun> func2Opt = tabFunService.findSingleFunctionById(request.getFunctionId2());

            if (func1Opt.isEmpty() || func2Opt.isEmpty()) {
                logger.error("Функции не найдены: {} или {}", request.getFunctionId1(), request.getFunctionId2());
                return ResponseEntity.notFound().build();
            }

            tabFun func1 = func1Opt.get();
            tabFun func2 = func2Opt.get();

            // 2. Получаем точки функций
            List<tab_points> points1 = tabPointsService.findByFunctionId(func1.getId());
            List<tab_points> points2 = tabPointsService.findByFunctionId(func2.getId());

            if (points1.isEmpty() || points2.isEmpty()) {
                logger.error("Нет точек для функций: {} или {}", func1.getId(), func2.getId());
                return ResponseEntity.badRequest().build();
            }

            // 3. Преобразуем в TabulatedFunction
            TabulatedFunction tabFunc1 = createTabulatedFunction(points1);
            TabulatedFunction tabFunc2 = createTabulatedFunction(points2);

            // 4. Выполняем операцию
            TabulatedFunction resultFunction;
            switch (request.getOperation().toLowerCase()) {
                case "sum":
                    resultFunction = operationService.sum(tabFunc1, tabFunc2);
                    break;
                case "subtract":
                    resultFunction = operationService.subtraction(tabFunc1, tabFunc2);
                    break;
                case "multiply":
                    resultFunction = operationService.multiplication(tabFunc1, tabFunc2);
                    break;
                case "divide":
                    resultFunction = operationService.division(tabFunc1, tabFunc2);
                    break;
                default:
                    logger.error("Неизвестная операция: {}", request.getOperation());
                    return ResponseEntity.badRequest().build();
            }

            // 5. Создаем новую функцию в БД для результата
            String resultName = String.format("%s %s %s",
                    func1.getName(),
                    getOperationSymbol(request.getOperation()),
                    func2.getName());

            tabFun resultTabFun = new tabFun();
            resultTabFun.setName(resultName);
            resultTabFun = tabFunService.save(resultTabFun);

            // 6. Сохраняем точки результата в БД
            List<TabPointsDTO> resultPointsDTOs = new ArrayList<>();
            Point[] resultPoints = TabulatedFunctionOperationService.asPoints(resultFunction);

            for (Point point : resultPoints) {
                tab_points tabPoint = new tab_points();
                tabPoint.setX(point.x);
                tabPoint.setY(point.y);
                tabPoint.setDerive(0.0); // производная пока не вычислена
                tabPoint.setFunction(resultTabFun);

                tab_points savedPoint = tabPointsService.save(tabPoint);
                resultPointsDTOs.add(new TabPointsDTO(savedPoint));
            }

            // 7. Формируем ответ
            TabFunDTO resultTabFunDTO = new TabFunDTO(resultTabFun);
            TabulatedFunctionWithPointsDTO resultDTO = new TabulatedFunctionWithPointsDTO(
                    resultTabFunDTO,
                    resultPointsDTOs
            );

            logger.info("Операция выполнена успешно. Создана функция с ID: {}", resultTabFun.getId());
            return ResponseEntity.ok(resultDTO);

        } catch (Exception e) {
            logger.error("Ошибка при выполнении операции: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/tabulated/{functionId}/differentiate")
    public ResponseEntity<TabulatedFunctionWithPointsDTO> differentiateTabulatedFunction(
            @PathVariable Long functionId) {

        logger.info("POST /api/functions/operations/tabulated/{}/differentiate", functionId);

        try {
            // 1. Получаем функцию из БД
            Optional<tabFun> funcOpt = tabFunService.findSingleFunctionById(functionId);
            if (funcOpt.isEmpty()) {
                logger.error("Функция не найдена: {}", functionId);
                return ResponseEntity.notFound().build();
            }

            tabFun func = funcOpt.get();

            // 2. Получаем точки функции
            List<tab_points> points = tabPointsService.findByFunctionId(func.getId());
            if (points.isEmpty()) {
                logger.error("Нет точек для функции: {}", func.getId());
                return ResponseEntity.badRequest().build();
            }

            // 3. Преобразуем в TabulatedFunction
            TabulatedFunction tabFunc = createTabulatedFunction(points);

            // 4. Создаем оператор дифференцирования
            ru.ssau.tk.swc.labs.operations.TabulatedDifferentialOperator diffOperator =
                    new ru.ssau.tk.swc.labs.operations.TabulatedDifferentialOperator();

            // 5. Выполняем дифференцирование
            TabulatedFunction diffResult = diffOperator.derive(tabFunc);

            // 6. Создаем новую функцию в БД для результата
            tabFun resultTabFun = new tabFun();
            resultTabFun.setName(func.getName() + " (производная)");
            resultTabFun = tabFunService.save(resultTabFun);

            List<TabPointsDTO> resultPointsDTOs = new ArrayList<>();
            Point[] resultPoints = TabulatedFunctionOperationService.asPoints(diffResult);

            for (Point point : resultPoints) {
                tab_points tabPoint = new tab_points();
                tabPoint.setX(point.x);
                tabPoint.setY(point.y);
                tabPoint.setDerive(0.0);
                tabPoint.setFunction(resultTabFun);

                tab_points savedPoint = tabPointsService.save(tabPoint);
                resultPointsDTOs.add(new TabPointsDTO(savedPoint));
            }

            TabFunDTO resultTabFunDTO = new TabFunDTO(resultTabFun);
            TabulatedFunctionWithPointsDTO resultDTO = new TabulatedFunctionWithPointsDTO(
                    resultTabFunDTO,
                    resultPointsDTOs
            );

            logger.info("Дифференцирование выполнено успешно. Создана функция с ID: {}", resultTabFun.getId());
            return ResponseEntity.ok(resultDTO);

        } catch (Exception e) {
            logger.error("Ошибка при дифференцировании: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private TabulatedFunction createTabulatedFunction(List<tab_points> points) {
        int count = points.size();
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            xValues[i] = points.get(i).getX();
            yValues[i] = points.get(i).getY();
        }

        ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        return factory.create(xValues, yValues);
    }

    private String getOperationSymbol(String operation) {
        switch (operation.toLowerCase()) {
            case "sum": return "+";
            case "subtract": return "-";
            case "multiply": return "*";
            case "divide": return "/";
            default: return "?";
        }
    }
}