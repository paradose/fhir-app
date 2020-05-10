package com.teamohno;

public class Controller {
    private Model myModel;
    private View myView;

    public Controller(Model newModel, View newView){
        myModel = newModel;
        myView = newView;
    }

    public void initView(){
        //initialise view - set default text from model
        // e.g. view.getField().setText(model.getValue...());
    }

    public void initController(){
        //initialise controller - add listeners to UI elements
        // e.g. view.getUIelement().addActionListener(e -> saveValue());

    }

    private void saveValue(){
        // store value into model
        // e.g. model.setName(view.getNameField().getText());
    }


}
