package com.hyperpunks.tetristilerbackend.library;

import java.util.*;

public class EmptyGrouper {
    private final int[][] mappings; // y, x
    private final IDManager idManager = new IDManager();
    private final Queue<int[]> toChange = new ArrayDeque<>(); // x, y

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
                    emptyTile(x, y);
                } else {
                    mappings[y][x] = 0;
                    fillTile(x, y);
                }
            }
        }
    }

    private void refreshIDs(int startX, int startY) {
        int idToSet = idManager.generateID();
        refreshIDs(startX, startY, idToSet);
        idManager.decrementPopulation(idToSet); // Remove extra one from side effect
    }

    private void refreshIDs(int startX, int startY, int idToSet) {
        toChange.add(new int[]{startX, startY});
        while (!toChange.isEmpty()) {
            int[] coordinate = toChange.remove();
            int x = coordinate[0];
            int y = coordinate[1];
            if (mappings[y][x] != 0) {
                idManager.decrementPopulation(mappings[y][x]);
            }
            mappings[y][x] = idToSet;
            idManager.incrementPopulation(idToSet);
            for (int[] neighbour : neighbouringCoordinates(x, y)) {
                int neighbourID = mappings[neighbour[1]][neighbour[0]];
                if (neighbourID != idToSet && neighbourID != 0) {
                    toChange.add(neighbour);
                }
            }
        }
    }

    private boolean inBoundsX(int x) {
        return x >= 0 && x < mappings[0].length;
    }

    private boolean inBoundsY(int y) {
        return y >= 0 && y < mappings.length;
    }

    private int borderfulMapping(int x, int y) {
        if (!inBoundsX(x) || !inBoundsY(y)) {
            return 0;
        }
        return mappings[y][x];
    }

    public void fillTile(int x, int y) {
        if (mappings[y][x] == 0) {
            return;
        }
        idManager.decrementPopulation(mappings[y][x]);
        mappings[y][x] = 0;
        int[][] neighbours8Ways = new int[][]{{x - 1, y}, {x - 1, y + 1}, {x, y + 1}, {x + 1, y + 1}, {x + 1, y}, {x + 1, y - 1}, {x, y - 1}, {x - 1, y - 1}};
        int lastID = borderfulMapping(x - 1, y);
        int transitions = 0;
        for (int[] neighbour : neighbours8Ways) {
            int id = borderfulMapping(neighbour[0], neighbour[1]);
            if (id != lastID) {
                transitions++;
                lastID = id;
            }
        }
        if (transitions > 2) {
            for (int[] neighbour : neighbours8Ways) {
                if (borderfulMapping(neighbour[0], neighbour[1]) != 0) {
                    refreshIDs(neighbour[0], neighbour[1]);
                }
            }
        }
    }

    public void fillTiles(List<int[]> tiles) {
        for (int[] tile : tiles) {
            fillTile(tile[0], tile[1]);
        }
    }

    public void emptyTile(int x, int y) {
        int largestGroupID = 0;
        int groupSizeNorth = 0;
        if (borderfulMapping(x, y + 1) != 0) {
            groupSizeNorth = idManager.getPopulation(borderfulMapping(x, y + 1));
        }
        int groupSizeSouth = 0;
        if (borderfulMapping(x, y - 1) != 0) {
            groupSizeSouth = idManager.getPopulation(borderfulMapping(x, y - 1));
        }
        int groupSizeEast = 0;
        if (borderfulMapping(x + 1, y) != 0) {
            groupSizeEast = idManager.getPopulation(borderfulMapping(x + 1, y));
        }
        int groupSizeWest = 0;
        if (borderfulMapping(x - 1, y) != 0) {
            groupSizeWest = idManager.getPopulation(borderfulMapping(x - 1, y));
        }
        // Yes, I'm tired and sleepy, how did you know?
        if (groupSizeNorth >= groupSizeSouth && groupSizeNorth >= groupSizeEast && groupSizeNorth >= groupSizeWest) {
            largestGroupID = borderfulMapping(x, y + 1);
        } else if (groupSizeSouth >= groupSizeNorth && groupSizeSouth >= groupSizeEast && groupSizeSouth >= groupSizeWest) {
            largestGroupID = borderfulMapping(x, y - 1);
        } else if (groupSizeEast >= groupSizeNorth && groupSizeEast >= groupSizeSouth && groupSizeEast >= groupSizeWest) {
            largestGroupID = borderfulMapping(x + 1, y);
        } else {
            largestGroupID = borderfulMapping(x - 1, y);
        }
        if (largestGroupID != 0) {
            refreshIDs(x, y, largestGroupID);
        } else {
            refreshIDs(x, y);
        }
    }

    public void emptyTiles(List<int[]> tiles) {
        for (int[] tile : tiles) {
            emptyTile(tile[0], tile[1]);
        }
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
