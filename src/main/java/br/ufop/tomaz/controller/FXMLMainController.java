package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.util.Screen;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLMainController implements Initializable{


    @FXML private MenuBar menuBar;
    @FXML private Button btnTimes;
    @FXML private Button btnProfessors;
    @FXML private Button btnClasses;
    @FXML private Button btnEvents;
    @FXML private Button btnConstraints;
    @FXML private Button btnTimeTabling;

    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void openTimesScreen() throws IOException {
        App.setScreen(Screen.TIMES);
    }

    @FXML
    private void openManagerClassesScreen() throws IOException{
        App.setScreen(Screen.MANAGER_CLASSES);
    }

    @FXML
    private void openManagerProfessorsScreen() throws IOException{
        App.setScreen(Screen.MANAGER_PROFESSORS);
    }

    @FXML
    private void openManagerConstraintsScreen() throws IOException{
        App.setScreen(Screen.MANAGER_CONSTRAINTS);
    }

    @FXML
    private void openManagerEventsScreen() throws IOException{
        App.setScreen(Screen.MANAGER_EVENTS);
    }

    @FXML
    private void openTimetablingScreen() throws IOException {
        App.setScreen(Screen.TIME_TABLING);
    }

}
