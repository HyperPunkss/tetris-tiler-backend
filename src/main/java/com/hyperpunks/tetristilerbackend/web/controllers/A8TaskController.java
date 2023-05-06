package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.A8TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/a8-task")
public class A8TaskController {

    private final A8TaskService a8TaskService;

    public A8TaskController(A8TaskService a8TaskService){
        this.a8TaskService=a8TaskService;
    }

    @GetMapping("")
    public ResponseEntity<Object> listPerimeter(){
        return a8TaskService.getPerimeter();
    }
}
