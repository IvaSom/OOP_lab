package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.service.CompFunService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comp-fun")
public class CompFunController {

    @Autowired
    private CompFunService service;

    @GetMapping
    public List<compFun> getAllFunctions() {
        return service.findAllFunctions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<compFun> getFunctionById(@PathVariable Long id) {
        Optional<compFun> function = service.findSingleFunctionById(id);
        return function.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<compFun> getFunctionByName(@RequestParam String name) {
        Optional<compFun> function = service.findSingleFunction(name);
        return function.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public List<compFun> getFunctionsWithFilter(
            @RequestParam(required = false) List<String> names,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        String[] namesArray = names != null ? names.toArray(new String[0]) : null;
        return service.findMultipleWithSorting(namesArray, sortBy, direction);
    }

    @PostMapping
    public compFun createFunction(@RequestBody compFun function) {
        return service.save(function);
    }

    @PutMapping("/{id}")
    public ResponseEntity<compFun> updateFunction(@PathVariable Long id, @RequestBody compFun function) {
        function.setId(id);
        compFun updated = service.save(function);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}