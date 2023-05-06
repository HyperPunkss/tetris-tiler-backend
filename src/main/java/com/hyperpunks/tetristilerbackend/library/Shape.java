package com.hyperpunks.tetristilerbackend.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shape {
    private final String name;
    private final int[][] localCoordinates;

    Shape(String name, int[][] localCoordinates) {
        this.name = name;
        this.localCoordinates = Arrays.stream(localCoordinates).sorted((a, b) -> {
            if (a[1] != b[1]) {
                return -Integer.compare(a[1], b[1]); // Negative so it goes from top to bottom
            } else {
                return Integer.compare(a[0], b[0]);
            }
        }).toArray(int[][]::new);
    }

    public static final Character[] allShapeLetters = {'F', 'I', 'L', 'N', 'P', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static Shape fromString(String name) {
        name = name.trim().toUpperCase();
        if (name.length() != 1 && name.length() != 2) {
            return null;
        }
        return switch (name.charAt(0)) {
            case 'F' -> new Shape("F", new int[][]{{0, 0}, {0, 1}, {1, 1}, {-1, 0}, {0, -1}});
            case 'I' -> new Shape("I", new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, -1}, {0, -2}});
            case 'L' -> new Shape("L", new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, -1}, {1, -1}});
            case 'N' -> new Shape("N", new int[][]{{0, 0}, {0, 1}, {0, 2}, {-1, 0}, {-1, -1}});
            case 'P' -> new Shape("P", new int[][]{{0, 0}, {0, 1}, {1, 1}, {1, 0}, {0, -1}});
            case 'T' -> new Shape("T", new int[][]{{0, 0}, {1, 0}, {-1, 0}, {0, -1}, {0, -2}});
            case 'U' -> new Shape("U", new int[][]{{0, 0}, {-1, 0}, {-1, 1}, {1, 0}, {1, 1}});
            case 'V' -> new Shape("V", new int[][]{{0, 0}, {-1, 0}, {-2, 0}, {0, 1}, {0, 2}});
            case 'W' -> new Shape("W", new int[][]{{0, 0}, {-1, 0}, {-1, 1}, {0, -1}, {1, -1}});
            case 'X' -> new Shape("X", new int[][]{{0, 0}, {-1, 0}, {0, 1}, {1, 0}, {0, -1}});
            case 'Y' -> new Shape("Y", new int[][]{{0, 0}, {-1, 0}, {0, 1}, {0, -1}, {0, -2}});
            case 'Z' -> new Shape("Z", new int[][]{{0, 0}, {0, 1}, {-1, 1}, {0, -1}, {1, -1}});
            default -> null;
        };
    }

    public boolean equals(Shape otherShape) {
        int[][] otherCoordinates = otherShape.localCoordinates;
        if (otherCoordinates.length != this.localCoordinates.length) {
            return false;
        }
        for (int i = 0; i < otherCoordinates.length; i++) {
            boolean foundMatch = false;
            for (int[] otherCoordinate : otherCoordinates) {
                if (this.localCoordinates[i][0] == otherCoordinate[0] && this.localCoordinates[i][1] == otherCoordinate[1]) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                return false;
            }
        }
        return true;
    }

    public boolean canBeFoundIn(List<Shape> list) {
        for (Shape shape : list) {
            if (this.equals(shape)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Shape clone() {
        int[][] clonedCoordinates = new int[this.localCoordinates.length][];
        for (int i = 0; i < this.localCoordinates.length; i++) {
            clonedCoordinates[i] = this.localCoordinates[i].clone();
        }
        return new Shape(this.name, clonedCoordinates);
    }

    public String getName() {
        return name;
    }

    public List<Shape> generateAllVariants() {
        List<Shape> variants = new ArrayList<>();
        variants.add(this.clone());

        // All rotations by 90 degrees
        Shape rotated = this.clone();
        for (int rotation = 0; rotation < 3; rotation++) {
            rotated.rotate90();
            if (!rotated.canBeFoundIn(variants)) {
                variants.add(rotated.clone());
            }
        }

        // Flip horizontally
        Shape flippedHorizontally = this.clone().flipHorizontally();
        if (!flippedHorizontally.canBeFoundIn(variants)) {
            variants.add(flippedHorizontally.clone());
        }
        // All rotations of flipped
        for (int rotation = 0; rotation < 3; rotation++) {
            flippedHorizontally.rotate90();
            if (!flippedHorizontally.canBeFoundIn(variants)) {
                variants.add(flippedHorizontally.clone());
            }
        }

        // Flip vertically
        Shape flippedVertically = this.clone().flipVertically();
        if (!flippedVertically.canBeFoundIn(variants)) {
            variants.add(flippedVertically.clone());
        }
        // All rotations of flipped
        for (int rotation = 0; rotation < 3; rotation++) {
            flippedVertically.rotate90();
            if (!flippedVertically.canBeFoundIn(variants)) {
                variants.add(flippedVertically);
            }
        }

        return variants;
    }

    private Shape rotate90() {
        int[][] coordinates = this.localCoordinates;
        // x' = x*cos(90) - y*sin(90) = -y
        // y' = x*sin(90) + y*cos(90) = x
        for (int i = 0; i < coordinates.length; i++) {
            int bufferX = -coordinates[i][1];
            int bufferY = coordinates[i][0];
            coordinates[i][0] = bufferX;
            coordinates[i][1] = bufferY;
        }
        return this;
    }

    private Shape flipHorizontally() {
        int[][] coordinates = this.localCoordinates;
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i][0] = -coordinates[i][0];
        }
        return this;
    }

    private Shape flipVertically() {
        int[][] coordinates = this.localCoordinates;
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i][1] = -coordinates[i][1];
        }
        return this;
    }

    public String toString() {
        int[][] coordinates = this.localCoordinates;
        int minY = Arrays.stream(coordinates).mapToInt(pair -> pair[1]).min().getAsInt();
        int maxY = Arrays.stream(coordinates).mapToInt(pair -> pair[1]).max().getAsInt();
        StringBuilder lines = new StringBuilder();
        for (int y = maxY; y >= minY; y--) {
            StringBuilder line = new StringBuilder("         ");
            for (int[] coordinate : coordinates) {
                if (coordinate[1] != y) {
                    continue;
                }
                line.setCharAt(coordinate[0] + 4, '#');
            }
            line.append("\n");
            lines.append(line);
        }
        return lines.toString();
    }

    public String toGridString() {
        int gridSizeX = 5;
        int gridSizeY = 5;

        int xAdjustment = (int) ((double) gridSizeX / 2.0);
        int yAdjustment = (int) ((double) gridSizeY / 2.0);

        int[][] globalCoordinates = new int[this.localCoordinates.length][2];
        for (int i = 0; i < globalCoordinates.length; i++) {
            globalCoordinates[i][0] = this.localCoordinates[i][0] + xAdjustment;
            globalCoordinates[i][1] = this.localCoordinates[i][1] + yAdjustment;
        }

        StringBuilder gridString = new StringBuilder();
        int i = 0;
        for (int y = gridSizeY; 0 <= y; y--) {
            for (int x = 0; x < gridSizeX; x++) {
                if (i < globalCoordinates.length && globalCoordinates[i][0] == x && globalCoordinates[i][1] == y) {
                    gridString.append(name);
                    i++;
                } else {
                    gridString.append('E');
                }
            }
            gridString.append(" ");
        }
        gridString.deleteCharAt(gridString.length() - 1);

        return gridString.toString();
    }

    public static List<Shape> getAllShapes() {
        List<Shape> result = new ArrayList<>();
        for (Character shapeLetter : allShapeLetters) {
            Shape shape = Shape.fromString(shapeLetter.toString());
            result.add(shape);
        }
        return result;
    }

    public static String getAllShapeVariantsString() {
        StringBuilder result = new StringBuilder();
        for (Shape shape : getAllShapes()) {
            for (Shape variant : shape.generateAllVariants()) {
                result.append(variant.toString()).append("\n");
            }
            result.append("\n\n\n");
        }
        return result.toString();
    }
}
