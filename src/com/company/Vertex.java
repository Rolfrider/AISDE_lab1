package com.company;

public class Vertex {


    int id, x, y;


    public Vertex(){

    }

    public Vertex(int id, int x , int y){

        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId(){
        return id;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

}
