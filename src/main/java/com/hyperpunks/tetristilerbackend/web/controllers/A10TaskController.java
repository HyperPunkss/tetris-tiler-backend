package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.A10TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/a10-task")
@CrossOrigin(origins = "*")
public class A10TaskController {

    private final A10TaskService a10TaskService;

    public A10TaskController(A10TaskService a10TaskService){
        this.a10TaskService=a10TaskService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> listUniqueSolution(@RequestBody String requestJSON){
        return a10TaskService.getUniqueSolutions(requestJSON);
    }
}
