package com.teamohno;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    // Instance variables
    private List<Observer> observers = new ArrayList<Observer>();

    public void attach(Observer observer){
        observers.add(observer);
    }

    public void detach(Observer observer){
        observers.remove(observer);
    }

    // Notifies all observers attached
    public void notifyObservers(){
        for (Observer observer:observers){
            observer.update();
        }
    }

}
