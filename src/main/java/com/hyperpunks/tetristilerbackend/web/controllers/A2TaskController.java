package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.A2TaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("a2-task")
public class A2TaskController {

    private final A2TaskService a2TaskService;

    public A2TaskController(A2TaskService a2TaskService){
        this.a2TaskService=a2TaskService;
    }
}
