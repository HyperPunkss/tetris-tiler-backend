package com.hyperpunks.tetristilerbackend.library;

import java.util.*;

public class IDManager {
    private Queue<Integer> availableIDs = new ArrayDeque<>();
    private Map<Integer, Integer> idPopulations = new TreeMap<>();

    public IDManager() {
    }

    public int generateID() {
        int id;
        if (availableIDs.isEmpty()) {
            id = idPopulations.size()+1;
        } else {
            id = availableIDs.remove();
        }
        idPopulations.put(id, 1);
        return id;
    }

    public int incrementPopulation(int id) {
        int population = idPopulations.get(id);
        population += 1;
        idPopulations.put(id, population);
        return population;
    }

    public int decrementPopulation(int id) {
        int population = idPopulations.get(id);
        population -= 1;
        if (population == 0) {
            availableIDs.add(id);
            idPopulations.remove(id);
        } else {
            idPopulations.put(id, population);
        }
        return population;
    }

    public int getPopulation(int id) {
        return idPopulations.get(id);
    }

    public List<Integer> getAllPopulationSizes() {
        return new ArrayList<>(idPopulations.values());
    }

    public int getSmallestGroupID() {
        int idOfMin = idPopulations.keySet().iterator().next();
        int smallestPopulation = idPopulations.get(idOfMin);
        for (int id : idPopulations.keySet()) {
            int population = idPopulations.get(id);
            if (population < smallestPopulation) {
                smallestPopulation = population;
                idOfMin = id;
            }
        }
        return idOfMin;
    }
}
