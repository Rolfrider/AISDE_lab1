package com.company;

public class Edge {


    int id, startVertexId, endVertexId, value;


    public Edge(int id, int startVertexId, int endVertexId, int value){
        this.id = id;
        this.startVertexId = startVertexId;
        this.endVertexId = endVertexId;
        this.value = value;
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
