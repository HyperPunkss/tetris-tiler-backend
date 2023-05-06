package com.hyperpunks.tetristilerbackend.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid {
    private final String[][] grid; // y, x

    private final EmptyGrouper emptyGrouper;


    public Grid(int gridSizeX, int gridSizeY) {
        grid = new String[gridSizeX][gridSizeY];
        for (String[] row : grid) {
            Arrays.fill(row, "E");
        }
        emptyGrouper = new EmptyGrouper(gridSizeX, gridSizeY);
    }

    public Grid(String stringedGrid) {
        String[] rows = stringedGrid.split(" ");
        grid = new String[rows.length][rows[0].length()];
        for (int y = 0; y < grid.length; y++) {
            grid[y] = rows[grid.length - y - 1].split("");
        }
        emptyGrouper = new EmptyGrouper(grid);
    }

    private Grid(String[][] grid) {
        this.grid = grid;
        emptyGrouper = new EmptyGrouper(grid);
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

    public String toDisplayString() {
        StringBuilder result = new StringBuilder();
        for (int y = grid.length - 1; y >= 0; y--) {
            result.append(String.join("", grid[y]));
            result.append("\n");
        }
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
        if (value.equals("E")) {
            emptyGrouper.emptyTiles(List.of(new int[]{x, y}));
        } else {
            emptyGrouper.fillTiles(List.of(new int[]{x, y}));
        }
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

    public boolean canFitAndTouch(int positionX, int positionY, Shape shape) {
        if (!canFit(positionX, positionY, shape)) {
            return false;
        }
        for (int[] shapeCoordinate : shape.getLocalCoordinates()) {
            int x = shapeCoordinate[0] + positionX;
            int y = shapeCoordinate[1] + positionY;
            for (int[] neighbourCoordinate : neighbouringCoordinates(x, y)) {
                String neighbourTile = grid[neighbourCoordinate[1]][neighbourCoordinate[0]];
                if (!neighbourTile.equals("E") && !neighbourTile.equals("B")) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<int[]> neighbouringCoordinates(int positionX, int positionY) {
        List<int[]> neighbours = new ArrayList<>(4);
        if (positionX > 0) {
            neighbours.add(new int[]{positionX - 1, positionY});
        }
        if (positionX < grid[0].length - 1) {
            neighbours.add(new int[]{positionX + 1, positionY});
        }
        if (positionY > 0) {
            neighbours.add(new int[]{positionX, positionY - 1});
        }
        if (positionY < grid.length - 1) {
            neighbours.add(new int[]{positionX, positionY + 1});
        }
        return neighbours;
    }

    public boolean place(int positionX, int positionY, Shape shape) {
        if (!canFit(positionX, positionY, shape)) {
            return false;
        }
        List<int[]> updatedCoordinates = new ArrayList<>(5);
        for (int[] shapeCoordinate : shape.getLocalCoordinates()) {
            int x = shapeCoordinate[0] + positionX;
            int y = shapeCoordinate[1] + positionY;
            grid[y][x] = shape.getName();
            updatedCoordinates.add(new int[]{x, y});
        }
        emptyGrouper.fillTiles(updatedCoordinates);
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

    public List<int[]> findAllPlacements(Shape shape) {
        List<int[]> placements = new ArrayList<>();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (canFit(x, y, shape)) {
                    placements.add(new int[]{x, y});
                }
            }
        }
        return placements;
    }

    public List<int[]> findAllTouchingPlacements(Shape shape) {
        List<int[]> placements = new ArrayList<>();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (canFitAndTouch(x, y, shape)) {
                    placements.add(new int[]{x, y});
                }
            }
        }
        return placements;
    }

    public boolean placeBottomLeftmost(Shape shape) {
        List<int[]> availablePlacements = findAllPlacements(shape);
        if (availablePlacements.size() == 0) {
            return false;
        }
        int[] firstPosition = availablePlacements.get(0);
        return place(firstPosition[0], firstPosition[1], shape);
    }

    public boolean placeTouching(Shape shape) {
        List<int[]> availablePlacements = findAllTouchingPlacements(shape);
        if (availablePlacements.size() == 0) {
            return false;
        }
        int[] firstPosition = availablePlacements.get(0);
        return place(firstPosition[0], firstPosition[1], shape);
    }

    public String getEmptiness() {
        return emptyGrouper.getAllGroupSizes().toString();
    }


    public int calculatePerimeter() {
        int count = 0;
        for (int y = 0; y < getSizeY(); y++) {
            for (int x = 0; x < getSizeX(); x++) {
                if (grid[y][x].equals("E") || grid[y][x].equals("B")) {
                    continue;
                }
                if (x < getSizeX() - 1 && (grid[y][x + 1].equals("E") || grid[y][x + 1].equals("B"))) {
                    count++;
                } else if (x == getSizeX() - 1) {
                    count++;
                }

                if (x > 0 && (grid[y][x - 1].equals("E") || grid[y][x - 1].equals("B"))) {
                    count++;
                } else if (x == 0) {
                    count++;
                }

                if (y < getSizeY() - 1 && (grid[y + 1][x].equals("E") || grid[y + 1][x].equals("B"))) {
                    count++;
                } else if (y == getSizeY() - 1) {
                    count++;
                }

                if (y > 0 && (grid[y - 1][x].equals("E") || grid[y - 1][x].equals("B"))) {
                    count++;
                } else if (y == 0) {
                    count++;
                }
            }
        }
        return count;
    }

}
