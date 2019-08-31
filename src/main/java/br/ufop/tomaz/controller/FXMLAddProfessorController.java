package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.controller.interfaces.EditScreen;
import br.ufop.tomaz.dao.ProfessorDAOImpl;
import br.ufop.tomaz.model.Editable;
import br.ufop.tomaz.model.Professor;
import br.ufop.tomaz.model.Unavailability;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.Day;
import br.ufop.tomaz.util.Screen;
import br.ufop.tomaz.util.TableTimesUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAddProfessorController implements Initializable, AppScreen, EditScreen {

    @FXML
    private TableView<Day> timesTableView;
    @FXML
    private TableView<Day> undPatternsTableView;
    @FXML
    private MenuBar menubar;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField edtName;
    @FXML
    private Spinner<Integer> spnWorkload;
    @FXML
    private Spinner<Integer> spnMinDaysWorking;
    @FXML
    private Spinner<Integer> spnMaxDaysWorking;
    @FXML
    private ComboBox<Double> cmbPriority;
    @FXML
    private TextField edtTag;
    private Professor editProfessor = null;

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.MANAGER_PROFESSORS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComponents();
    }

    private void initComponents() {
        int nDays = AppSettings.getInstance().getDaysList().size();
        int nTimes = AppSettings.getInstance().getTimesList().size();
        spnMinDaysWorking.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, nDays));
        spnMaxDaysWorking.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, nDays));
        spnWorkload.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, nDays * nTimes));
        cmbPriority.setItems(FXCollections.observableList(Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0)));
        btnConfirm.disableProperty().bind(
                edtName.textProperty()
                        .isEmpty()
                        .or(edtTag.textProperty().isEmpty())
                        .or(cmbPriority.getSelectionModel().selectedItemProperty().isNull())
        );

        //Init table times
        TableTimesUtils timesUtils = new TableTimesUtils(TableTimesUtils.PROFESSORS_TABLE_TYPE);
        timesUtils.prepareTableTimes(timesTableView);
        List<Day> days = AppSettings.getInstance().defaultDays();
        timesTableView.setItems(FXCollections.observableList(days));
    }

    @FXML
    private void clearFields() {
        edtName.setText("");
        edtTag.setText("");
        spnWorkload.getValueFactory().setValue(0);
        spnMaxDaysWorking.getValueFactory().setValue(0);
        spnMinDaysWorking.getValueFactory().setValue(0);
        cmbPriority.getSelectionModel().clearSelection();
        timesTableView.setItems(FXCollections.observableList(AppSettings.getInstance().defaultDays()));
    }

    @FXML
    private void addProfessorDb() {
        Professor professor = new Professor();
        professor.setName(edtName.getText());
        professor.setTag(edtTag.getText());
        professor.setWorkload(spnWorkload.getValue());
        professor.setPriority(cmbPriority.getSelectionModel().getSelectedItem());
        professor.setPreferredMinNumberOfWorkingDays(spnMinDaysWorking.getValue());
        professor.setPreferredMaxNumberOfWorkingDays(spnMaxDaysWorking.getValue());
        professor.setUnavailabilities(TableTimesUtils.getUnavailabilities(timesTableView));
        new Thread(() -> ProfessorDAOImpl.getInstance().persistProfessor(professor)).start();
        clearFields();
    }

    @Override
    public void loadEditable(Editable editable) {
        editProfessor = (Professor) editable;
        edtName.textProperty().bindBidirectional(editProfessor.nameProperty());
        edtTag.textProperty().bindBidirectional(editProfessor.tagProperty());
        spnMinDaysWorking.getValueFactory()
                .valueProperty()
                .bindBidirectional(editProfessor.preferedMinNumberOfWorkingDaysProperty().asObject());
        spnMaxDaysWorking.getValueFactory()
                .valueProperty()
                .bindBidirectional(editProfessor.preferredMaxNumberOfWorkingDaysProperty().asObject());
        spnWorkload.getValueFactory()
                .valueProperty()
                .bindBidirectional(editProfessor.workloadProperty().asObject());
        cmbPriority.getSelectionModel().select(editProfessor.getPriority());
        TableTimesUtils.loadUnavailabilities(timesTableView, editProfessor.getUnavailabilities());
        configureConfirmEditionButton();
    }

    @Override
    public void configureConfirmEditionButton() {
        btnConfirm.setText("Confirm");
        btnConfirm.setOnAction(event -> {
            editProfessor.setPriority(cmbPriority.getSelectionModel().getSelectedItem());
            List<Unavailability> unavailabilities = TableTimesUtils.getUnavailabilities(timesTableView);
            editProfessor.setUnavailabilities(unavailabilities);
            ProfessorDAOImpl.getInstance().updateProfessor(editProfessor);
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
