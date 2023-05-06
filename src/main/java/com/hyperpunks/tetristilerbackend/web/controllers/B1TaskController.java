package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.B1TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/a1-task")
public class B1TaskController {

    private final B1TaskService b1TaskService;

    public B1TaskController(B1TaskService b1TaskService){
        this.b1TaskService=b1TaskService;
    }

    @GetMapping("min-canvas-for-all-shapes")
    public ResponseEntity<Object> minCanvas(){
        return b1TaskService.fitAllShapes();
    }
}
