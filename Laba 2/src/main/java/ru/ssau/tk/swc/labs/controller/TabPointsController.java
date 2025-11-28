package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.TabPointsDTO;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.service.TabPointsService;

import ru.ssau.tk.swc.labs.dto.*;
import ru.ssau.tk.swc.labs.entity.*;
import ru.ssau.tk.swc.labs.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tab-points")
public class TabPointsController {

    @Autowired
    private TabPointsService service;

    @Autowired
    private TabFunRepository tabFunRepository;

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


    @GetMapping("/point")
    public ResponseEntity<TabPointsDTO> getPointByXAndFunction(
            @RequestParam Double x,
            @RequestParam Long functionId) {

        Optional<tab_points> point = service.findSinglePoint(x, functionId);
        return point.map(p -> ResponseEntity.ok(new TabPointsDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public TabPointsDTO createPoint(@RequestBody TabPointsDTO pointDTO) {
        tabFun function = tabFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> new RuntimeException("Tab function not found with id: " + pointDTO.getFunctionId()));

        tab_points point = new tab_points();
        point.setX(pointDTO.getX());
        point.setY(pointDTO.getY());
        point.setDerive(pointDTO.getDerive());
        point.setFunction(function);

        tab_points saved = service.save(point);
        return new TabPointsDTO(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<TabPointsDTO> updatePoint(@PathVariable Long id, @RequestBody TabPointsDTO pointDTO) {
        tab_points existingPoint = service.findById(id)
                .orElseThrow(() -> new RuntimeException("Tab point not found with id: " + id));

        tabFun function = tabFunRepository.findById(pointDTO.getFunctionId())
                .orElseThrow(() -> new RuntimeException("Tab function not found with id: " + pointDTO.getFunctionId()));

        existingPoint.setX(pointDTO.getX());
        existingPoint.setY(pointDTO.getY());
        existingPoint.setDerive(pointDTO.getDerive());
        existingPoint.setFunction(function);

        tab_points updated = service.save(existingPoint);
        return ResponseEntity.ok(new TabPointsDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}