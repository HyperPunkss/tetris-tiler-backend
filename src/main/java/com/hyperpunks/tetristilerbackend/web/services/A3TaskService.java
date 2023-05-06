package com.hyperpunks.tetristilerbackend.web.services;

import com.hyperpunks.tetristilerbackend.library.Grid;
import com.hyperpunks.tetristilerbackend.library.Shape;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class A3TaskService {
    class VariationResult {
        public final String name;
        public final String content;

        public VariationResult(String name, String content) {
            this.name = name;
            this.content = content;
        }
    }

    public ResponseEntity<Object> getVariations(String letter) {
        int gridSizeX = 5;
        int gridSizeY = 5;
        int centerX = gridSizeX / 2;
        int centerY = gridSizeY / 2;

        if (letter.length() != 1) {
            return ResponseEntity.badRequest().body("Letter parameter must be one character");
        }
        Shape shape = Shape.fromString(letter);
        if (shape == null) {
            return ResponseEntity.badRequest().body("Invalid letter parameter");
        }

        List<VariationResult> variations = new ArrayList<>();
        int i = 1;
        for (Shape variant : shape.generateAllVariants()) {
            Grid grid = new Grid(gridSizeX, gridSizeY);
            boolean placed = grid.place(centerX, centerY, variant);
            if (!placed) {
                return ResponseEntity.internalServerError().body("Failed to place " + variant.getName() + " on the " + gridSizeX + "x" + gridSizeY + " grid");
            }
            variations.add(new VariationResult(variant.getName() + i, grid.toString()));
            i++;
        }
        return ResponseEntity.ok(variations);
    }
}
