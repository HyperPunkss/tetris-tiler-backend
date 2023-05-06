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

    public  static class RequestForm {
        public int gridSizeX;
        public int gridSizeY;
        public List<int[]> blackHoles;
        public String letter;

        public RequestForm() {
        }

        public RequestForm(int gridSizeX, int gridSizeY, List<int[]> blackHoles, String letter) {
            this.gridSizeX = gridSizeX;
            this.gridSizeY = gridSizeY;
            this.blackHoles = blackHoles;
            this.letter = letter;
        }
    }


    private RequestForm deserializeRequestJSON(String requestJSON) throws JSONException{
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


//    private RequestForm deserializeRequestJSON(JSONObject requestJSON) throws JSONException {
//        RequestForm requestForm = new RequestForm();
//        requestForm.gridSizeX = requestJSON.getInt("gridSizeX");
//        requestForm.gridSizeY = requestJSON.getInt("gridSizeY");
//        requestForm.letter = requestJSON.getString("letter");
//
//        JSONArray blackHolesJSON = requestJSON.getJSONArray("blackHoles");
//        requestForm.blackHoles = new ArrayList<>();
//        for (int i = 0; i < blackHolesJSON.length(); i++) {
//            JSONArray holeJSON = blackHolesJSON.getJSONArray(i);
//            int x = holeJSON.getInt(0);
//            int y = holeJSON.getInt(1);
//            requestForm.blackHoles.add(new int[]{x, y});
//        }
//
//        return requestForm;
//    }




    public ResponseEntity<Object> getPositions(String requestJSON) {
        RequestForm requestForm;
        try {
            requestForm = deserializeRequestJSON(requestJSON);
        } catch (JSONException e) {
            return ResponseEntity.badRequest().body("JSON is invalid, error: " + e);
        }
        if (requestForm==null){
            return ResponseEntity.badRequest().body("Could not deserialize the JSON");
        }
        int gridSizeX = requestForm.gridSizeX;
        int gridSizeY = requestForm.gridSizeY;
        if (gridSizeX <= 0 || gridSizeY <= 0) {
            return ResponseEntity.badRequest().body("Invalid grid size parameters "+gridSizeY +", "+gridSizeY);
        }
        if (requestForm.letter.length() != 1) {
            return ResponseEntity.badRequest().body("Invalid letter parameter");
        }
        for (int[] blackHole : requestForm.blackHoles) {
            if (blackHole.length != 2) {
                return ResponseEntity.badRequest().body("Invalid black hole parameter: " + Arrays.toString(blackHole));
            }
        }

        List<Shape> variants = Shape.fromString(requestForm.letter).generateAllVariants();
        Grid grid = Grid.withBlacks(gridSizeX, gridSizeY, requestForm.blackHoles);
        List<String> results = new ArrayList<>();
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                for (Shape variant : variants) {
                    if (grid.canFit(x, y, variant)) {
                        Grid clonedGrid = grid.clone();
                        clonedGrid.place(x, y, variant);
                        results.add(grid.toString());
                    }
                }
            }
        }
        return ResponseEntity.ok(results);
    }
}
