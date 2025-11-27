package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.CompFunDTO;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.service.CompFunService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comp-fun")
public class CompFunController {

    @Autowired
    private CompFunService service;

    @GetMapping
    public List<CompFunDTO> getAllFunctions() {
        return service.findAllFunctions().stream()
                .map(CompFunDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompFunDTO> getFunctionById(@PathVariable Long id) {
        Optional<compFun> function = service.findSingleFunctionById(id);
        return function.map(f -> ResponseEntity.ok(new CompFunDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<CompFunDTO> getFunctionByName(@RequestParam String name) {
        Optional<compFun> function = service.findSingleFunction(name);
        return function.map(f -> ResponseEntity.ok(new CompFunDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public List<CompFunDTO> getFunctionsWithFilter(
            @RequestParam(required = false) List<String> names,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        String[] namesArray = names != null ? names.toArray(new String[0]) : null;
        return service.findMultipleWithSorting(namesArray, sortBy, direction).stream()
                .map(CompFunDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public CompFunDTO createFunction(@RequestBody compFun function) {
        compFun saved = service.save(function);
        return new CompFunDTO(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompFunDTO> updateFunction(@PathVariable Long id, @RequestBody compFun function) {
        function.setId(id);
        compFun updated = service.save(function);
        return ResponseEntity.ok(new CompFunDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}