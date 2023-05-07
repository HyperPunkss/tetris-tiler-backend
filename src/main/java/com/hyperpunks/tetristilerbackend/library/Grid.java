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
            grid.set(blackCoordinate[1], blackCoordinate[0], "B");
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
            emptyGrouper.emptyTile(x, y);
        } else {
            emptyGrouper.fillTile(x, y);
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
        for (int[] shapeCoordinate : shape.getLocalCoordinates()) {
            int x = shapeCoordinate[0] + positionX;
            int y = shapeCoordinate[1] + positionY;
            grid[y][x] = shape.getName();
            emptyGrouper.fillTile(x, y);
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

    public Grid tryPlacing2(Shape shape1, Shape shape2) {
        List<Shape> shape1Variants = shape1.generateAllVariants();
        List<Shape> shape2Variants = shape2.generateAllVariants();
        for (Shape shape1Variant : shape1Variants) {
            for (int[] shape1Placements : findAllPlacements(shape1Variant)) {
                Grid shape1Grid = clone();
                boolean placed = shape1Grid.place(shape1Placements[0], shape1Placements[1], shape1);
                if (!placed) {
                    continue;
                }
                if (1 < shape1Grid.getAllEmptyGroupsSizes().size()) {
                    continue;
                }
                for (Shape shape2Variant : shape2Variants) {
                    for (int[] shape2Placements : shape1Grid.findAllTouchingPlacements(shape2Variant)) {
                        Grid shape2Grid = shape1Grid.clone();
                        placed = shape2Grid.place(shape2Placements[0], shape2Placements[1], shape2Variant);
                        if (!placed) {
                            continue;
                        }
                        if (shape2Grid.getAllEmptyGroupsSizes().size() == 1) {
                            return shape2Grid;
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<Integer> getAllEmptyGroupsSizes() {
        return emptyGrouper.getAllGroupSizes();
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
