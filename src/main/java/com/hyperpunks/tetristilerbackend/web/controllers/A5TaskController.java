package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.A5TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/a5-task")
public class A5TaskController {
    private final A5TaskService a5TaskService;

    public A5TaskController(A5TaskService a5TaskService) {
        this.a5TaskService = a5TaskService;
    }

    @PostMapping("")
    public ResponseEntity<Object> getRandomFilledUnfilled(@RequestBody String requestJSON) {
        return a5TaskService.getRandomFilledUnfilled(requestJSON);
    }

}
