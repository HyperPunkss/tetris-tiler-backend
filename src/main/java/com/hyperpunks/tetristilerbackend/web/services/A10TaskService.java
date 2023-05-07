package com.hyperpunks.tetristilerbackend.web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperpunks.tetristilerbackend.library.Grid;
import com.hyperpunks.tetristilerbackend.library.Shape;
import com.hyperpunks.tetristilerbackend.library.Solver;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class A10TaskService {
    public static class RequestForm {
        public int gridSizeX;
        public int gridSizeY;
        public boolean allowFlips;
        public boolean allowRotations;
        public List<int[]> blackHoles;
        public List<String> letters;


        public RequestForm() {
        }

        public RequestForm(int gridSizeX, int gridSizeY, boolean allowFlips, boolean allowRotations, List<int[]> blackHoles,List<String> letters) {
            this.gridSizeX = gridSizeX;
            this.gridSizeY = gridSizeY;
            this.allowFlips = allowFlips;
            this.allowRotations = allowRotations;
            this.blackHoles = blackHoles;
            this.letters=letters;
        }
    }
    private class ResponseForm {
        public int solutions;
        public long timeTaken;

        ResponseForm(int solutions, long timeTaken) {
            this.solutions = solutions;
            this.timeTaken = timeTaken;
        }
    }
    private RequestForm deserializeRequestJSON(String requestJSON) throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        RequestForm requestForm;
        requestForm = mapper.readValue(requestJSON, RequestForm.class);
        return requestForm;
    }

    public ResponseEntity<Object> getUniqueSolutions(String requestJSON){
        long startTime = System.currentTimeMillis();
        RequestForm requestForm;
        try {
            requestForm = deserializeRequestJSON(requestJSON);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not parse JSON, error: " + e);
        }
        Grid grid = new Grid(requestForm.gridSizeX, requestForm.gridSizeY);
        List<Shape> shapes = requestForm.letters.stream().map(Shape::fromString).toList();
        List<Grid> resultGrids = Solver.findAllSolutions(grid,shapes,requestForm.allowRotations, requestForm.allowFlips);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        List<Object> result = new ArrayList<>(resultGrids);
        result.add(executionTime);
        return ResponseEntity.ok(result);
    }
}
