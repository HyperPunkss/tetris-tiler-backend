package com.hyperpunks.tetristilerbackend.web.services;

import com.hyperpunks.tetristilerbackend.library.Grid;
import com.hyperpunks.tetristilerbackend.library.Shape;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class A2TaskService {


    class ResponseItem{
        public String letter;
        public String content;

        public ResponseItem(String letter, String content) {
            this.letter = letter;
            this.content = content;
        }


    }

    public ResponseEntity<Object> getAllShapes(){
        int gridSizeX = 5;
        int gridSizeY = 5;
        int centerX = gridSizeX / 2;
        int centerY = gridSizeY / 2;

        List<ResponseItem> items = new ArrayList<>();
        for(Shape shape :  Shape.getAllShapes()){
            Grid grid    = new Grid(gridSizeX,gridSizeY);
            boolean placed = grid.place(centerX, centerY, shape);
            if (!placed) {
                return ResponseEntity.internalServerError().body("Failed to place " + shape.getName() + " on the " + gridSizeX + "x" + gridSizeY + " grid");
            }
            items.add(new ResponseItem(shape.getName(),grid.toString()));
        }
        return  ResponseEntity.ok(items);
    }

}
