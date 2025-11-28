package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.CompPointsDTO;
import ru.ssau.tk.swc.labs.entity.comp_points;
import ru.ssau.tk.swc.labs.service.CompPointsService;

import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comp-points")
public class CompPointsController {

    @Autowired
    private CompPointsService service;
    @Autowired
    private CompFunRepository compFunRepository;

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


    @GetMapping("/point")
    public ResponseEntity<CompPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<comp_points> point = service.findSinglePoint(x, functionId);
        return point.map(p -> ResponseEntity.ok(new CompPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public CompPointsDTO createPoint(@RequestBody CompPointsDTO pointDTO) {
        compFun function = compFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> new RuntimeException("Composite function not found with id: " + pointDTO.getFunctionId()));

        comp_points point = new comp_points();
        point.setX(pointDTO.getX());
        point.setY(pointDTO.getY());
        point.setFunction(function);

        comp_points saved = service.save(point);
        return new CompPointsDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompPointsDTO> updatePoint(@PathVariable Long id, @RequestBody CompPointsDTO pointDTO) {

        comp_points existingPoint = service.findById(id)
                .orElseThrow(() -> new RuntimeException("Composite point not found with id: " + id));

        compFun function = compFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> new RuntimeException("Composite function not found with id: " + pointDTO.getFunctionId()));

        existingPoint.setX(pointDTO.getX());
        existingPoint.setY(pointDTO.getY());
        existingPoint.setFunction(function);

        comp_points updated = service.save(existingPoint);
        return ResponseEntity.ok(new CompPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}