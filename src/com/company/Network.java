package com.company;
import com.sun.deploy.util.ArrayUtil;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class Network {
    Vertex []vertexes;
    Edge []edges;
    String algorithm;
    private final String MST = "MST",
            FLOYD = "FLOYD",
            SCIEZKA ="SCIEZKA";

    protected String styleSheet =
            "node {" +
                    "	fill-color: black;" +
                    "}" +
                    "node.marked {" +
                    "	fill-color: red;" +
                    "}";

    class Subset{
        int parent, rank;
    };

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
                    vertexes[iV] = updateVertex(line);
                    iV++;
                    continue;
                }

                if(line.contains("LACZA")){
                    edges = new Edge[takeNumOf(line)];
                    nE = edges.length;
                    iE =0;
                    continue;
                }

                if( nE > iE){
                    edges[iE] = updateEdge(line);
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

    public void useAlgorithm(){
        switch (algorithm){
            case MST:
                KruskalMST();
                break;
            case SCIEZKA:
                break;
            case FLOYD:
                break;
        }
    }

    // A utility function to find set of an element i
    // (uses path compression technique)
    int find(Subset subsets[], int i)
    {
        // find root and make root as parent of i (path compression)
        if (subsets[i-1].parent != i)
            subsets[i-1].parent = find(subsets, subsets[i-1].parent);

        return subsets[i-1].parent;
    }

    // A function that does union of two sets of x and y
    // (uses union by rank)
    void Union(Subset subsets[], int x, int y)
    {
        int xroot = find(subsets, x);
        int yroot = find(subsets, y);

        // Attach smaller rank tree under root of high rank tree
        // (Union by Rank)
        if (subsets[xroot-1].rank < subsets[yroot-1].rank)
            subsets[xroot-1].parent = yroot;
        else if (subsets[xroot-1].rank > subsets[yroot-1].rank)
            subsets[yroot-1].parent = xroot;

            // If ranks are same, then make one as root and increment
            // its rank by one
        else
        {
            subsets[yroot-1].parent = xroot;
            subsets[xroot-1].rank++;
        }
    }

    void KruskalMST(){
        int V = vertexes.length;
        Edge result[] = new Edge[V];
        int e = 0, i = 0;
        for (i = 0; i< V; i++){
            result[i] = new Edge();
        }

        // Step 1:  Sort all the edges in non-decreasing order of their
        // weight.
        Arrays.sort(edges);

        Subset subsets[] = new Subset[V];
        for (i=0; i<V; i++){
            subsets[i]= new Subset();
        }

        for(int v = 0 ; v < V; v++){
            subsets[v].parent = v +1;
            subsets[v].rank = 0;
        }

        i =0;

        while (e< V-1){

            // Step 2: Pick the smallest edge. And increment
            // the index for next iteration
            Edge nextEdge = new Edge();
            nextEdge = edges[i++];

            int x = find(subsets, nextEdge.startVertexId);
            int y = find(subsets, nextEdge.endVertexId);

            // If including this edge does't cause cycle,
            // include it in result and increment the index
            // of result for next edge
            if(x != y){
                result[e++] = nextEdge;
                Union(subsets, x, y);
            }

        }

        if(result[result.length-1].id == 0){
            Edge []tmp = new Edge[result.length -1];
            for( i = 0; i< result.length-1; i++ ){
                tmp[i] = result[i];
            }
            edges = tmp;
        }
        else
            edges = result;

//        for (i = 0; i < e; ++i)
//            System.out.println(result[i].startVertexId+" -- " +
//                    result[i].endVertexId+" == " + result[i].value);
    }


    public void showNet(){
        System.out.println(vertexes.length);
        System.out.println(edges.length);
        System.out.println(algorithm);
        for (int i = 0; i < edges.length; ++i) {
            System.out.println(edges[i].startVertexId + " -- " +
                    edges[i].endVertexId + " == " + edges[i].value);
        }
        Graph graph = new SingleGraph("Net") ;
        graph.addAttribute("ui.stylesheet", styleSheet);
        int i = 0;
        for (i = 0 ; i < vertexes.length ; i++){
            Node n = graph.addNode(vertexes[i].id + "");
            n.addAttribute("ui.label", "" + vertexes[i].id);
            n.addAttribute("ui.class", "marked");
        }
        for (i = 0; i < edges.length; i++){
            org.graphstream.graph.Edge e = graph.addEdge(edges[i].id +"", edges[i].startVertexId +"", edges[i].endVertexId +"");
            e.addAttribute("ui.label",edges[i].id +"" );
        }
        graph.display();
    }



    private int takeNumOf(String line){
        String[] parts =line.split(" ");
        int i = Integer.parseInt(parts[2]);
        return i;
    }

    private Vertex updateVertex(String line){
        String[] parts =line.split(" ");
        return new Vertex(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]));

    }

    private Edge updateEdge(String line){
        String[] parts =line.split(" ");
        int startId =Integer.parseInt(parts[1]),
                endId =Integer.parseInt(parts[2]);
        return new Edge(Integer.parseInt(parts[0]), startId,
                endId, calcValue(startId, endId) );
    }

    private int calcValue(int startId, int endId){
        double dx = vertexes[startId - 1].x - vertexes[endId - 1].x;
        double dy = vertexes[startId-1].y - vertexes[endId-1].y;
        Double v = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return v.intValue();
    }

}
