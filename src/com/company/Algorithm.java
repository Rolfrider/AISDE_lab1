package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Algorithm {

    //KRUSKAL  MST

    static class Subset{
        int parent, rank;
    }

    // A utility function to find set of an element i
    // (uses path compression technique)
    private static int find(Subset subsets[], int i)
    {
        // find root and make root as parent of i (path compression)
        if (subsets[i-1].parent != i)
            subsets[i-1].parent = find(subsets, subsets[i-1].parent);

        return subsets[i-1].parent;
    }

    // A function that does union of two sets of x and y
    // (uses union by rank)
    private static void Union(Subset subsets[], int x, int y)
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
        else {
            subsets[yroot-1].parent = xroot;
            subsets[xroot-1].rank++;
        }
    }

    static Path KruskalMST(Vertex vertices[], Edge edges[]) {
        int V = vertices.length;
        Edge result[] = new Edge[V];
        int e = 0, i;
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
            Edge nextEdge = edges[i++];

            int x = find(subsets, nextEdge.getStartVertex());
            int y = find(subsets, nextEdge.getEndVertex());

            // If including this edge does't cause cycle,
            // include it in result and increment the index
            // of result for next edge
            if (x != y) {
                result[e++] = nextEdge;
                Union(subsets, x, y);
            }
        }

        Path mstPath = new Path();
        for (i = 0; i < result.length; i++) {
            if (result[i].getId() != 0) {
                mstPath.edgesId.add(result[i].getId());
            }
        }
        for (i = 0; i < vertices.length; i++) {

            mstPath.vertexesId.add(vertices[i].getId());
        }

        return mstPath;
    }

    // Dikstra Sciezka i Steiner

    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in shortest path tree
    private static int minDistance(int dist[], Boolean sptSet[], int V)
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index=-1;

        for (int v = 0; v < V; v++)
            if (!sptSet[v] && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }
        return min_index;
    }

    static Path Dijkstra(int startV, int endV, Vertex vertices[], Edge edges[], ArrayList<Integer> optional){
        int V = vertices.length;
        int edgeId ;
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
            int u = minDistance(dist, sptSet, V);

            // Mark the picked vertex as processed
            sptSet[u] = true;


            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 0; v < V; v++) {

                edgeId = getEdge(u + 1, v + 1, edges);

                // Update dist[v] only if is not in sptSet, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!sptSet[v] && edgeId != 0 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u] + edges[edgeId -1].getValue() < dist[v])
                    dist[v] = dist[u] + edges[edgeId -1].getValue();
            }
        }

        Path path = null;
        if (optional != null) {
            // Getting Stainer tree
            for (Vertex sV : vertices) {
                endV = sV.getId();
                if (endV == startV || optional.contains(endV))
                    continue;
                Path shortestPath = new Path();
                int target = endV;
                int id = 0, ver = 0;
                shortestPath.vertexesId.add(target);
                while (target != startV) {
                    for (int v = 0; v < V; v++) {

                        edgeId = getEdge(v + 1, target, edges);
                        if (sptSet[v] && edgeId != 0 && dist[target - 1] > dist[v]) {
                            if (ver == 0 || dist[v] < dist[ver - 1]) {
                                ver = v + 1;
                                id = edgeId;
                            }
                        }
                    }
                    shortestPath.edgesId.add(id);
                    target = ver;
                    shortestPath.vertexesId.add(target);
                }
                if (path == null) {
                    path = shortestPath;
                } else {
                    for (int idS : shortestPath.edgesId) {
                        if (!path.edgesId.contains(idS))
                            path.edgesId.add(idS);
                    }
                    for (int idS : shortestPath.vertexesId) {
                        if (!path.vertexesId.contains(idS))
                            path.vertexesId.add(idS);
                    }
                }
            }
        }
        else {
            // Getting shortest path
            Path shortestPath = new Path();
            int target = endV;
            int id =0, ver = 0;
            shortestPath.vertexesId.add(target);
            while (target != startV) {
                for (int v = 0; v < V; v++) {

                    edgeId = getEdge(v + 1, target, edges);
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
            path  = shortestPath;
        }



        return path;
    }



    //Floyd FLOYD

    static int[][] Floyd(int graph[][], int V, ArrayList<Network.Pair> floydPts)
    {
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
        // Print wanted paths
        for (int l = 0; l < floydPts.size(); l++) {
            int left = floydPts.get(l).getLeft();
            int right = floydPts.get(l).getRight();
            System.out.println("Shortest path from vertex " + left +
                    " to " + right + " has a value of " + dist[left-1][right-1]);
        }

        return graph;
    }

    public static void printMatrix(int dist[][])
    {
        for (int i=0; i<dist.length; ++i) {
            for (int j=0; j<dist[0].length; ++j) {
                if (dist[i][j]>=Integer.MAX_VALUE/2)
                    System.out.print("INF ");
                else
                    System.out.print(dist[i][j]+"   ");
            }
            System.out.println();
        }
    }

    public static int getEdge(int Id, int Id2, Edge edges[]){
        for(Edge edge : edges){
            if(edge.getStartVertex() == Id &&
                    edge.getEndVertex() == Id2
                    || (edge.getStartVertex() == Id2 &&
                    edge.getEndVertex() == Id))
                return edge.getId();
        }
        return 0;
    }
}