package org.epam.trainerworkloadservice.controller;

import jakarta.validation.Valid;
import org.epam.trainerworkloadservice.dto.request.TrainerWorkloadRequest;
import org.epam.trainerworkloadservice.service.TrainerWorkloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workloads")
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;

    public TrainerWorkloadController(
            TrainerWorkloadService trainerWorkloadService) {
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @PostMapping
    public ResponseEntity<Void> updateWorkload(
            @Valid @RequestBody TrainerWorkloadRequest request) {

        trainerWorkloadService.updateWorkload(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{trainerUsername}/years/{year}/months/{month}")
    public ResponseEntity<Integer> getMonthlyWorkload(
            @PathVariable String trainerUsername,
            @PathVariable int year,
            @PathVariable int month) {

        Integer duration = trainerWorkloadService.getMonthlyWorkload(
                trainerUsername,
                year,
                month
        );

        if (duration == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(duration);
    }
}