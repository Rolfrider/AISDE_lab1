package com.company;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Network {
    Vertex []vertexes;
    Edge []edges;
    String algorithm;

    public void readNetwork( String fileName){
        String line = null;

        try{
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int nV = 0 , iV = 0, nE = 0, iE = 0;
            while((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("#"))
                    continue;

                if(line.contains("WEZLY")){
                    vertexes = new Vertex[takeNumOf(line)];
                    nV = vertexes.length;
                    iV =0;
                    continue;
                }

                if( nV > iV){
                    updateVertex(line,vertexes[iV]);
                    iV++;
                    continue;
                }

                if(line.contains("LACZA")){
                    edges = new Edge[takeNumOf(line)];
                    nE = edges.length;
                    iE =0;
                    continue;
                }

                if( nV > iV){
                    updateEdge(line,edges[iE]);
                    iE++;
                    continue;
                }

                if(line.contains("ALGORYTM")){
                    String[] parts = line.split("= ");
                    algorithm = parts[1];
                }

            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

    }

    public void showNet(){
        System.out.println(vertexes.length);
        System.out.println(edges.length);
        System.out.println(algorithm);
    }



    private int takeNumOf(String line){
        String[] parts =line.split(" ");
        int i = Integer.parseInt(parts[2]);
        return i;
    }

    private void updateVertex(String line,Vertex v){
        String[] parts =line.split(" ");
        v = new Vertex(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]));

    }

    private void updateEdge(String line, Edge e){
        String[] parts =line.split(" ");
        e = new Edge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]));
    }
}
