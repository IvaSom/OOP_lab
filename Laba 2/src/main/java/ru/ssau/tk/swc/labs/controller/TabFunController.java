package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.TabFunDTO;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.service.TabFunService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tab-fun")
public class TabFunController {

    @Autowired
    private TabFunService service;

    @GetMapping
    public List<TabFunDTO> getAllFunctions() {
        return service.findAllFunctions().stream()
                .map(TabFunDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabFunDTO> getFunctionById(@PathVariable Long id) {
        Optional<tabFun> function = service.findSingleFunctionById(id);
        return function.map(f -> ResponseEntity.ok(new TabFunDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<TabFunDTO> getFunctionByName(@RequestParam String name) {
        Optional<tabFun> function = service.findSingleFunction(name);
        return function.map(f -> ResponseEntity.ok(new TabFunDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public List<TabFunDTO> getFunctionsWithFilter(
            @RequestParam(required = false) Integer[] types,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.findMultipleWithSorting(types, sortBy, direction).stream()
                .map(TabFunDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TabFunDTO createFunction(@RequestBody tabFun function) {
        tabFun saved = service.save(function);
        return new TabFunDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabFunDTO> updateFunction(@PathVariable Long id, @RequestBody tabFun function) {
        function.setId(id);
        tabFun updated = service.save(function);
        return ResponseEntity.ok(new TabFunDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}