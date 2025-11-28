package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.service.AnalPointsService;
import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anal-points")
public class AnalPointsController {

    @Autowired
    private AnalPointsService service;

    @Autowired
    private AnalFunRepository analFunRepository;

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

    @GetMapping("/point")
    public ResponseEntity<AnalPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<anal_points> point = service.findSinglePoint(x, functionId);
        return point.map(p -> ResponseEntity.ok(new AnalPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AnalPointsDTO createPoint(@RequestBody AnalPointsDTO pointDTO) {
        analFun function = analFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> new RuntimeException("Function not found"));

        anal_points point = new anal_points();
        point.setX(pointDTO.getX());
        point.setY(pointDTO.getY());
        point.setFunction(function);

        anal_points saved = service.save(point);
        return new AnalPointsDTO(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<AnalPointsDTO> updatePoint(@PathVariable Long id, @RequestBody AnalPointsDTO pointDTO) {

        anal_points existingPoint = service.findById(id)
                .orElseThrow(() -> new RuntimeException("Point not found with id: " + id));

        analFun function = analFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> new RuntimeException("Function not found with id: " + pointDTO.getFunctionId()));

        existingPoint.setX(pointDTO.getX());
        existingPoint.setY(pointDTO.getY());
        existingPoint.setFunction(function);

        anal_points updated = service.save(existingPoint);
        return ResponseEntity.ok(new AnalPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}