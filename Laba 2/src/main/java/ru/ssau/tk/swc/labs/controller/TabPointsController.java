package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.service.TabPointsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tab-points")
public class TabPointsController {

    @Autowired
    private TabPointsService service;

    @GetMapping
    public List<tab_points> getAllPoints() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<tab_points> getPointById(@PathVariable Long id) {
        Optional<tab_points> point = service.findById(id);
        return point.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/function/{funId}")
    public List<tab_points> getPointsByFunction(@PathVariable Long funId) {
        return service.findByFunctionId(funId);
    }

    @GetMapping("/search")
    public List<tab_points> searchPoints(
            @RequestParam Double minX,
            @RequestParam Double maxX,
            @RequestParam Double minY,
            @RequestParam Double maxY,
            @RequestParam Long functionId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(minX, maxX, minY, maxY, sortBy, direction, functionId);
    }

    @GetMapping("/bfs")
    public List<tab_points> breadthFirstSearch(
            @RequestParam Double startX,
            @RequestParam Double radius,
            @RequestParam Long functionId) {

        return service.breadthFirstSearch(startX, radius, functionId);
    }

    @GetMapping("/point")
    public ResponseEntity<tab_points> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<tab_points> point = service.findSinglePoint(x, functionId);
        return point.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public tab_points createPoint(@RequestBody tab_points point) {
        return service.save(point);
    }

    @PutMapping("/{id}")
    public ResponseEntity<tab_points> updatePoint(@PathVariable Long id, @RequestBody tab_points point) {
        point.setId(id);
        tab_points updated = service.save(point);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}