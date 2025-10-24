package com.example.Incident.controller;

import com.example.Incident.model.ToolHealthCheckDTO;
import com.example.Incident.model.ToolHealthCheckSession;
import com.example.Incident.services.ToolHealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tool-health-check")
@RequiredArgsConstructor
public class ToolHealthCheckController {

    private final ToolHealthCheckService service;

    @PostMapping
    public ResponseEntity<?> submitCheck(@RequestBody ToolHealthCheckDTO dto) {
        ToolHealthCheckSession saved = service.saveSession(dto);
        return ResponseEntity.ok(Map.of("sessionId", saved.getId()));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getSession(@PathVariable Long id) {
//        return service.sessionRepo.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}

