package com.hyperpunks.tetristilerbackend.web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperpunks.tetristilerbackend.library.Grid;
import com.hyperpunks.tetristilerbackend.library.Shape;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class A4TaskService {

    public static class RequestForm {
        public int gridSizeX;
        public int gridSizeY;
        public List<int[]> blackHoles;
        public String letter;
        public boolean allowRotations;
        public boolean allowFlip;

        public RequestForm() {
        }

        public RequestForm(int gridSizeX, int gridSizeY, List<int[]> blackHoles, String letter, boolean allowRotations, boolean allowFlip) {
            this.gridSizeX = gridSizeX;
            this.gridSizeY = gridSizeY;
            this.blackHoles = blackHoles;
            this.letter = letter;
            this.allowRotations = allowRotations;
            this.allowFlip = allowFlip;
        }
    }


    private RequestForm deserializeRequestJSON(String requestJSON) throws JSONException {
        ObjectMapper mapper = new ObjectMapper();
        RequestForm requestForm;
        try {
            requestForm = mapper.readValue(requestJSON, RequestForm.class);
        } catch (IOException e) {
            // Handle any deserialization errors
            e.printStackTrace();
            return null;
        }
        return requestForm;
    }

    public ResponseEntity<Object> getPositions(String requestJSON) {
        long startTime = System.currentTimeMillis();
        RequestForm requestForm;
        try {
            requestForm = deserializeRequestJSON(requestJSON);
        } catch (JSONException e) {
            return ResponseEntity.badRequest().body("JSON is invalid, error: " + e);
        }
        if (requestForm == null) {
            return ResponseEntity.badRequest().body("Could not deserialize the JSON");
        }
        int gridSizeX = requestForm.gridSizeX;
        int gridSizeY = requestForm.gridSizeY;
        if (gridSizeX <= 0 || gridSizeY <= 0) {
            return ResponseEntity.badRequest().body("Invalid grid size parameters " + gridSizeY + ", " + gridSizeY);
        }
        if (requestForm.letter.length() != 1) {
            return ResponseEntity.badRequest().body("Invalid letter parameter");
        }
        for (int[] blackHole : requestForm.blackHoles) {
            if (blackHole.length != 2) {
                return ResponseEntity.badRequest().body("Invalid black hole parameter: " + Arrays.toString(blackHole));
            }
        }

        List<Shape> variants = Shape.fromString(requestForm.letter).generateAllVariants(requestForm.allowRotations, requestForm.allowFlip);
        Grid grid = Grid.withBlacks(gridSizeX, gridSizeY, requestForm.blackHoles);
        List<String> results = new ArrayList<>();
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                for (Shape variant : variants) {
                    if (grid.canFit(x, y, variant)) {
                        Grid clonedGrid = grid.clone();
                        clonedGrid.place(x, y, variant);
                        results.add(clonedGrid.toString());
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        List<String> resultsWithExecutionTime = new ArrayList<>(results);
        resultsWithExecutionTime.add(String.valueOf(executionTime));
        return ResponseEntity.ok(resultsWithExecutionTime);
    }
}
