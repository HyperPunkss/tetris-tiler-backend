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

    private Grid(String[][] grid) {
        this.grid = grid;
    }

    public static Grid withBlacks(int gridSizeX, int gridSizeY, List<int[]> blackCoordinates) {
        Grid grid = new Grid(gridSizeX, gridSizeY);
        for (int[] blackCoordinate : blackCoordinates) {
            grid.set(blackCoordinate[0], blackCoordinate[1], "B");
        }
        return grid;
    }

    @Override
    public Grid clone() {
        String[][] clonedGrid = new String[this.grid.length][];
        for (int i = 0; i < this.grid.length; i++) {
            clonedGrid[i] = this.grid[i].clone();
        }
        return new Grid(clonedGrid);
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

    public int getSizeX() {
        return grid.length;
    }

    public int getSizeY() {
        return grid[0].length;
    }

    public void set(int x, int y, String value) {
        grid[y][x] = value;
    }

    public boolean canFit(int positionX, int positionY, Shape shape) {
        for (int[] shapeCoordinate : shape.getLocalCoordinates()) {
            int x = shapeCoordinate[0] + positionX;
            int y = shapeCoordinate[1] + positionY;
            if (x < 0 || grid[0].length <= x) {
                return false;
            }
            if (y < 0 || grid.length <= y) {
                return false;
            }
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

    public int countFilled() {
        int count = 0;
        for (String[] row : grid) {
            for (String tile : row) {
                if (!tile.equals("E") && !tile.equals("B")) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countUnfilled() {
        int count = 0;
        for (String[] row : grid) {
            for (String tile : row) {
                if (tile.equals("E")) {
                    count++;
                }
            }
        }
        return count;
    }
}
