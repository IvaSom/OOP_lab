package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.service.TabFunService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tab-fun")
public class TabFunController {

    @Autowired
    private TabFunService service;

    @GetMapping
    public List<tabFun> getAllFunctions() {
        return service.findAllFunctions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<tabFun> getFunctionById(@PathVariable Long id) {
        Optional<tabFun> function = service.findSingleFunctionById(id);
        return function.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //http://localhost:8080/api/tab-functions/search?name=имя
    @GetMapping("/search")
    public ResponseEntity<tabFun> getFunctionByName(@RequestParam String name) {
        Optional<tabFun> function = service.findSingleFunction(name);
        return function.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //http://localhost:8080/api/tab-functions/filter?types=1,2,3&sortBy=id&direction=asc
    @GetMapping("/filter")
    public List<tabFun> getFunctionsWithFilter(
            @RequestParam(required = false) Integer[] types,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(types, sortBy, direction);
    }

    @PostMapping
    public tabFun createFunction(@RequestBody tabFun function) {
        return service.save(function);
    }

    @PutMapping("/{id}")
    public ResponseEntity<tabFun> updateFunction(@PathVariable Long id, @RequestBody tabFun function) {
        function.setId(id);
        tabFun updated = service.save(function);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}