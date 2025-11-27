package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.AnalFunDTO;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.service.AnalFunService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anal-fun")
public class AnalFunController {

    @Autowired
    private AnalFunService service;

    @GetMapping
    public List<AnalFunDTO> getAllFunctions() {
        return service.findAllFunctions().stream()
                .map(AnalFunDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalFunDTO> getFunctionById(@PathVariable Long id) {
        Optional<analFun> function = service.findSingleFunctionById(id);
        return function.map(f -> ResponseEntity.ok(new AnalFunDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AnalFunDTO createFunction(@RequestBody analFun function) {
        analFun saved = service.save(function);
        return new AnalFunDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalFunDTO> updateFunction(@PathVariable Long id, @RequestBody analFun function) {
        function.setId(id);
        analFun updated = service.save(function);
        return ResponseEntity.ok(new AnalFunDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}