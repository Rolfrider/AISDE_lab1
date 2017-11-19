package com.company;

public class Edge {


    int id, startVertexId, endVertexId;


    public Edge(int id, int startVertexId, int endVertexId){
        this.id = id;
        this.startVertexId = startVertexId;
        this.endVertexId = endVertexId;
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
