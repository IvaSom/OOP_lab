package ru.ssau.tk.swc.labs.controller;

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

    @Autowired
    private CompStructureService service;

    @Autowired
    private AnalFunRepository analFunRepository;
    @Autowired
    private CompFunRepository compFunRepository;

    @GetMapping
    public List<CompositeStructureDTO> getAllStructures() {
        return service.findAllStructures().stream()
                .map(CompositeStructureDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompositeStructureDTO> getStructureById(@PathVariable Long id) {
        Optional<composite_structure> structure = service.findSingleStructure(id);
        return structure.map(s -> ResponseEntity.ok(new CompositeStructureDTO(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comp-fun/{compFunId}")
    public List<CompositeStructureDTO> getStructuresByCompositeFunction(@PathVariable Long compFunId) {
        return service.findByCompositeFunctionId(compFunId).stream()
                .map(CompositeStructureDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/anal-fun/{analFunId}")
    public List<CompositeStructureDTO> getStructuresByAnalyticFunction(@PathVariable Long analFunId) {
        return service.findByAnalyticFunctionId(analFunId).stream()
                .map(CompositeStructureDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public CompositeStructureDTO createStructure(@RequestBody CompositeStructureDTO structureDTO) {
        compFun compositeFunction = compFunRepository.findById(structureDTO.getCompositeFunctionId())
                .orElseThrow(() -> new RuntimeException("Composite function not found with id: " + structureDTO.getCompositeFunctionId()));

        analFun analyticFunction = analFunRepository.findById(structureDTO.getAnalyticFunctionId())
                .orElseThrow(() -> new RuntimeException("Analytic function not found with id: " + structureDTO.getAnalyticFunctionId()));

        composite_structure structure = new composite_structure();
        structure.setCompFun(compositeFunction);
        structure.setAnalFun(analyticFunction);
        structure.setExecutionOrder(structureDTO.getExecutionOrder());

        composite_structure saved = service.save(structure);
        return new CompositeStructureDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositeStructureDTO> updateStructure(@PathVariable Long id, @RequestBody CompositeStructureDTO structureDTO) {

        composite_structure existingStructure = service.findSingleStructure(id)
                .orElseThrow(() -> new RuntimeException("Composite structure not found with id: " + id));


        compFun compositeFunction = compFunRepository.findById(structureDTO.getCompositeFunctionId())
                .orElseThrow(() -> new RuntimeException("Composite function not found with id: " + structureDTO.getCompositeFunctionId()));

        analFun analyticFunction = analFunRepository.findById(structureDTO.getAnalyticFunctionId())
                .orElseThrow(() -> new RuntimeException("Analytic function not found with id: " + structureDTO.getAnalyticFunctionId()));

        existingStructure.setCompFun(compositeFunction);
        existingStructure.setAnalFun(analyticFunction);
        existingStructure.setExecutionOrder(structureDTO.getExecutionOrder());

        composite_structure updated = service.save(existingStructure);
        return ResponseEntity.ok(new CompositeStructureDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStructure(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}