package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.CompPointsDTO;
import ru.ssau.tk.swc.labs.entity.comp_points;
import ru.ssau.tk.swc.labs.functions.MathFunction;
import ru.ssau.tk.swc.labs.service.CompPointsService;
import ru.ssau.tk.swc.labs.service.CreateCompositeFunctionService;

import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comp-points")
public class CompPointsController {

    private static final Logger logger = LoggerFactory.getLogger(CompPointsController.class);

    @Autowired
    private CompPointsService service;
    @Autowired
    private CompFunRepository compFunRepository;
    @Autowired
    private CreateCompositeFunctionService createCompositeFunctionService;

    @GetMapping
    public List<CompPointsDTO> getAllPoints() {
        logger.info("GET /api/comp-points - Получение всех точек композитных функций");
        List<CompPointsDTO> points = service.findAll().stream()
                .map(CompPointsDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/comp-points - Найдено {} точек композитных функций", points.size());
        return points;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompPointsDTO> getPointById(@PathVariable Long id) {
        logger.info("GET /api/comp-points/{} - Получение точки композитной функции по ID", id);
        Optional<comp_points> point = service.findById(id);
        if (point.isPresent()) {
            logger.info("GET /api/comp-points/{} - Точка композитной функции найдена", id);
            return ResponseEntity.ok(new CompPointsDTO(point.get()));
        } else {
            logger.warn("GET /api/comp-points/{} - Точка композитной функции не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/function/{funId}")
    public List<CompPointsDTO> getPointsByFunction(@PathVariable Long funId) {
        logger.info("GET /api/comp-points/function/{} - Получение точек по композитной функции", funId);
        List<CompPointsDTO> points = service.findByFunctionId(funId).stream()
                .map(CompPointsDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/comp-points/function/{} - Найдено {} точек для композитной функции", funId, points.size());
        return points;
    }

    @GetMapping("/point")
    public ResponseEntity<CompPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {
        logger.info("GET /api/comp-points/point - Поиск точки по x={} и functionId={}", x, functionId);
        Optional<comp_points> point = service.findSinglePoint(x, functionId);
        if (point.isPresent()) {
            logger.info("GET /api/comp-points/point - Точка найдена для x={} и functionId={}", x, functionId);
            return ResponseEntity.ok(new CompPointsDTO(point.get()));
        } else {
            logger.warn("GET /api/comp-points/point - Точка не найдена для x={} и functionId={}", x, functionId);
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    public CompPointsDTO createPoint(@RequestBody CompPointCreateDTO createDTO) {
        logger.info("POST /api/comp-points - Создание новой точки композитной функции");

        compFun function = compFunRepository.findById(createDTO.getFunctionId())
                .orElseThrow(() -> {
                    logger.error("POST /api/comp-points - Композитная функция не найдена с ID: {}", createDTO.getFunctionId());
                    return new RuntimeException("Composite function not found with id: " + createDTO.getFunctionId());
                });

        MathFunction compositeMathFunction = createCompositeFunctionService.buildCompositeFunction(function);

        Double calculatedY = compositeMathFunction.apply(createDTO.getX());

        comp_points point = new comp_points();
        point.setX(createDTO.getX());
        point.setY(calculatedY);
        point.setFunction(function);

        comp_points saved = service.save(point);
        logger.info("POST /api/comp-points - Точка композитной функции создана с ID: {}, вычислено y = {}", saved.getId(), calculatedY);
        return new CompPointsDTO(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompPointsDTO> updatePoint(@PathVariable Long id, @RequestBody CompPointCreateDTO createDTO) {
        logger.info("PUT /api/comp-points/{} - Обновление точки композитной функции", id);

        comp_points existingPoint = service.findById(id)
                .orElseThrow(() -> {
                    logger.error("PUT /api/comp-points/{} - Точка композитной функции не найдена", id);
                    return new RuntimeException("Composite point not found with id: " + id);
                });

        compFun function = compFunRepository.findById(createDTO.getFunctionId())
                .orElseThrow(() -> {
                    logger.error("PUT /api/comp-points/{} - Композитная функция не найдена с ID: {}", id, createDTO.getFunctionId());
                    return new RuntimeException("Composite function not found with id: " + createDTO.getFunctionId());
                });

        MathFunction compositeMathFunction = createCompositeFunctionService.buildCompositeFunction(function);
        Double calculatedY = compositeMathFunction.apply(createDTO.getX());

        existingPoint.setX(createDTO.getX());
        existingPoint.setY(calculatedY);
        existingPoint.setFunction(function);

        comp_points updated = service.save(existingPoint);
        logger.info("PUT /api/comp-points/{} - Точка композитной функции успешно обновлена, вычислено y = {}", id, calculatedY);
        return ResponseEntity.ok(new CompPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        logger.info("DELETE /api/comp-points/{} - Удаление точки композитной функции", id);
        service.deleteById(id);
        logger.info("DELETE /api/comp-points/{} - Точка композитной функции успешно удалена", id);
        return ResponseEntity.ok().build();
    }
}