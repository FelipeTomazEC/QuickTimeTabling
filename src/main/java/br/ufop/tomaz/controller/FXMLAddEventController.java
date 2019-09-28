package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.controller.interfaces.EditScreen;
import br.ufop.tomaz.dao.ClassDAOImpl;
import br.ufop.tomaz.dao.EventDAO;
import br.ufop.tomaz.dao.EventDAOImpl;
import br.ufop.tomaz.dao.ProfessorDAOImpl;
import br.ufop.tomaz.model.*;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.Screen;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableList;

public class FXMLAddEventController implements Initializable, AppScreen, EditScreen {

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
    private TextField edtSubject;
    @FXML
    private TextField edtTag;
    @FXML
    private Spinner<Integer> spnDuration;
    @FXML
    private Spinner<Integer> spnMinGap;
    @FXML
    private Spinner<Integer> spnMaxGap;
    @FXML
    private ComboBox<String> cmbSplit;
    @FXML
    private ComboBox<ClassE> cmbClass;
    @FXML
    private ComboBox<Event> cmbLinkedEvent;
    @FXML
    private ListView<Professor> availableProfessorsListView;
    @FXML
    private TableView<ProfessorWeight> compatibleProfessorsTabView;
    @FXML
    private TableColumn<ProfessorWeight, String> nameColumn;
    @FXML
    private TableColumn<ProfessorWeight, Spinner<Integer>> costColumn;
    @FXML
    private Button btnExclude;
    @FXML
    private Button btnInclude;
    private ObservableList<Professor> availableProfessors = observableArrayList();
    private ListProperty<ProfessorWeight> compatibleProfessors = new SimpleListProperty<>(observableArrayList());
    private Event editEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComponents();
        initCmbClasses();
        initCmbLinkedEvent();
        initListView();
        initTabCompatibleProfessors();
    }

    private void initComponents() {
        //init buttons
        btnInclude.disableProperty().bind(
                availableProfessorsListView.getSelectionModel().selectedItemProperty().isNull()
        );
        btnExclude.disableProperty().bind(
                compatibleProfessorsTabView.getSelectionModel().selectedItemProperty().isNull()
        );
        btnConfirm.disableProperty().bind(
                edtSubject.textProperty().isEmpty()
                        .or(edtTag.textProperty().isEmpty())
                        .or(cmbClass.getSelectionModel().selectedItemProperty().isNull())
                        .or(compatibleProfessors.emptyProperty())
        );

        //init spinners
        int nDays = AppSettings.getInstance().getDaysList().size();
        spnMinGap.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, nDays, 0));
        spnMaxGap.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, nDays, 0));
        spnDuration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        spnDuration.valueProperty().addListener((ob, ov, nv) -> {
            fillCmbSplit(nv);
            cmbSplit.getSelectionModel().select(0);
        });

        //init comboBoxes
        fillCmbSplit(spnDuration.getValue());
        cmbSplit.getSelectionModel().select(0);
    }

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.MANAGER_EVENTS);
    }

    @Override
    public void loadEditable(Editable editable) {
        editEvent = (Event) editable;
        edtSubject.textProperty().bindBidirectional(editEvent.subjectProperty());
        edtTag.textProperty().bindBidirectional(editEvent.tagProperty());
        spnDuration.getValueFactory().valueProperty().bindBidirectional(editEvent.durationProperty().asObject());
        spnMaxGap.getValueFactory().valueProperty().bindBidirectional(editEvent.maxGapBetweenMeetingsProperty().asObject());
        spnMinGap.getValueFactory().valueProperty().bindBidirectional(editEvent.minGapBetweenMeetingsProperty().asObject());
        cmbSplit.getSelectionModel().select(editEvent.getSplit());
        cmbClass.getSelectionModel().select(editEvent.getClassE());
        List<String> compatible = editEvent.getProfessorWeights()
                .stream()
                .map(p -> p.getProfessor().getName())
                .collect(Collectors.toList());
        List<Professor> compatibles = availableProfessors.filtered(professor -> compatible.contains(professor.getName()));
        availableProfessors.removeAll(compatibles);
        compatibleProfessors.setAll(editEvent.getProfessorWeights());
        cmbLinkedEvent.getSelectionModel().select(editEvent.getLinkedEvent());
        configureConfirmEditionButton();
    }

    @Override
    public void configureConfirmEditionButton() {
        btnConfirm.setText("Confirm");
        btnConfirm.setOnAction(event -> {
            editEvent.setClassE(cmbClass.getSelectionModel().getSelectedItem());
            editEvent.setProfessorWeights(compatibleProfessors);
            editEvent.setSplit(cmbSplit.getValue());
            Event linkedEvent = (cmbLinkedEvent.getSelectionModel().getSelectedIndex() == 0)
                    ? null : cmbLinkedEvent.getValue();
            editEvent.setLinkedEvent(linkedEvent);
            EventDAOImpl.getInstance().updateEvent(editEvent);
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Fill Split ComboBox
    private void fillCmbSplit(int duration) {
        List<String> combinations = new ArrayList<>();
        int tam = 1;
        while (tam <= duration) {
            //DECOMPÕE O NUMERO EM TAM ALGARISMOS
            Integer[] comb = new Integer[tam];
            int aux = duration;
            for (int j = 1; j < tam; j++) {
                comb[j] = 1;
                aux--;
            }
            comb[0] = aux;
            combinations.add(strCombination(comb));
            int i = 0;
            while (i < tam - 1) {
                int j = i + 1;
                while (comb[i] - 1 > comb[j]) {
                    comb[i]--;
                    comb[j]++;
                    combinations.add(strCombination(comb));
                }
                i++;
            }
            tam++;
        }
        cmbSplit.setItems(observableList(combinations));
    }

    //Return the string Combination
    private String strCombination(Integer[] comb) {
        String strComb = "";
        for (int i = 0; i < comb.length; i++) {
            if (i < comb.length - 1) {
                strComb = strComb.concat(comb[i] + "+");
            } else {
                strComb = strComb.concat(String.valueOf(comb[i]));
            }
        }
        return strComb;
    }

    private void initCmbClasses() {

        Task<List<ClassE>> task = new Task<List<ClassE>>() {
            @Override
            protected List<ClassE> call() {
                return ClassDAOImpl.getInstance().getClasses();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                cmbClass.setItems(observableList(getValue()));
            }
        };
        new Thread(task).start();
    }

    private void initListView() {
        availableProfessors = FXCollections.observableList(ProfessorDAOImpl.getInstance().getProfessors());
        SortedList<Professor> professorSortedList =
                new SortedList<>(availableProfessors, Comparator.comparing(Professor::getName));
        availableProfessorsListView.setItems(professorSortedList);
    }

    @FXML
    private void includeProfessor() {
        Professor professor = availableProfessorsListView.getSelectionModel().getSelectedItem();
        availableProfessorsListView.getSelectionModel().clearSelection();
        availableProfessors.remove(professor);
        compatibleProfessors.add(new ProfessorWeight(professor, 0));
    }

    @FXML
    private void excludeProfessor() {
        ProfessorWeight pw = compatibleProfessorsTabView.getSelectionModel().getSelectedItem();
        availableProfessors.add(pw.getProfessor());
        compatibleProfessors.remove(pw);
        compatibleProfessorsTabView.getSelectionModel().clearSelection();
    }

    private void initTabCompatibleProfessors() {
        SortedList<ProfessorWeight> pwSorted = new SortedList<>(compatibleProfessors);
        pwSorted.setComparator(Comparator.comparing(o -> o.getProfessor().getName()));
        compatibleProfessorsTabView.setItems(pwSorted);
        nameColumn.setCellValueFactory(param -> {
            Professor professor = param.getValue().getProfessor();
            return professor.nameProperty();
        });
        costColumn.setCellValueFactory(param -> {
            ProfessorWeight pw = param.getValue();
            Spinner<Integer> spnCost = new Spinner<>(0, 10, pw.getWeight(), 1);
            spnCost.getValueFactory().valueProperty().bindBidirectional(pw.weightProperty().asObject());
            return new SimpleObjectProperty<>(spnCost);
        });
    }

    @FXML
    private void addEventToDb() {
        Event event = new Event();
        event.setSubject(edtSubject.getText());
        event.setTag(edtTag.getText());
        event.setSplit(cmbSplit.getSelectionModel().getSelectedItem());
        event.setMinGapBetweenMeetings(spnMinGap.getValue());
        event.setMaxGapBetweenMeetings(spnMaxGap.getValue());
        event.setDuration(spnDuration.getValue());
        event.setClassE(cmbClass.getSelectionModel().getSelectedItem());
        event.setProfessorWeights(new ArrayList<>(compatibleProfessors));

        if(cmbLinkedEvent.getSelectionModel().getSelectedIndex() != 0){
            event.setLinkedEvent(cmbLinkedEvent.getValue());
        }

        EventDAOImpl.getInstance().persistEvent(event);
        clearFields();
    }

    @FXML
    private void clearFields() {
        edtSubject.setText("");
        edtTag.setText("");
        spnDuration.getValueFactory().setValue(0);
        spnMaxGap.getValueFactory().setValue(0);
        spnMinGap.getValueFactory().setValue(0);
        cmbClass.getSelectionModel().select(-1);
        compatibleProfessors.forEach((p) -> availableProfessors.add(p.getProfessor()));
        compatibleProfessors.clear();
        cmbLinkedEvent.getSelectionModel().selectFirst();
    }

    @FXML
    private void initCmbLinkedEvent(){
        EventDAO eventDAO = EventDAOImpl.getInstance();
        List<Event> eventList = eventDAO.getAllEvents();
        Event none = new Event();
        none.setSubject("None");
        cmbLinkedEvent.getItems().add(none);
        cmbLinkedEvent.getSelectionModel().select(none);
        cmbLinkedEvent.getItems().addAll(eventList);
        cmbLinkedEvent.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Event> call(ListView<Event> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Event item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            String text = item.getSubject();
                            ClassE classE = item.getClassE();
                            text = (item.getSubject().equalsIgnoreCase("none"))
                                    ? text : text.concat(" - ").concat(classE.getName());
                            setText(text);
                        }
                    }
                };
            }
        });
    }

}
