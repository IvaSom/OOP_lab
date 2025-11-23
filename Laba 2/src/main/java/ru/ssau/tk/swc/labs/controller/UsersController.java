package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.entity.users;
import ru.ssau.tk.swc.labs.service.UsersService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersService service;

    @GetMapping
    public List<users> getAllUsers() {
        return service.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<users> getUserById(@PathVariable Long id) {
        Optional<users> user = service.findUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/login")
    public ResponseEntity<users> getUserByLogin(@RequestParam String login) {
        Optional<users> user = service.findUserByLogin(login);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/email")
    public ResponseEntity<users> getUserByEmail(@RequestParam String email) {
        Optional<users> user = service.findUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public List<users> getUsersWithFilter(
            @RequestParam(required = false) List<String> names,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        String[] namesArray = names != null ? names.toArray(new String[0]) : null;
        return service.findMultipleWithSorting(namesArray, sortBy, direction);
    }

    @PostMapping("/auth")
    public ResponseEntity<users> authenticateUser(
            @RequestParam String login,
            @RequestParam String password) {

        Optional<users> user = service.authenticateUser(login, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/check-login")
    public ResponseEntity<Boolean> checkLoginExists(@RequestParam String login) {
        boolean exists = service.isLoginExists(login);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = service.isEmailExists(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/register")
    public ResponseEntity<users> registerUser(@RequestBody users user) {
        if (service.isLoginExists(user.getLogin())) {
            return ResponseEntity.badRequest().build(); //логин занят
        }
        if (service.isEmailExists(user.getEmail())) {
            return ResponseEntity.badRequest().build(); //email занят
        }

        users savedUser = service.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<users> updateUser(@PathVariable Long id, @RequestBody users user) {
        user.setId(id);
        users updated = service.save(user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}