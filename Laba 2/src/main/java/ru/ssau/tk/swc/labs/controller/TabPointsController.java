package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.TabPointsDTO;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.service.TabPointsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tab-points")
public class TabPointsController {

    @Autowired
    private TabPointsService service;

    @GetMapping
    public List<TabPointsDTO> getAllPoints() {
        return service.findAll().stream()
                .map(TabPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabPointsDTO> getPointById(@PathVariable Long id) {
        Optional<tab_points> point = service.findById(id);
        return point.map(p -> ResponseEntity.ok(new TabPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/function/{funId}")
    public List<TabPointsDTO> getPointsByFunction(@PathVariable Long funId) {
        return service.findByFunctionId(funId).stream()
                .map(TabPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<TabPointsDTO> searchPoints(
            @RequestParam Double minX,
            @RequestParam Double maxX,
            @RequestParam Double minY,
            @RequestParam Double maxY,
            @RequestParam Long functionId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(minX, maxX, minY, maxY, sortBy, direction, functionId).stream()
                .map(TabPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/bfs")
    public List<TabPointsDTO> breadthFirstSearch(
            @RequestParam Double startX,
            @RequestParam Double radius,
            @RequestParam Long functionId) {

        return service.breadthFirstSearch(startX, radius, functionId).stream()
                .map(TabPointsDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/point")
    public ResponseEntity<TabPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<tab_points> point = service.findSinglePoint(x, functionId);
        return point.map(p -> ResponseEntity.ok(new TabPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TabPointsDTO createPoint(@RequestBody tab_points point) {
        tab_points saved = service.save(point);
        return new TabPointsDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabPointsDTO> updatePoint(@PathVariable Long id, @RequestBody tab_points point) {
        point.setId(id);
        tab_points updated = service.save(point);
        return ResponseEntity.ok(new TabPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}