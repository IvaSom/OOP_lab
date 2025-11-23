package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.service.AnalFunService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/analytic-functions")
public class AnalFunController {

    @Autowired
    private AnalFunService service;

    @GetMapping
    public List<analFun> getAllFunctions() {
        return service.findAllFunctions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<analFun> getFunctionById(@PathVariable Long id) {
        Optional<analFun> function = service.findSingleFunctionById(id);
        return function.map(ResponseEntity::ok) //если нашел, то возвращает
                .orElse(ResponseEntity.notFound().build()); //иначе на 404
    }

    @PostMapping
    public analFun createFunction(@RequestBody analFun function) {
        return service.save(function);
    }

    @PutMapping("/{id}")
    public ResponseEntity<analFun> updateFunction(@PathVariable Long id, @RequestBody analFun function) {
        function.setId(id);
        // Нужно добавить метод save в сервис
        analFun updated = service.save(function);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}