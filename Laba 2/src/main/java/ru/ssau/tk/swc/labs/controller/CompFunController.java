package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.CompFunDTO;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.service.CompFunService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comp-fun")
public class CompFunController {

    private static final Logger logger = LoggerFactory.getLogger(CompFunController.class);

    @Autowired
    private CompFunService service;

    @GetMapping
    public List<CompFunDTO> getAllFunctions() {
        logger.info("GET /api/comp-fun - Получение всех композитных функций");
        List<CompFunDTO> functions = service.findAllFunctions().stream()
                .map(CompFunDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/comp-fun - Найдено {} композитных функций", functions.size());
        return functions;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompFunDTO> getFunctionById(@PathVariable Long id) {
        logger.info("GET /api/comp-fun/{} - Получение композитной функции по ID", id);
        Optional<compFun> function = service.findSingleFunctionById(id);
        if (function.isPresent()) {
            logger.info("GET /api/comp-fun/{} - Композитная функция найдена", id);
            return ResponseEntity.ok(new CompFunDTO(function.get()));
        } else {
            logger.warn("GET /api/comp-fun/{} - Композитная функция не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<CompFunDTO> getFunctionByName(@RequestParam String name) {
        logger.info("GET /api/comp-fun/search - Поиск композитной функции по имени: {}", name);
        Optional<compFun> function = service.findSingleFunction(name);
        if (function.isPresent()) {
            logger.info("GET /api/comp-fun/search - Композитная функция '{}' найдена", name);
            return ResponseEntity.ok(new CompFunDTO(function.get()));
        } else {
            logger.warn("GET /api/comp-fun/search - Композитная функция '{}' не найдена", name);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public CompFunDTO createFunction(@RequestBody compFun function) {
        logger.info("POST /api/comp-fun - Создание новой композитной функции");
        compFun saved = service.save(function);
        logger.info("POST /api/comp-fun - Композитная функция создана с ID: {}", saved.getId());
        return new CompFunDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompFunDTO> updateFunction(@PathVariable Long id, @RequestBody compFun function) {
        logger.info("PUT /api/comp-fun/{} - Обновление композитной функции", id);
        function.setId(id);
        compFun updated = service.save(function);
        logger.info("PUT /api/comp-fun/{} - Композитная функция успешно обновлена", id);
        return ResponseEntity.ok(new CompFunDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        logger.info("DELETE /api/comp-fun/{} - Удаление композитной функции", id);
        service.deleteById(id);
        logger.info("DELETE /api/comp-fun/{} - Композитная функция успешно удалена", id);
        return ResponseEntity.ok().build();
    }
}