package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.A3TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/a3-task/")
public class A3TaskController {
    private final A3TaskService a3TaskService;

    public A3TaskController(A3TaskService a3TaskService){
        this.a3TaskService=a3TaskService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getVariations(@RequestParam String letter){
        return a3TaskService.getVariations(letter);
    }
}
