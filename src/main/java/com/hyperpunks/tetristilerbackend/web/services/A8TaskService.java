package com.hyperpunks.tetristilerbackend.web.services;

import com.hyperpunks.tetristilerbackend.library.Grid;
import com.hyperpunks.tetristilerbackend.library.Shape;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class A8TaskService {

    class ResponseItem{
        public String grid;
        public int perimeter;
        public long timer;

        public ResponseItem(String grid, int perimeter,long timer) {
            this.grid=grid;
            this.perimeter = perimeter;
            this.timer=timer;
        }
    }



    public ResponseEntity<Object> getPerimeter(){
        long startTime = System.currentTimeMillis();
        Shape shapeOne = Shape.fromString("F");
        Shape shapeTwo = Shape.fromString("U");
        int gridSizeX = 5;
        int gridSizeY = 5;
        Grid grid = new Grid(gridSizeX,gridSizeY);
        boolean placedOne = grid.place(1, 1, shapeOne);
        boolean placedTwo = grid.place(3, 0,shapeTwo);
        if (!placedOne || !placedTwo) {
            return ResponseEntity.internalServerError().body("Failed to place the shapes on the " + gridSizeX + "x" + gridSizeY + " grid");
        }
        List<ResponseItem> items = new ArrayList<>();
        int perimeter = grid.calculatePerimeter();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        items.add(new ResponseItem(grid.toString(),perimeter,executionTime));
        return ResponseEntity.ok(items);
    }


}
