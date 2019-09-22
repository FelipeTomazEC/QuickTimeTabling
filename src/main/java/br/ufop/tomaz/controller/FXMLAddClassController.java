package br.ufop.tomaz.controller;

import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.controller.interfaces.EditScreen;
import br.ufop.tomaz.dao.ClassDAOImpl;
import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.Editable;
import br.ufop.tomaz.model.Unavailability;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.Day;
import br.ufop.tomaz.util.Screen;
import br.ufop.tomaz.util.TableTimesUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAddClassController implements Initializable, AppScreen, EditScreen {

    @FXML
    private MenuBar menubar;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField edtClassName;
    @FXML
    private TableView<Day> timesTableView;
    private ClassE editClass = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Init table times
        TableTimesUtils timesUtils = new TableTimesUtils(TableTimesUtils.CLASSES_TABLE_TYPE);
        timesUtils.prepareTableTimes(timesTableView);
        timesTableView.setItems(FXCollections.observableList(AppSettings.getInstance().defaultDays()));

        //Init buttons behaviors
        btnAdd.disableProperty().bind(edtClassName.textProperty().isEmpty());
    }

    @FXML
    public void clearFields() {
        edtClassName.setText("");
        timesTableView.setItems(FXCollections.observableList(AppSettings.getInstance().defaultDays()));
    }

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.MANAGER_CLASSES);
    }

    @FXML
    private void addClassToDb() {
        List<Unavailability> unavailabilities = TableTimesUtils.getUnavailabilities(timesTableView);
        ClassE classE = new ClassE();
        classE.setName(edtClassName.getText());
        classE.setUnavailabilities(unavailabilities);
        ClassDAOImpl.getInstance().persistClass(classE);
        //TODO -- Alert with message informing if succeed or failure
        clearFields();
    }

    @Override
    public void loadEditable(Editable editable) {
        this.editClass = (ClassE) editable;
        edtClassName.textProperty().bindBidirectional(editClass.nameProperty());
        TableTimesUtils.loadUnavailabilities(timesTableView, editClass.getUnavailabilities());
        configureConfirmEditionButton();
    }

    @Override
    public void configureConfirmEditionButton() {
        btnAdd.setText("Confirm");
        btnAdd.setOnAction(event -> {
            editClass.setUnavailabilities(TableTimesUtils.getUnavailabilities(timesTableView));
            ClassDAOImpl.getInstance().updateClass(editClass);
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
