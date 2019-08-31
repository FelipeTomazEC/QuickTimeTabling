package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.Screen;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLTimesController implements Initializable, AppScreen {


    @FXML
    private MenuBar menuBar;

    @FXML
    private ComboBox<Integer> cmbDaysPerWeek;

    @FXML
    private ComboBox<Integer> cmbTimesPerDay;

    @FXML
    private GridPane gridDays;

    @FXML
    private TabPane tabPaneDaysAndTimes;

    @FXML
    private GridPane gridTimes;

    @FXML
    private Button btnConfirm;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnCancel;

    private List<TextField> edtDaysList;

    private List<TextField> edtTimesList;


    public void initialize(URL location, ResourceBundle resources) {
        this.initTimesAndDaysList();
        this.initComboBoxes();
    }

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.HOME);
    }

    private void clearAndEnableCells(List<TextField> edtList, int lastEnableIndex, String defaultLabel) {
        for (int i = 0; i < edtList.size(); i++) {
            TextField item = edtList.get(i);
            if (i <= lastEnableIndex)
                item.setDisable(false);
            else {
                int finalI = i + 1;
                Platform.runLater(() -> item.setText(defaultLabel.concat(" " + finalI)));
                item.setDisable(true);
            }
        }
    }

    //init the TextFields
    private void initTimesAndDaysList() {

        List<String> daysFromFile = AppSettings.getInstance().getDaysList();
        List<String> timesFromFile = AppSettings.getInstance().getTimesList();

        //init days list
        edtDaysList = gridDays.getChildren()
                .stream()
                .map(item -> ((TextField) item))
                .collect(Collectors.toList());
        for (int i = 0; i < daysFromFile.size(); i++) {
            String dayDescription = daysFromFile.get(i);
            edtDaysList.get(i).setText(dayDescription);
        }
        this.clearAndEnableCells(edtDaysList, daysFromFile.size() - 1, "Day");

        //init times list
        edtTimesList = gridTimes.getChildren()
                .stream()
                .map(item -> (TextField) item)
                .collect(Collectors.toList());
        for (int i = 0; i < timesFromFile.size(); i++) {
            String timeDescription = timesFromFile.get(i);
            edtTimesList.get(i).setText(timeDescription);
        }
        this.clearAndEnableCells(edtTimesList, timesFromFile.size() - 1, "Time");
    }

    //init days and times combobox
    private void initComboBoxes() {
        int nDaysSettingsFile = AppSettings.getInstance().getDaysList().size() - 1;
        int nTimesSettingsFile = AppSettings.getInstance().getTimesList().size() - 1;

        List<Integer> cmbItemsList = edtDaysList.stream().map(item -> edtDaysList.indexOf(item) + 1)
                .collect(Collectors.toList());

        cmbDaysPerWeek.setItems(FXCollections.observableList(cmbItemsList));
        cmbTimesPerDay.setItems(FXCollections.observableList(cmbItemsList));
        cmbDaysPerWeek.getSelectionModel().select(nDaysSettingsFile);
        cmbTimesPerDay.getSelectionModel().select(nTimesSettingsFile);

        cmbDaysPerWeek.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            tabPaneDaysAndTimes.getSelectionModel().select(0);
            clearAndEnableCells(edtDaysList, newValue.intValue(), "Day");
        }));
        cmbTimesPerDay.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            tabPaneDaysAndTimes.getSelectionModel().select(1);
            clearAndEnableCells(edtTimesList, newValue.intValue(), "Time");
        }));
    }

    @FXML
    private void confirm() throws IOException {
        List<String> days = this.edtDaysList.stream()
                .filter(edtDay -> !edtDay.isDisable())
                .map(TextField::getText)
                .collect(Collectors.toList());

        List<String> times = this.edtTimesList.stream()
                .filter(edtTime -> !edtTime.isDisable())
                .map(TextField::getText)
                .collect(Collectors.toList());
        //Save the changes
        AppSettings.getInstance().setDaysList(days);
        AppSettings.getInstance().setTimesList(times);
        AppSettings.getInstance().saveSettings();
        this.close();
    }

    @FXML
    private void cancel() throws IOException {
        close();
    }

    @FXML
    private void clear() {
        int nDaysSettingsFile = AppSettings.getInstance().getDaysList().size() - 1;
        int nTimesSettingsFile = AppSettings.getInstance().getTimesList().size() - 1;
        cmbTimesPerDay.getSelectionModel().select(nTimesSettingsFile);
        cmbDaysPerWeek.getSelectionModel().select(nDaysSettingsFile);
        initTimesAndDaysList();
    }
    //TODO -- Programme menubar
}
