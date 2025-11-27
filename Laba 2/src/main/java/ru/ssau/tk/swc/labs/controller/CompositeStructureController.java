package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.CompositeStructureDTO;
import ru.ssau.tk.swc.labs.entity.composite_structure;
import ru.ssau.tk.swc.labs.service.CompStructureService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comp-structures")
public class CompositeStructureController {

    @Autowired
    private CompStructureService service;

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

    @GetMapping("/filter")
    public List<CompositeStructureDTO> getStructuresWithFilter(
            @RequestParam(required = false) Long compositeFunctionId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(compositeFunctionId, sortBy, direction).stream()
                .map(CompositeStructureDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/hierarchy/{compFunId}")
    public List<Map<String, Object>> getHierarchy(@PathVariable Long compFunId) {
        return service.getFlattenedHierarchy(compFunId);
    }

    @PostMapping
    public CompositeStructureDTO createStructure(@RequestBody composite_structure structure) {
        composite_structure saved = service.save(structure);
        return new CompositeStructureDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositeStructureDTO> updateStructure(@PathVariable Long id, @RequestBody composite_structure structure) {
        structure.setId(id);
        composite_structure updated = service.save(structure);
        return ResponseEntity.ok(new CompositeStructureDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStructure(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}