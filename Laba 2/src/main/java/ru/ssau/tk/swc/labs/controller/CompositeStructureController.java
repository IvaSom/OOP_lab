package ru.ssau.tk.swc.labs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.CompositeStructureDTO;
import ru.ssau.tk.swc.labs.entity.composite_structure;
import ru.ssau.tk.swc.labs.service.CompStructureService;

import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.repository.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comp-structures")
public class CompositeStructureController {

    private static final Logger logger = LoggerFactory.getLogger(CompositeStructureController.class);

    @Autowired
    private CompStructureService service;

    @Autowired
    private AnalFunRepository analFunRepository;
    @Autowired
    private CompFunRepository compFunRepository;

    @GetMapping
    public List<CompositeStructureDTO> getAllStructures() {
        logger.info("GET /api/comp-structures - Получение всех композитных структур");
        List<CompositeStructureDTO> structures = service.findAllStructures().stream()
                .map(CompositeStructureDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/comp-structures - Найдено {} композитных структур", structures.size());
        return structures;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompositeStructureDTO> getStructureById(@PathVariable Long id) {
        logger.info("GET /api/comp-structures/{} - Получение композитной структуры по ID", id);
        Optional<composite_structure> structure = service.findSingleStructure(id);
        if (structure.isPresent()) {
            logger.info("GET /api/comp-structures/{} - Композитная структура найдена", id);
            return ResponseEntity.ok(new CompositeStructureDTO(structure.get()));
        } else {
            logger.warn("GET /api/comp-structures/{} - Композитная структура не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/comp-fun/{compFunId}")
    public List<CompositeStructureDTO> getStructuresByCompositeFunction(@PathVariable Long compFunId) {
        logger.info("GET /api/comp-structures/comp-fun/{} - Получение структур по композитной функции", compFunId);
        List<CompositeStructureDTO> structures = service.findByCompositeFunctionId(compFunId).stream()
                .map(CompositeStructureDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/comp-structures/comp-fun/{} - Найдено {} структур для композитной функции", compFunId, structures.size());
        return structures;
    }

    @GetMapping("/anal-fun/{analFunId}")
    public List<CompositeStructureDTO> getStructuresByAnalyticFunction(@PathVariable Long analFunId) {
        logger.info("GET /api/comp-structures/anal-fun/{} - Получение структур по аналитической функции", analFunId);
        List<CompositeStructureDTO> structures = service.findByAnalyticFunctionId(analFunId).stream()
                .map(CompositeStructureDTO::new)
                .collect(Collectors.toList());
        logger.info("GET /api/comp-structures/anal-fun/{} - Найдено {} структур для аналитической функции", analFunId, structures.size());
        return structures;
    }

    @PostMapping
    public CompositeStructureDTO createStructure(@RequestBody CompositeStructureDTO structureDTO) {
        logger.info("POST /api/comp-structures - Создание новой композитной структуры");

        compFun compositeFunction = compFunRepository.findById(structureDTO.getCompositeFunctionId())
                .orElseThrow(() -> {
                    logger.error("POST /api/comp-structures - Композитная функция не найдена с ID: {}", structureDTO.getCompositeFunctionId());
                    return new RuntimeException("Composite function not found with id: " + structureDTO.getCompositeFunctionId());
                });

        analFun analyticFunction = analFunRepository.findById(structureDTO.getAnalyticFunctionId())
                .orElseThrow(() -> {
                    logger.error("POST /api/comp-structures - Аналитическая функция не найдена с ID: {}", structureDTO.getAnalyticFunctionId());
                    return new RuntimeException("Analytic function not found with id: " + structureDTO.getAnalyticFunctionId());
                });

        composite_structure structure = new composite_structure();
        structure.setCompFun(compositeFunction);
        structure.setAnalFun(analyticFunction);
        structure.setExecutionOrder(structureDTO.getExecutionOrder());

        composite_structure saved = service.save(structure);
        logger.info("POST /api/comp-structures - Композитная структура создана с ID: {}", saved.getId());
        return new CompositeStructureDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositeStructureDTO> updateStructure(@PathVariable Long id, @RequestBody CompositeStructureDTO structureDTO) {
        logger.info("PUT /api/comp-structures/{} - Обновление композитной структуры", id);

        composite_structure existingStructure = service.findSingleStructure(id)
                .orElseThrow(() -> {
                    logger.error("PUT /api/comp-structures/{} - Композитная структура не найдена", id);
                    return new RuntimeException("Composite structure not found with id: " + id);
                });

        compFun compositeFunction = compFunRepository.findById(structureDTO.getCompositeFunctionId())
                .orElseThrow(() -> {
                    logger.error("PUT /api/comp-structures/{} - Композитная функция не найдена с ID: {}", id, structureDTO.getCompositeFunctionId());
                    return new RuntimeException("Composite function not found with id: " + structureDTO.getCompositeFunctionId());
                });

        analFun analyticFunction = analFunRepository.findById(structureDTO.getAnalyticFunctionId())
                .orElseThrow(() -> {
                    logger.error("PUT /api/comp-structures/{} - Аналитическая функция не найдена с ID: {}", id, structureDTO.getAnalyticFunctionId());
                    return new RuntimeException("Analytic function not found with id: " + structureDTO.getAnalyticFunctionId());
                });

        existingStructure.setCompFun(compositeFunction);
        existingStructure.setAnalFun(analyticFunction);
        existingStructure.setExecutionOrder(structureDTO.getExecutionOrder());

        composite_structure updated = service.save(existingStructure);
        logger.info("PUT /api/comp-structures/{} - Композитная структура успешно обновлена", id);
        return ResponseEntity.ok(new CompositeStructureDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStructure(@PathVariable Long id) {
        logger.info("DELETE /api/comp-structures/{} - Удаление композитной структуры", id);
        service.deleteById(id);
        logger.info("DELETE /api/comp-structures/{} - Композитная структура успешно удалена", id);
        return ResponseEntity.ok().build();
    }
}