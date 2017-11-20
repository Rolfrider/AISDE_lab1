package com.company;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Network {
    private Map<Integer, Vertex> vertexes = new HashMap<>();
    private Map<Integer, Edge> edges = new HashMap<>();
    private String algorithm;
    private final String MST = "MST",
            FLOYD = "FLOYD",
            SCIEZKA ="SCIEZKA";

    public void readNetwork(String fileName) {
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int nV = 0,
                    iV = 0,
                    nE = 0,
                    iE = 0;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;

                if (line.contains("WEZLY")) {
                    nV = takeNumOf(line);
                    iV = 0;
                    continue;
                }

                if (nV > iV) {
                    Vertex v = updateVertex(line);
                    vertexes.put(v.id, v);
                    iV++;
                    continue;
                }

                if (line.contains("LACZA")) {
                    nE = takeNumOf(line);
                    iE = 0;
                    continue;
                }

                if (nE > iE) {
                    Edge e = updateEdge(line);
                    edges.put(e.id, e);
                    iE++;
                    continue;
                }

                if (line.contains("ALGORYTM")) {
                    String[] parts = line.split("= ");
                    algorithm = parts[1];
                }
            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }

    public void useAlgorithm(){
        switch (algorithm){
            case MST:
                break;
            case SCIEZKA:
                break;
            case FLOYD:
                break;
        }
    }

    public void showNet() {
        System.out.println("Vertexes: " + vertexes.size());
        System.out.println("Edges: " + edges.size());
        System.out.println(algorithm);
    }

    private int takeNumOf(String line) {
        String[] parts = line.split(" ");
        return Integer.parseInt(parts[2]);
    }

    private Vertex updateVertex(String line) {
        String[] parts = line.split(" ");
        return new Vertex(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]));
    }

    private Edge updateEdge(String line) {
        String[] parts = line.split(" ");
        int startId = Integer.parseInt(parts[0]),
                endId = Integer.parseInt(parts[1]);
        return new Edge(startId, endId,
                Integer.parseInt(parts[2]), calcValue(startId, endId));

    }

    private int calcValue(int startId, int endId) {
        double dx = vertexes.get(endId).x - vertexes.get(startId).x;
        double dy = vertexes.get(endId).y - vertexes.get(startId).y;
        Double v = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return v.intValue();
    }
}