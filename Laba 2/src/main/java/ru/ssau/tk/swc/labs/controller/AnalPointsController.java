package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.functions.MathFunction;
import ru.ssau.tk.swc.labs.service.AnalPointsService;
import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.repository.*;
import ru.ssau.tk.swc.labs.service.MathFunctionFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anal-points")
public class AnalPointsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalPointsController.class);

    @Autowired
    private AnalPointsService service;

    @Autowired
    private AnalFunRepository analFunRepository;
    @Autowired
    private MathFunctionFactory mathFunctionFactory;

    @GetMapping
    public List<AnalPointsDTO> getAllPoints() {
        logger.info("GET /api/anal-points - Получение всех точек");
        List<AnalPointsDTO> points = service.findAll().stream()
                .map(AnalPointsDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/anal-points - Найдено {} точек", points.size());
        return points;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalPointsDTO> getPointById(@PathVariable Long id) {
        logger.info("GET /api/anal-points/{} - Получение точки по ID", id);
        Optional<anal_points> point = service.findById(id);
        if (point.isPresent()) {
            logger.info("GET /api/anal-points/{} - Точка найдена", id);
            return ResponseEntity.ok(new AnalPointsDTO(point.get()));
        } else {
            logger.warn("GET /api/anal-points/{} - Точка не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/function/{funId}")
    public List<AnalPointsDTO> getPointsByFunction(@PathVariable Long funId) {
        logger.info("GET /api/anal-points/function/{} - Получение точек по функции", funId);
        List<AnalPointsDTO> points = service.findByFunctionId(funId).stream()
                .map(AnalPointsDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/anal-points/function/{} - Найдено {} точек для функции", funId, points.size());
        return points;
    }

    @GetMapping("/point")
    public ResponseEntity<AnalPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {
        logger.info("GET /api/anal-points/point - Поиск точки по x={} и functionId={}", x, functionId);
        Optional<anal_points> point = service.findSinglePoint(x, functionId);
        if (point.isPresent()) {
            logger.info("GET /api/anal-points/point - Точка найдена");
            return ResponseEntity.ok(new AnalPointsDTO(point.get()));
        } else {
            logger.warn("GET /api/anal-points/point - Точка не найдена для x={} и functionId={}", x, functionId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AnalPointsDTO createPoint(@RequestBody CreateAnalPointDTO createPointDTO) {
        logger.info("POST /api/anal-points - Создание новой точки для X: {}, FunctionID: {}",
                createPointDTO.getX(), createPointDTO.getFunctionId());

        analFun function = analFunRepository.findById(createPointDTO.getFunctionId())
                .orElseThrow(() -> {
                    logger.error("POST /api/anal-points - Функция не найдена с ID: {}", createPointDTO.getFunctionId());
                    return new RuntimeException("Function not found with id: " + createPointDTO.getFunctionId());
                });

        //y вычисляется
        MathFunction mathFunction = mathFunctionFactory.createMathFunction(function);
        Double calculatedY = mathFunction.apply(createPointDTO.getX());

        logger.info("Вычислено значение Y: {} для X: {} с функцией: {} (тип: {})",
                calculatedY, createPointDTO.getX(), function.getName(), function.getType());

        anal_points point = new anal_points();
        point.setX(createPointDTO.getX());
        point.setY(calculatedY);
        point.setFunction(function);

        anal_points saved = service.save(point);
        logger.info("POST /api/anal-points - Точка создана с ID: {}", saved.getId());
        return new AnalPointsDTO(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalPointsDTO> updatePoint(@PathVariable Long id, @RequestBody CreateAnalPointDTO updatePointDTO) {
        logger.info("PUT /api/anal-points/{} - Обновление точки", id);

        // находим точку
        anal_points existingPoint = service.findById(id)
                .orElseThrow(() -> {
                    logger.error("PUT /api/anal-points/{} - Точка не найдена", id);
                    return new RuntimeException("Point not found with id: " + id);
                });

        //находим функцию
        analFun function = analFunRepository.findById(updatePointDTO.getFunctionId())
                .orElseThrow(() -> {
                    logger.error("PUT /api/anal-points/{} - Функция не найдена с ID: {}", id, updatePointDTO.getFunctionId());
                    return new RuntimeException("Function not found with id: " + updatePointDTO.getFunctionId());
                });


        MathFunction mathFunction = mathFunctionFactory.createMathFunction(function);
        Double calculatedY = mathFunction.apply(updatePointDTO.getX());

        logger.info("Для X: {} вычислено Y: {} с функцией: {} (тип: {})",
                updatePointDTO.getX(), calculatedY, function.getName(), function.getType());

        existingPoint.setX(updatePointDTO.getX());
        existingPoint.setY(calculatedY);
        existingPoint.setFunction(function);

        anal_points updated = service.save(existingPoint);
        logger.info("PUT /api/anal-points/{} - Точка успешно обновлена", id);
        return ResponseEntity.ok(new AnalPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        logger.info("DELETE /api/anal-points/{} - Удаление точки", id);
        service.deleteById(id);
        logger.info("DELETE /api/anal-points/{} - Точка успешно удалена", id);
        return ResponseEntity.ok().build();
    }
}