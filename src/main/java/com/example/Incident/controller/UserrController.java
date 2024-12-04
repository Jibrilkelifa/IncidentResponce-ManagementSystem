package com.example.Incident.controller;

import com.example.Incident.model.Userr;
import com.example.Incident.repo.UserrRepository;
import com.example.Incident.services.UserrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserrController {
    @Autowired
    private UserrRepository userRepository;
    @Autowired
    private UserrService userrService;

    public UserrController() {
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<Userr> createUser(@RequestBody Userr user) {
        Userr savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SMS_ADMIN','SMS_USER')")
    public ResponseEntity<List<Userr>> getAllUsers() {
        List<Userr> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Userr> getUserById(@PathVariable Long id) {
        Optional<Userr> userr = userrService.getUserById(id);
        return userr.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<Userr> updateUser(@PathVariable Long id, @RequestBody Userr userr) {
        Userr updatedUser = userrService.updateUser(id, userr);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SMS_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userrService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

