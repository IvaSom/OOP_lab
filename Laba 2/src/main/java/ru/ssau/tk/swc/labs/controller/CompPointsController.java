package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.CompPointsDTO;
import ru.ssau.tk.swc.labs.entity.comp_points;
import ru.ssau.tk.swc.labs.service.CompPointsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comp-points")
public class CompPointsController {

    @Autowired
    private CompPointsService service;

    @GetMapping
    public List<CompPointsDTO> getAllPoints() {
        return service.findAll().stream()
                .map(CompPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompPointsDTO> getPointById(@PathVariable Long id) {
        Optional<comp_points> point = service.findById(id);
        return point.map(p -> ResponseEntity.ok(new CompPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/function/{funId}")
    public List<CompPointsDTO> getPointsByFunction(@PathVariable Long funId) {
        return service.findByFunctionId(funId).stream()
                .map(CompPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<CompPointsDTO> searchPoints(
            @RequestParam Double minX,
            @RequestParam Double maxX,
            @RequestParam Double minY,
            @RequestParam Double maxY,
            @RequestParam Long functionId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(minX, maxX, minY, maxY, sortBy, direction, functionId).stream()
                .map(CompPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/bfs")
    public List<CompPointsDTO> breadthFirstSearch(
            @RequestParam Double startX,
            @RequestParam Double radius,
            @RequestParam Long functionId) {

        return service.breadthFirstSearch(startX, radius, functionId).stream()
                .map(CompPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/point")
    public ResponseEntity<CompPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<comp_points> point = service.findSinglePoint(x, functionId);
        return point.map(p -> ResponseEntity.ok(new CompPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CompPointsDTO createPoint(@RequestBody comp_points point) {
        comp_points saved = service.save(point);
        return new CompPointsDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompPointsDTO> updatePoint(@PathVariable Long id, @RequestBody comp_points point) {
        point.setId(id);
        comp_points updated = service.save(point);
        return ResponseEntity.ok(new CompPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}