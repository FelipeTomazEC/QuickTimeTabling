package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.controller.interfaces.EditScreen;
import br.ufop.tomaz.dao.ProfessorDAOImpl;
import br.ufop.tomaz.model.Editable;
import br.ufop.tomaz.model.Professor;
import br.ufop.tomaz.model.Unavailability;
import br.ufop.tomaz.model.UndesiredPattern;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.Day;
import br.ufop.tomaz.util.Screen;
import br.ufop.tomaz.util.TableTimesUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLAddProfessorController implements Initializable, AppScreen, EditScreen {

    @FXML
    private TableView<Day> timesTableView;
    @FXML
    private TableView<Pattern> undPatternsTableView;
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
    private Button btnRemoveUndesiredPattern;
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
        initPatternsTable(undPatternsTableView);
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
        undPatternsTableView.getItems().clear();
        btnRemoveUndesiredPattern.disableProperty().bind(getPatternsRemoveBinding());
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

        List<UndesiredPattern> undesiredPatternList = undPatternsTableView.getItems()
                .stream()
                .map(pattern -> pattern.getUndesiredPattern())
                .collect(Collectors.toList());
        professor.setUndesiredPatterns(undesiredPatternList);

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
        List<Pattern> undesiredPatternList = editProfessor.getUndesiredPatterns()
                .stream()
                .map(undesiredPattern -> getPatternFromUndesiredPattern(undesiredPattern))
                .collect(Collectors.toList());
        undPatternsTableView.getItems().setAll(undesiredPatternList);
        btnRemoveUndesiredPattern.disableProperty().bind(getPatternsRemoveBinding());
        configureConfirmEditionButton();
    }

    @Override
    public void configureConfirmEditionButton() {
        btnConfirm.setText("Confirm");
        btnConfirm.setOnAction(event -> {
            editProfessor.setPriority(cmbPriority.getSelectionModel().getSelectedItem());
            List<Unavailability> unavailabilities = TableTimesUtils.getUnavailabilities(timesTableView);
            editProfessor.setUnavailabilities(unavailabilities);
            List<UndesiredPattern> undesiredPatternList = undPatternsTableView.getItems()
                    .stream()
                    .map(pattern -> pattern.getUndesiredPattern())
                    .collect(Collectors.toList());
            editProfessor.setUndesiredPatterns(undesiredPatternList);
            ProfessorDAOImpl.getInstance().updateProfessor(editProfessor);
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initPatternsTable(TableView<Pattern> undesiredPatternTableView){

        List<String> days = AppSettings.getInstance().getDaysList();

        List<TableColumn<Pattern, Boolean>> tableColumnList = days.stream()
                .map((day) -> new TableColumn<Pattern, Boolean>(day))
                .collect(Collectors.toList());

        tableColumnList.forEach(column -> {
            column.setCellValueFactory(cellData -> {
                int index = cellData.getTableView().getColumns().indexOf(cellData.getTableColumn());
                return cellData.getValue().days.get(index);
            });

            column.setSortable(false);

            column.setCellFactory(param -> new UndesiredPatternTableCell());

        });

        undesiredPatternTableView.getColumns().addAll(tableColumnList);

        TableColumn<Pattern, Boolean> removeColumn = new TableColumn<>("Remove");
        removeColumn.setId("header-remove");
        removeColumn.setSortable(false);
        removeColumn.setCellValueFactory(param -> param.getValue().remove);
        removeColumn.setCellFactory(param -> new CheckBoxTableCell<>());
        undesiredPatternTableView.setEditable(true);
        undesiredPatternTableView.getColumns().add(removeColumn);
    }

    @FXML
    private void addNewUndesiredPattern(){
        Pattern pattern = new Pattern();
        undPatternsTableView.getItems().add(pattern);
        btnRemoveUndesiredPattern.disableProperty().bind(getPatternsRemoveBinding());
    }

    private BooleanBinding getPatternsRemoveBinding (){
        BooleanBinding binding = new SimpleBooleanProperty(false)
                .or(new SimpleBooleanProperty(false));
        for (Pattern p : undPatternsTableView.getItems()){
            binding = binding.or(p.remove);
        }
        return binding.not();
    }

    private Pattern getPatternFromUndesiredPattern(UndesiredPattern undesiredPattern){
        Pattern pattern = new Pattern();
        char [] undesired = undesiredPattern.getPattern();
        for(int i = 0; i < undesired.length; i++){
            if(undesired[i] == '0')
                pattern.setDayAvailability(i, false);
        }
        return pattern;
    }

    @FXML
    private void removeSelectedPatterns(){
        List<Pattern> patternToRemoveList = this.undPatternsTableView
                .getItems()
                .filtered(pattern -> pattern.remove.get());
        undPatternsTableView.getItems().removeAll(patternToRemoveList);
        btnRemoveUndesiredPattern.disableProperty().bind(getPatternsRemoveBinding());
    }

    private class UndesiredPatternTableCell extends TableCell<Pattern, Boolean>{

        public UndesiredPatternTableCell(){
            this.setOnMouseClicked((event)-> onClickAction());
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setStyle("-fx-border-color: transparent; -fx-background-color: white;" );
                setOnMouseClicked(null);
            } else {
                setOnMouseClicked(event -> this.onClickAction());
                String color = (item) ? "#008000" : "#FF0000";
                setStyle("-fx-border-color: #cacaca; -fx-background-color:"+ color + ';' );
            }
        }

        private void onClickAction(){
            int dayIndex = getTableView().getColumns().indexOf(getTableColumn());
            getTableRow().getItem().setDayAvailability(dayIndex, !getItem());
        }
    }

    private class Pattern {

        private List<BooleanProperty> days;
        private BooleanProperty remove = new SimpleBooleanProperty(false);

        public Pattern() {
            this.days = AppSettings.getInstance()
                    .getDaysList()
                    .stream()
                    .map(day -> new SimpleBooleanProperty(true))
                    .collect(Collectors.toList());
        }

        public boolean isRemove() {
            return remove.get();
        }

        public BooleanProperty removeProperty() {
            return remove;
        }

        public void setRemove(boolean remove) {
            this.remove.set(remove);
        }

        public void setDayAvailability(int dayIndex, boolean value){
            this.days.get(dayIndex).setValue(value);
        }

        public UndesiredPattern getUndesiredPattern(){
            char [] pattern = this.days.stream()
                    .map(day -> (day.get()) ? "1" : "0")
                    .reduce((accumulator, ch) -> accumulator.concat(ch))
                    .orElse("")
                    .toCharArray();
            return new UndesiredPattern(pattern);
        }
    }
}
