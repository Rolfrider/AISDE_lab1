package com.company;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;


public class Network {
    Vertex [] vertices;
    Edge []edges;
    String algorithm;
    Path shortestPath = null;
    int startPath , endPath;
    Path mstPath = null;

    final static int INF = 99999;
    private final String MST = "MST",
            FLOYD = "FLOYD",
            SCIEZKA ="SCIEZKA";

    protected String styleSheet =
            "node {" +
                    "	fill-color: black;" +
                    "   size: 15px;" +
                    "   text-size: 20 ;"+
                    "}" +
                    "node.marked {" +
                    "	fill-color: blue;" +
                    "   text-color: blue ;"+
                    "}"+
             "edge {" +
                    "  fill-color: black;" +
                    "text-size: 20 ;"+
                    "}" +
                    "edge.marked {" +
                    "  fill-color: blue;" +
                    "   text-color: blue ;"+
                    "   size: 3px;"+
                    "}";

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
                    vertices = new Vertex[takeNumOf(line)];
                    nV = vertices.length;
                    iV =0;
                    continue;
                }

                if( nV > iV){
                    vertices[iV] = updateVertex(line);
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
                    continue;
                }

                if(algorithm.equals(SCIEZKA)){
                    String[] parts = line.split(" ");
                    startPath = Integer.parseInt(parts[0]);
                    endPath = Integer.parseInt(parts[1]);
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
                Dijkstra(startPath, endPath);
                break;
            case FLOYD:
                Floyd(toMatrix());
                break;
        }
    }

    //KRUSKAL  MST

    class Subset{
        int parent, rank;
    };
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

    void KruskalMST() {
        int V = vertices.length;
        Edge result[] = new Edge[V];
        int e = 0, i = 0;
        for (i = 0; i < V; i++) {
            result[i] = new Edge();
        }

        // Step 1:  Sort all the edges in non-decreasing order of their
        // weight.
        Arrays.sort(edges);

        Subset subsets[] = new Subset[V];
        for (i = 0; i < V; i++) {
            subsets[i] = new Subset();
        }

        for (int v = 0; v < V; v++) {
            subsets[v].parent = v + 1;
            subsets[v].rank = 0;
        }

        i = 0;

        while (e < V - 1) {

            // Step 2: Pick the smallest edge. And increment
            // the index for next iteration
            Edge nextEdge = new Edge();
            nextEdge = edges[i++];

            int x = find(subsets, nextEdge.startVertexId);
            int y = find(subsets, nextEdge.endVertexId);

            // If including this edge does't cause cycle,
            // include it in result and increment the index
            // of result for next edge
            if (x != y) {
                result[e++] = nextEdge;
                Union(subsets, x, y);
            }
        }

        mstPath = new Path();
        for (i = 0; i < result.length; i++) {
            if (result[i].id != 0) {
                mstPath.edgesId.add(result[i].id);
            }
        }
        for (i = 0; i < vertices.length; i++) {

            mstPath.vertexesId.add(vertices[i].id);
        }
    }

    // Dikstra Sciezka

    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in shortest path tree
    int minDistance(int dist[], Boolean sptSet[])
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index=-1;

        for (int v = 0; v < vertices.length; v++)
            if (sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }
        return min_index;
    }

    void Dijkstra(int startV, int endV){
        int V = vertices.length;
        int edgeId = 0;
        // The output array. dist[i] will hold the shortest distance from src to i
        int dist[] = new int[V];

        // sptSet[i] will true if vertex i is included in shortest
        // path tree or shortest distance from src to i is finalized
        Boolean sptSet[] = new Boolean[V];

        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 0; i < V; i++)
        {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        dist[startV - 1] = 0;

        // Find shortest path for all vertices
        for (int count = 0; count < V-1; count++)
        {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;


            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 0; v < V; v++) {

                edgeId = getEdge(u + 1, v + 1);

                // Update dist[v] only if is not in sptSet, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!sptSet[v] && edgeId != 0 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u] + edges[edgeId -1].value < dist[v])
                    dist[v] = dist[u] + edges[edgeId -1].value;
            }
        }

        //Getting the path
        shortestPath = new Path();
        int target = endV;
        int id =0, ver = 0;
        shortestPath.vertexesId.add(target);
        while (target != startV) {
            for (int v = 0; v < V; v++) {

                edgeId = getEdge(v + 1, target);
                if (sptSet[v] && edgeId != 0 && dist[target -1] >  dist[v] ) {
                    if(ver == 0 || dist[v] < dist[ver-1]) {
                        ver = v + 1;
                        id = edgeId;
                    }
                }
            }
            shortestPath.edgesId.add(id);
            target = ver;
            shortestPath.vertexesId.add(target);
        }
    }

    //Floyd FLOYD

    void Floyd(int graph[][])
    {
        int V = vertices.length;
        int dist[][] = new int[V][V];
        int i, j, k;

        //Initialize the solution matrix same as input graph matrix.
        //Or we can say the initial values of shortest distances
        //are based on shortest paths considering no intermediate
        //vertex.

        for (i = 0; i < V; i++)
            for (j = 0; j < V; j++)
                dist[i][j] = graph[i][j];

        /* Add all vertices one by one to the set of intermediate
           vertices.
          ---> Before start of a iteration, we have shortest
               distances between all pairs of vertices such that
               the shortest distances consider only the vertices in
               set {0, 1, 2, .. k-1} as intermediate vertices.
          ----> After the end of a iteration, vertex no. k is added
                to the set of intermediate vertices and the set
                becomes {0, 1, 2, .. k} */

        for (k = 0; k < V; k++)
        {
            // Pick all vertices as source one by one
            for (i = 0; i < V; i++)
            {
                // Pick all vertices as destination for the
                // above picked source
                for (j = 0; j < V; j++)
                {
                    // If vertex k is on the shortest path from
                    // i to j, then update the value of dist[i][j]
                    if (dist[i][k] + dist[k][j] < dist[i][j])
                        dist[i][j] = dist[i][k] + dist[k][j];
                }
            }
        }
        // Print the shortest distance matrix
        System.out.println("Following matrix shows the shortest "+
              "distances between every pair of vertices");
        printMatrix(dist);
    }

    void printMatrix(int dist[][])
    {
        int V = vertices.length;

        for (int i=0; i<V; ++i) {
            for (int j=0; j<V; ++j) {
                if (dist[i][j]==INF)
                    System.out.print("INF ");
                else
                    System.out.print(dist[i][j]+"   ");
            }
            System.out.println();
        }
    }

    public void showNet(){
        System.out.println(vertices.length);
        System.out.println(edges.length);
        System.out.println(algorithm);
        for (int i = 0; i < edges.length; ++i) {
            System.out.println(edges[i].startVertexId + " -- " +
                    edges[i].endVertexId + " == " + edges[i].value);
        }

        Graph graph = new SingleGraph("Net") ;
        graph.addAttribute("ui.stylesheet", styleSheet);

        int i = 0;
        for (i = 0 ; i < vertices.length ; i++){
            Node n = graph.addNode(vertices[i].id + "");
            n.addAttribute("ui.label", "" + vertices[i].id);
            n.addAttribute("x", vertices[i].x);
            n.addAttribute("y", vertices[i].y);
        }
        for (i = 0; i < edges.length; i++){
            org.graphstream.graph.Edge e = graph.addEdge(edges[i].id +"", edges[i].startVertexId +"", edges[i].endVertexId +"");
            e.addAttribute("ui.label",edges[i].id +"" );
        }
        graph.display();

        Path path = null;
        if (shortestPath != null)
            path = shortestPath;
        else if(mstPath != null)
            path = mstPath;

        if(path != null){
            for (Integer id: path.vertexesId){
                Node n = graph.getNode(id + "");
                n.addAttribute("ui.class", "marked");
            }
            for (Integer id: path.edgesId){
                org.graphstream.graph.Edge e = graph.getEdge(id +"");
                e.addAttribute("ui.class", "marked");
            }
           // graph.display();
        }
    }

    private int[][] toMatrix(){
        int size = vertices.length;
        int[][] graph = new int [size][size];

        for (int i = 0; i < size; i++ ) {
            for (int n = 0; n < size; n++ ){
                graph[i][n] = INF;
            }
            graph[i][i] = 0;
        }

        for (Edge edge : edges){
            int from = edge.getStartVertex() - 1; // -1 because Id starts at 1 and arrays start from 0
            int to  = edge.getEndVertex() - 1;
            int val = edge.getValue();
            graph[from][to] = val;
            graph[to][from] = val;
        }

        System.out.println("Following matrix shows the values of existing edges");
        printMatrix(graph);
        return graph;
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
        double dx = vertices[startId - 1].x - vertices[endId - 1].x;
        double dy = vertices[startId-1].y - vertices[endId-1].y;
        Double v = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return v.intValue();
    }

    private int getEdge(int Id, int Id2){
        for (int i = 0; i < edges.length; i++){
            if(edges[i].startVertexId == Id &&
                    edges[i].endVertexId == Id2
                    || (edges[i].startVertexId == Id2 &&
                    edges[i].endVertexId == Id))
                return edges[i].id;
        }
        return 0;
    }
}