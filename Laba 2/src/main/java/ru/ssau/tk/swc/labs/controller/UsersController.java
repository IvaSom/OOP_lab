package ru.ssau.tk.swc.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.swc.labs.dto.UserAuthDTO;
import ru.ssau.tk.swc.labs.dto.UserDTO;
import ru.ssau.tk.swc.labs.entity.users;
import ru.ssau.tk.swc.labs.service.UsersService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersService service;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return service.findAllUsers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<users> user = service.findUserById(id);
        return user.map(u -> ResponseEntity.ok(new UserDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }



    @GetMapping("/search/login")
    public ResponseEntity<UserDTO> getUserByLogin(@RequestParam String login) {
        Optional<users> user = service.findUserByLogin(login);
        return user.map(u -> ResponseEntity.ok(new UserDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        Optional<users> user = service.findUserByEmail(email);
        return user.map(u -> ResponseEntity.ok(new UserDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/auth")
    public ResponseEntity<UserDTO> authenticateUser(@RequestBody UserAuthDTO authDTO) {
        Optional<users> user = service.authenticateUser(authDTO.getLogin(), authDTO.getPassword());
        return user.map(u -> ResponseEntity.ok(new UserDTO(u)))
                .orElse(ResponseEntity.status(401).build());
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
    public ResponseEntity<UserDTO> registerUser(@RequestBody users user) {
        if (service.isLoginExists(user.getLogin())) {
            return ResponseEntity.badRequest().build();
        }
        if (service.isEmailExists(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        users savedUser = service.save(user);
        return ResponseEntity.ok(new UserDTO(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody users user) {
        user.setId(id);
        users updated = service.save(user);
        return ResponseEntity.ok(new UserDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}