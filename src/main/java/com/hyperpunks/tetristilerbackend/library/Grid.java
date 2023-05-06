package com.hyperpunks.tetristilerbackend.library;

import java.util.Arrays;
import java.util.List;

public class Grid {
    private String[][] grid;

    public Grid(int gridSizeX, int gridSizeY) {
        grid = new String[gridSizeX][gridSizeY];
        for (String[] row : grid) {
            Arrays.fill(row, "E");
        }
    }

    public Grid(String stringedGrid) {
        String[] rows = stringedGrid.split(" ");
        grid = new String[rows.length][rows[0].length()];
        for (int y = 0; y < grid.length; y++) {
            grid[y] = rows[grid.length - y - 1].split("");
        }
    }

    public static Grid withBlacks(int gridSizeX, int gridSizeY, List<int[]> blackCoordinates) {
        Grid grid = new Grid(gridSizeX, gridSizeY);
        for (int[] blackCoordinate : blackCoordinates) {
            grid.set(blackCoordinate[0], blackCoordinate[1], "B");
        }
        return grid;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int y = grid.length - 1; y >= 0; y--) {
            result.append(String.join("", grid[y]));
            result.append(" ");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public void set(int x, int y, String value) {
        grid[y][x] = value;
    }

    public boolean canFit(int positionX, int positionY, Shape shape) {
        for (int[] shapeCoordinate : shape.getLocalCoordinates()) {
            int x = shapeCoordinate[0] + positionX;
            int y = shapeCoordinate[1] + positionY;
            String tile = grid[y][x];
            if (!tile.equals("E")) {
                return false;
            }
        }
        return true;
    }

    public boolean place(int positionX, int positionY, Shape shape) {
        if (!canFit(positionX, positionY, shape)) {
            return false;
        }
        for (int[] shapeCoordinate : shape.getLocalCoordinates()) {
            int x = shapeCoordinate[0] + positionX;
            int y = shapeCoordinate[1] + positionY;
            grid[y][x] = shape.getName();
        }
        return true;
    }
}
