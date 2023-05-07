package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.A7TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/a7-task")
@CrossOrigin(origins = "*")
public class A7TaskController {
    private final A7TaskService a7TaskService;

    public A7TaskController(A7TaskService a7TaskService){
        this.a7TaskService=a7TaskService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> listTwoShapes(@RequestBody String requestJSON){
        return a7TaskService.getTwoShapes(requestJSON);
    }



}
