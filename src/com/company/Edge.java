package com.company;

public class Edge implements Comparable<Edge> {


    int id, startVertexId, endVertexId, value;

    Edge(){}


    public Edge(int id, int startVertexId, int endVertexId, int value){
        this.id = id;
        this.startVertexId = startVertexId;
        this.endVertexId = endVertexId;
        this.value = value;
    }

    public int compareTo(Edge e){
        return this.value - e.value;
    }

    public int getId(){
        return id;
    }

    public int getStartVertex(){
        return startVertexId;
    }

    public int getEndVertex() {
        return endVertexId;
    }
}
