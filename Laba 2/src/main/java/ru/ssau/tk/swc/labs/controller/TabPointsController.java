package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.TabPointsDTO;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.service.TabPointsService;

import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tab-points")
public class TabPointsController {

    private static final Logger logger = LoggerFactory.getLogger(TabPointsController.class);

    @Autowired
    private TabPointsService service;

    @Autowired
    private TabFunRepository tabFunRepository;

    @GetMapping
    public List<TabPointsDTO> getAllPoints() {
        logger.info("GET /api/tab-points - Получение всех точек табулированных функций");
        List<TabPointsDTO> points = service.findAll().stream()
                .map(TabPointsDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/tab-points - Найдено {} точек табулированных функций", points.size());
        return points;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabPointsDTO> getPointById(@PathVariable Long id) {
        logger.info("GET /api/tab-points/{} - Получение точки табулированной функции по ID", id);
        Optional<tab_points> point = service.findById(id);
        if (point.isPresent()) {
            logger.info("GET /api/tab-points/{} - Точка табулированной функции найдена", id);
            return ResponseEntity.ok(new TabPointsDTO(point.get()));
        } else {
            logger.warn("GET /api/tab-points/{} - Точка табулированной функции не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/function/{funId}")
    public List<TabPointsDTO> getPointsByFunction(@PathVariable Long funId) {
        logger.info("GET /api/tab-points/function/{} - Получение точек по табулированной функции", funId);
        List<TabPointsDTO> points = service.findByFunctionId(funId).stream()
                .map(TabPointsDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/tab-points/function/{} - Найдено {} точек для табулированной функции", funId, points.size());
        return points;
    }

    @GetMapping("/point")
    public ResponseEntity<TabPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {
        logger.info("GET /api/tab-points/point - Поиск точки по x={} и functionId={}", x, functionId);
        Optional<tab_points> point = service.findSinglePoint(x, functionId);
        if (point.isPresent()) {
            logger.info("GET /api/tab-points/point - Точка найдена для x={} и functionId={}", x, functionId);
            return ResponseEntity.ok(new TabPointsDTO(point.get()));
        } else {
            logger.warn("GET /api/tab-points/point - Точка не найдена для x={} и functionId={}", x, functionId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public TabPointsDTO createPoint(@RequestBody TabPointsDTO pointDTO) {
        logger.info("POST /api/tab-points - Создание новой точки табулированной функции");

        tabFun function = tabFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> {
                    logger.error("POST /api/tab-points - Табулированная функция не найдена с ID: {}", pointDTO.getFunctionId());
                    return new RuntimeException("Tab function not found with id: " + pointDTO.getFunctionId());
                });

        tab_points point = new tab_points();
        point.setX(pointDTO.getX());
        point.setY(pointDTO.getY());
        point.setDerive(pointDTO.getDerive());
        point.setFunction(function);

        tab_points saved = service.save(point);
        logger.info("POST /api/tab-points - Точка табулированной функции создана с ID: {}", saved.getId());
        return new TabPointsDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabPointsDTO> updatePoint(@PathVariable Long id, @RequestBody TabPointsDTO pointDTO) {
        logger.info("PUT /api/tab-points/{} - Обновление точки табулированной функции", id);

        tab_points existingPoint = service.findById(id)
                .orElseThrow(() -> {
                    logger.error("PUT /api/tab-points/{} - Точка табулированной функции не найдена", id);
                    return new RuntimeException("Tab point not found with id: " + id);
                });

        tabFun function = tabFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> {
                    logger.error("PUT /api/tab-points/{} - Табулированная функция не найдена с ID: {}", id, pointDTO.getFunctionId());
                    return new RuntimeException("Tab function not found with id: " + pointDTO.getFunctionId());
                });

        existingPoint.setX(pointDTO.getX());
        existingPoint.setY(pointDTO.getY());
        existingPoint.setDerive(pointDTO.getDerive());
        existingPoint.setFunction(function);

        tab_points updated = service.save(existingPoint);
        logger.info("PUT /api/tab-points/{} - Точка табулированной функции успешно обновлена", id);
        return ResponseEntity.ok(new TabPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        logger.info("DELETE /api/tab-points/{} - Удаление точки табулированной функции", id);
        service.deleteById(id);
        logger.info("DELETE /api/tab-points/{} - Точка табулированной функции успешно удалена", id);
        return ResponseEntity.ok().build();
    }
}