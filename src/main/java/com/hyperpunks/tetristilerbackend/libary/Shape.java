package com.hyperpunks.tetristilerbackend.libary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shape {
    private final int[][] localCoordinates;

    Shape(int[][] localCoordinates) {
        this.localCoordinates = Arrays.stream(localCoordinates).sorted((a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(a[0], b[0]);
            } else {
                return Integer.compare(a[1], b[1]);
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
            case 'F' -> new Shape(new int[][]{{0, 0}, {0, 1}, {1, 1}, {-1, 0}, {0, -1}});
            case 'I' -> new Shape(new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, -1}, {0, -2}});
            case 'L' -> new Shape(new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, -1}, {1, -1}});
            case 'N' -> new Shape(new int[][]{{0, 0}, {0, 1}, {0, 2}, {-1, 0}, {-1, -1}});
            case 'P' -> new Shape(new int[][]{{0, 0}, {0, 1}, {1, 1}, {1, 0}, {0, -1}});
            case 'T' -> new Shape(new int[][]{{0, 0}, {1, 0}, {-1, 0}, {0, -1}, {0, -2}});
            case 'U' -> new Shape(new int[][]{{0, 0}, {-1, 0}, {-1, 1}, {1, 0}, {1, 1}});
            case 'V' -> new Shape(new int[][]{{0, 0}, {-1, 0}, {-2, 0}, {0, 1}, {0, 2}});
            case 'W' -> new Shape(new int[][]{{0, 0}, {-1, 0}, {-1, 1}, {0, -1}, {1, -1}});
            case 'X' -> new Shape(new int[][]{{0, 0}, {-1, 0}, {0, 1}, {1, 0}, {0, -1}});
            case 'Y' -> new Shape(new int[][]{{0, 0}, {-1, 0}, {0, 1}, {0, -1}, {0, -2}});
            case 'Z' -> new Shape(new int[][]{{0, 0}, {0, 1}, {-1, 1}, {0, -1}, {1, -1}});
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
        return new Shape(clonedCoordinates);
    }

    private List<Shape> generateAllVariants() {
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
        int minY = Arrays.stream(coordinates).mapToInt(x -> x[1]).min().getAsInt();
        int maxY = Arrays.stream(coordinates).mapToInt(x -> x[1]).max().getAsInt();
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
