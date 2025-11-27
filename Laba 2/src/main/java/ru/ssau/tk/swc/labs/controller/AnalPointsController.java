package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.AnalPointsDTO;
import ru.ssau.tk.swc.labs.entity.anal_points;
import ru.ssau.tk.swc.labs.service.AnalPointsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anal-points")
public class AnalPointsController {

    @Autowired
    private AnalPointsService service;

    @GetMapping
    public List<AnalPointsDTO> getAllPoints() {
        return service.findAll().stream()
                .map(AnalPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalPointsDTO> getPointById(@PathVariable Long id) {
        Optional<anal_points> point = service.findById(id);
        return point.map(p -> ResponseEntity.ok(new AnalPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/function/{funId}")
    public List<AnalPointsDTO> getPointsByFunction(@PathVariable Long funId) {
        return service.findByFunctionId(funId).stream()
                .map(AnalPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<AnalPointsDTO> searchPoints(
            @RequestParam Double minX,
            @RequestParam Double maxX,
            @RequestParam Double minY,
            @RequestParam Double maxY,
            @RequestParam Long funId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(minX, maxX, minY, maxY, sortBy, direction, funId).stream()
                .map(AnalPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/bfs")
    public List<AnalPointsDTO> breadthFirstSearch(
            @RequestParam Double startX,
            @RequestParam Double radius,
            @RequestParam Long functionId) {

        return service.breadthFirstSearch(startX, radius, functionId).stream()
                .map(AnalPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/point")
    public ResponseEntity<AnalPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<anal_points> point = service.findSinglePoint(x, functionId);
        return point.map(p -> ResponseEntity.ok(new AnalPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AnalPointsDTO createPoint(@RequestBody anal_points point) {
        anal_points saved = service.save(point);
        return new AnalPointsDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalPointsDTO> updatePoint(@PathVariable Long id, @RequestBody anal_points point) {
        point.setId(id);
        anal_points updated = service.save(point);
        return ResponseEntity.ok(new AnalPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}