package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.entity.anal_points;
import ru.ssau.tk.swc.labs.service.AnalPointsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/anal-points")
public class AnalPointsController {

    @Autowired
    private AnalPointsService service;

    @GetMapping
    public List<anal_points> getAllPoints() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<anal_points> getPointById(@PathVariable Long id) {
        Optional<anal_points> point = service.findById(id);
        return point.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/function/{funId}")
    public List<anal_points> getPointsByFunction(@PathVariable Long funId) {
        return service.findByFunctionId(funId);
    }

    @GetMapping("/search")
    public List<anal_points> searchPoints(
            @RequestParam Double minX,
            @RequestParam Double maxX,
            @RequestParam Double minY,
            @RequestParam Double maxY,
            @RequestParam Long funId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(minX, maxX, minY, maxY, sortBy, direction, funId);
    }

    @GetMapping("/bfs")
    public List<anal_points> breadthFirstSearch(
            @RequestParam Double startX,
            @RequestParam Double radius,
            @RequestParam Long functionId) {

        return service.breadthFirstSearch(startX, radius, functionId);
    }

    @GetMapping("/point")
    public ResponseEntity<anal_points> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<anal_points> point = service.findSinglePoint(x, functionId);
        return point.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public anal_points createPoint(@RequestBody anal_points point) {
        return service.save(point);
    }

    @PutMapping("/{id}")
    public ResponseEntity<anal_points> updatePoint(@PathVariable Long id, @RequestBody anal_points point) {
        point.setId(id);
        anal_points updated = service.save(point);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}