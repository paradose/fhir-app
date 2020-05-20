package com.teamohno;


import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // setup before getting input from user
        Model m = new Model();
        View v = new View(m);
        Controller c = new Controller(m, v);

        // Add listeners to view objects
        c.initController();
    }
}
