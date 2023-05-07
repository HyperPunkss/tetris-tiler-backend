package com.hyperpunks.tetristilerbackend.web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperpunks.tetristilerbackend.library.Grid;
import com.hyperpunks.tetristilerbackend.library.Shape;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class A7TaskService {

    public static class RequestForm {
        public String letter1;
        public String letter2;
        public int gridSizeX;
        public int gridSizeY;


        public RequestForm() {
        }

        public RequestForm(String letter1, String letter2, int gridSizeX, int gridSizeY) {
            this.letter1 = letter1;
            this.letter2 = letter2;
            this.gridSizeX = gridSizeX;
            this.gridSizeY = gridSizeY;
        }
    }

    private class ResponseForm {
        public String grid;
        public long timeTaken;

        ResponseForm(String grid, long timeTaken) {
            this.grid = grid;
            this.timeTaken = timeTaken;
        }
    }

    private RequestForm deserializeRequestJSON(String requestJSON) throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        RequestForm requestForm;
        requestForm = mapper.readValue(requestJSON, RequestForm.class);
        return requestForm;
    }

    public ResponseEntity<Object> getTwoShapes(String requestJSON) {
        long startTime = System.currentTimeMillis();
        RequestForm requestForm;
        try {
            requestForm = deserializeRequestJSON(requestJSON);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not parse JSON, error: " + e);
        }
        Grid grid = new Grid(requestForm.gridSizeX, requestForm.gridSizeY);
        Shape shape1 = Shape.fromString(requestForm.letter1);
        Shape shape2 = Shape.fromString(requestForm.letter2);
        assert shape1 != null;
        assert shape2 != null;
        Grid resultGrid = grid.tryPlacing2(shape1, shape2);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        if (resultGrid == null) {
            return ResponseEntity.status(69).body("There is no way to arrange these shapes in this grid.\nIt took " + executionTime + " ms to find that out.");
        }
        return ResponseEntity.ok(new ResponseForm(resultGrid.toString(), executionTime));
    }

}
