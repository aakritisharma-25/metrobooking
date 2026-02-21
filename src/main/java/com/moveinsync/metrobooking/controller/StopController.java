package com.moveinsync.metrobooking.controller;

import com.moveinsync.metrobooking.model.Stop;
import com.moveinsync.metrobooking.repository.StopRepository;
import com.moveinsync.metrobooking.service.GraphService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stops")
@RequiredArgsConstructor
@Slf4j
public class StopController {

    private final StopRepository stopRepository;
    private final GraphService graphService;

    @GetMapping
    public ResponseEntity<List<Stop>> getAllStops() {
        return ResponseEntity.ok(stopRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stop> getStopById(@PathVariable Long id) {
        return stopRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Stop> createStop(@RequestBody Stop stop) {
        Stop saved = stopRepository.save(stop);
        graphService.refreshGraph();
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stop> updateStop(@PathVariable Long id, @RequestBody Stop stop) {
        if (!stopRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        stop.setId(id);
        Stop updated = stopRepository.save(stop);
        graphService.refreshGraph();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStop(@PathVariable Long id) {
        if (!stopRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        stopRepository.deleteById(id);
        graphService.refreshGraph();
        return ResponseEntity.ok("Stop deleted successfully");
    }
}