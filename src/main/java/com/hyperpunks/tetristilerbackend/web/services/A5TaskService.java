package com.hyperpunks.tetristilerbackend.web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperpunks.tetristilerbackend.library.Grid;
import com.hyperpunks.tetristilerbackend.library.Shape;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class A5TaskService {
    public static class RequestForm {
        public int gridSizeX;
        public int gridSizeY;
        public List<int[]> blackHoles;
    }

    private RequestForm deserializeRequestJSON(String requestJSON) throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        RequestForm requestForm;
        requestForm = mapper.readValue(requestJSON, RequestForm.class);
        return requestForm;
    }

    private class ResponseForm {
        public String grid;
        public int filled;
        public int unfilled;
    }

    public ResponseEntity<Object> getRandomFilledUnfilled(String requestJSON) {
        long startTime = System.currentTimeMillis();
        RequestForm requestForm;
        try {
            requestForm = deserializeRequestJSON(requestJSON);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not parse JSON, error: " + e);
        }
        Grid grid = Grid.withBlacks(requestForm.gridSizeX, requestForm.gridSizeY, requestForm.blackHoles);

        Random random = new Random();
        List<Shape> allShapesAndVariants = Shape.generateAllShapesAndVariants();
        for (int attempt = 0; attempt < 4096; attempt++) {
            Shape randomShape = allShapesAndVariants.get(random.nextInt(allShapesAndVariants.size()));
            int randomX = random.nextInt(grid.getSizeX());
            int randomY = random.nextInt(grid.getSizeY());
            if (grid.place(randomX, randomY, randomShape)) {
                ResponseForm responseForm = new ResponseForm();
                responseForm.grid = grid.toString();
                responseForm.filled = grid.countFilled();
                responseForm.unfilled = grid.countUnfilled();
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;
                List<Object> responseFormWithExecutionTime = new ArrayList<>(Collections.singleton(responseForm));
                responseFormWithExecutionTime.add(String.valueOf(executionTime));
                return ResponseEntity.ok(responseFormWithExecutionTime);
            }
        }

        return ResponseEntity.internalServerError().body("Failed to place random shapes");
    }
}
