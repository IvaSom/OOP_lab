package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.TabFunDTO;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.service.TabFunService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tab-fun")
public class TabFunController {

    private static final Logger logger = LoggerFactory.getLogger(TabFunController.class);

    @Autowired
    private TabFunService service;

    @GetMapping
    public List<TabFunDTO> getAllFunctions() {
        logger.info("GET /api/tab-fun - Получение всех табулированных функций");
        List<TabFunDTO> functions = service.findAllFunctions().stream()
                .map(TabFunDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/tab-fun - Найдено {} табулированных функций", functions.size());
        return functions;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabFunDTO> getFunctionById(@PathVariable Long id) {
        logger.info("GET /api/tab-fun/{} - Получение табулированной функции по ID", id);
        Optional<tabFun> function = service.findSingleFunctionById(id);
        if (function.isPresent()) {
            logger.info("GET /api/tab-fun/{} - Табулированная функция найдена", id);
            return ResponseEntity.ok(new TabFunDTO(function.get()));
        } else {
            logger.warn("GET /api/tab-fun/{} - Табулированная функция не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<TabFunDTO> getFunctionByName(@RequestParam String name) {
        logger.info("GET /api/tab-fun/search - Поиск табулированной функции по имени: {}", name);
        Optional<tabFun> function = service.findSingleFunction(name);
        if (function.isPresent()) {
            logger.info("GET /api/tab-fun/search - Табулированная функция '{}' найдена", name);
            return ResponseEntity.ok(new TabFunDTO(function.get()));
        } else {
            logger.warn("GET /api/tab-fun/search - Табулированная функция '{}' не найдена", name);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public TabFunDTO createFunction(@RequestBody tabFun function) {
        logger.info("POST /api/tab-fun - Создание новой табулированной функции");
        tabFun saved = service.save(function);
        logger.info("POST /api/tab-fun - Табулированная функция создана с ID: {}", saved.getId());
        return new TabFunDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabFunDTO> updateFunction(@PathVariable Long id, @RequestBody tabFun function) {
        logger.info("PUT /api/tab-fun/{} - Обновление табулированной функции", id);
        function.setId(id);
        tabFun updated = service.save(function);
        logger.info("PUT /api/tab-fun/{} - Табулированная функция успешно обновлена", id);
        return ResponseEntity.ok(new TabFunDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        logger.info("DELETE /api/tab-fun/{} - Удаление табулированной функции", id);
        service.deleteById(id);
        logger.info("DELETE /api/tab-fun/{} - Табулированная функция успешно удалена", id);
        return ResponseEntity.ok().build();
    }
}