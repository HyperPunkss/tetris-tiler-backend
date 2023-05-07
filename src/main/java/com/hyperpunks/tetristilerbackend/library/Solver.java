package com.hyperpunks.tetristilerbackend.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Solver {
    static void permute(java.util.List<Integer> arr, int k){
        for(int i = k; i < arr.size(); i++){
            java.util.Collections.swap(arr, i, k);
            permute(arr, k+1);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() -1){
            System.out.println(java.util.Arrays.toString(arr.toArray()));
        }
    }

    public static Set<String> findAllSolutions(Grid startingGrid, List<Shape> shapes, boolean canRotate, boolean canFlip) {
        Set<String> solutions = new TreeSet<>();
        if (startingGrid.countUnfilled() % 5 != 0) {
            return solutions;
        }

        List<List<Shape>> allShapesAndVariants = new ArrayList<>();
        for (Shape shape : shapes) {
            allShapesAndVariants.add(shape.generateAllVariants(canRotate, canFlip));
        }

        List<Grid> gridStates = new ArrayList<>();
        gridStates.add(startingGrid);
        List<Grid> nextGridStates = new ArrayList<>();
        for (List<Shape> shape : allShapesAndVariants) {
            for (Shape shapeVariant : shape) {
                for (int i = 0; i < gridStates.size(); i++) {
                    Grid stateGrid = gridStates.get(i);
                    Grid nextGrid = stateGrid.clone();
                    List<int[]> allPlacements;
                    if (gridStates.size() == 1) {
                        allPlacements = nextGrid.findAllPlacements(shapeVariant);
                    } else {
                        allPlacements = nextGrid.findAllTouchingPlacements(shapeVariant);
                    }
                    for (int[] coordinates : allPlacements) {
                        if (nextGrid.place(coordinates[0], coordinates[1], shapeVariant)) {
                            boolean foundWrongSize = false;
                            for (int size : nextGrid.getAllEmptyGroupsSizes()) {
                                if (size % 5 != 0) {
                                    nextGrid = stateGrid.clone();
                                    foundWrongSize = true;
                                    break;
                                }
                            }
                            if (foundWrongSize) {
                                break;
                            }
                            if (nextGrid.countUnfilled() == 0) {
                                solutions.add(nextGrid.toString());
                            } else {
                                nextGridStates.add(nextGrid);
                                nextGrid = stateGrid.clone();
                            }
                        }
                    }
                }
            }
            gridStates.clear();
            gridStates.addAll(nextGridStates);
        }
        return solutions;
    }
}
