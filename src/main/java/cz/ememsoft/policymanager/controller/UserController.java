package cz.ememsoft.policymanager.controller;

import cz.ememsoft.policymanager.model.User;
import cz.ememsoft.policymanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        return ResponseEntity.ok(userService.getUserByName(name));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping("/{name}")
    public ResponseEntity<User> updateUser(@PathVariable String name, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(name, user));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteUser(@PathVariable String name) {
        userService.deleteUser(name);
        return ResponseEntity.noContent().build();
    }
}