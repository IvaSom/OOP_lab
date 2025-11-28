package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.AnalFunDTO;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.service.AnalFunService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/anal-fun")
public class AnalFunController {

    private static final Logger logger = LoggerFactory.getLogger(AnalFunController.class);

    @Autowired
    private AnalFunService service;
    @GetMapping
    public List<AnalFunDTO> getAllFunctions() {
        logger.info("GET /api/anal-fun - Получение всех аналитических функций");
        List<AnalFunDTO> functions = service.findAllFunctions().stream()
                .map(AnalFunDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/anal-fun - Найдено {} функций", functions.size());
        return functions;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalFunDTO> getFunctionById(@PathVariable Long id) {
        logger.info("GET /api/anal-fun/{} - Получение функции по ID", id);
        Optional<analFun> function = service.findSingleFunctionById(id);
        if (function.isPresent()) {
            logger.info("GET /api/anal-fun/{} - Функция найдена", id);
            return ResponseEntity.ok(new AnalFunDTO(function.get()));
        } else {
            logger.warn("GET /api/anal-fun/{} - Функция не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public AnalFunDTO createFunction(@RequestBody analFun function) {
        logger.info("POST /api/anal-fun - Создание новой функции");
        analFun saved = service.save(function);
        logger.info("POST /api/anal-fun - Функция создана с ID: {}", saved.getId());
        return new AnalFunDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalFunDTO> updateFunction(@PathVariable Long id, @RequestBody analFun function) {
        logger.info("PUT /api/anal-fun/{} - Обновление функции", id);
        function.setId(id);
        analFun updated = service.save(function);
        logger.info("PUT /api/anal-fun/{} - Функция успешно обновлена", id);
        return ResponseEntity.ok(new AnalFunDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        logger.info("DELETE /api/anal-fun/{} - Удаление функции", id);
        service.deleteById(id);
        logger.info("DELETE /api/anal-fun/{} - Функция успешно удалена", id);
        return ResponseEntity.ok().build();
    }
}