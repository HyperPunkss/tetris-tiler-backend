package com.hyperpunks.tetristilerbackend.web.controllers;

import com.hyperpunks.tetristilerbackend.web.services.A4TaskService;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/a4-task")
public class A4TaskController {

    private final A4TaskService a4TaskService;

    public A4TaskController(A4TaskService a4TaskService){
        this.a4TaskService=a4TaskService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Object> listAllowedPositions(@RequestBody String requestJSON){
        return a4TaskService.getPositions(requestJSON);
    }
}
