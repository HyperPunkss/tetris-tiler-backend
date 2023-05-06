package com.hyperpunks.tetristilerbackend.library;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class EmptyGrouper {
    private final int[][] mappings; // y, x
    private final IDManager idManager = new IDManager();
    private final Queue<int[]> coordinatesToCheck = new ArrayDeque<>(); // x, y

    EmptyGrouper(int gridSizeX, int gridSizeY) {
        mappings = new int[gridSizeY][gridSizeX];
        int id = idManager.generateID();
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                mappings[y][x] = 1;
                idManager.incrementPopulation(id);
            }
        }
        idManager.decrementPopulation(id);
    }

    EmptyGrouper(String[][] grid) {
        mappings = new int[grid.length][grid[0].length];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x].equals("E")) {
                    mappings[y][x] = idManager.generateID();
                    coordinatesToCheck.add(new int[]{x, y});
                } else {
                    mappings[y][x] = 0;
                }
            }
        }
        checkGroups();
    }

    private void checkGroups() {
        while (!coordinatesToCheck.isEmpty()) {
            int[] coordinate = coordinatesToCheck.remove();
            int x = coordinate[0];
            int y = coordinate[1];
            if (mappings[y][x] == 0) {
                continue;
            }
            boolean updatedAnything;
            do {
                updatedAnything = false;
                for (int[] neighbour : neighbouringCoordinates(x, y)) {
                    int neighbourID = mappings[neighbour[1]][neighbour[0]];
                    if (neighbourID == 0) {
                        continue;
                    }
                    if (neighbourID != mappings[y][x]) {
                        int maxID = Integer.max(neighbourID, mappings[y][x]);
                        int minID = Integer.min(neighbourID, mappings[y][x]);
                        idManager.decrementPopulation(maxID);
                        idManager.incrementPopulation(minID);
                        mappings[y][x] = minID;
                        mappings[neighbour[1]][neighbour[0]] = minID;
                        coordinatesToCheck.add(neighbour);
                        updatedAnything = true;
                    }
                }
            } while (updatedAnything);
        }
    }

    public void fillTiles(List<int[]> tiles) {
        for (int[] tile : tiles) {
            if (mappings[tile[1]][tile[0]] != 0) {
                int old_id = mappings[tile[1]][tile[0]];
                idManager.decrementPopulation(old_id);
            }
            mappings[tile[1]][tile[0]] = 0;
            for (int[] neighbour : neighbouringCoordinates(tile[0], tile[1])) {
                if (mappings[neighbour[1]][neighbour[0]] != 0) {
                    int old_id = mappings[neighbour[1]][neighbour[0]];
                    idManager.decrementPopulation(old_id);
                    mappings[neighbour[1]][neighbour[0]] = idManager.generateID();
                    coordinatesToCheck.add(neighbour);
                }
            }
        }
        checkGroups();
    }

    public void emptyTiles(List<int[]> tiles) {
        for (int[] tile : tiles) {
            if (mappings[tile[1]][tile[0]] != 0) {
                int old_id = mappings[tile[1]][tile[0]];
                idManager.decrementPopulation(old_id);
            }
            mappings[tile[1]][tile[0]] = idManager.generateID();
            coordinatesToCheck.add(tile);
        }
        checkGroups();
    }

    public List<Integer> getAllGroupSizes() {
        return idManager.getAllPopulationSizes();
    }

    public List<int[]> getSmallestGroupCoordinates() {
        int smallestGroupID = idManager.getSmallestGroupID();
        List<int[]> coordinates = new ArrayList<>();
        for (int y = 0; y < mappings.length; y++) {
            for (int x = 0; x < mappings[0].length; x++) {
                if (mappings[y][x] == smallestGroupID) {
                    coordinates.add(new int[]{x, y});
                }
            }
        }
        return coordinates;
    }

    private List<int[]> neighbouringCoordinates(int positionX, int positionY) {
        List<int[]> neighbours = new ArrayList<>(4);
        if (positionX > 0) {
            neighbours.add(new int[]{positionX - 1, positionY});
        }
        if (positionX < mappings[0].length - 1) {
            neighbours.add(new int[]{positionX + 1, positionY});
        }
        if (positionY > 0) {
            neighbours.add(new int[]{positionX, positionY - 1});
        }
        if (positionY < mappings.length - 1) {
            neighbours.add(new int[]{positionX, positionY + 1});
        }
        return neighbours;
    }
}
