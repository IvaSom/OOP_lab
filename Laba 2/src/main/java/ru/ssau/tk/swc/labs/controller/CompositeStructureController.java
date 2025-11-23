package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.entity.composite_structure;
import ru.ssau.tk.swc.labs.service.CompStructureService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comp-structures")
public class CompositeStructureController {

    @Autowired
    private CompStructureService service;

    @GetMapping
    public List<composite_structure> getAllStructures() {
        return service.findAllStructures();
    }

    @GetMapping("/{id}")
    public ResponseEntity<composite_structure> getStructureById(@PathVariable Long id) {
        Optional<composite_structure> structure = service.findSingleStructure(id);
        return structure.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comp-fun/{compFunId}")
    public List<composite_structure> getStructuresByCompositeFunction(@PathVariable Long compFunId) {
        return service.findByCompositeFunctionId(compFunId);
    }

    @GetMapping("/anal-fun/{analFunId}")
    public List<composite_structure> getStructuresByAnalyticFunction(@PathVariable Long analFunId) {
        return service.findByAnalyticFunctionId(analFunId);
    }

    @GetMapping("/filter")
    public List<composite_structure> getStructuresWithFilter(
            @RequestParam(required = false) Long compositeFunctionId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(compositeFunctionId, sortBy, direction);
    }

    @GetMapping("/hierarchy/{compFunId}")
    public List<Map<String, Object>> getHierarchy(@PathVariable Long compFunId) {
        return service.getFlattenedHierarchy(compFunId);
    }

    @PostMapping
    public composite_structure createStructure(@RequestBody composite_structure structure) {
        return service.save(structure);
    }

    @PutMapping("/{id}")
    public ResponseEntity<composite_structure> updateStructure(@PathVariable Long id, @RequestBody composite_structure structure) {
        structure.setId(id);
        composite_structure updated = service.save(structure);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStructure(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}