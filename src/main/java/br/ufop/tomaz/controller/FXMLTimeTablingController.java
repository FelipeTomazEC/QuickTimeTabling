package br.ufop.tomaz.controller;

import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.dao.ClassDAO;
import br.ufop.tomaz.dao.ClassDAOImpl;
import br.ufop.tomaz.dao.ProfessorDAO;
import br.ufop.tomaz.dao.ProfessorDAOImpl;
import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.EventAssignment;
import br.ufop.tomaz.model.Professor;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.DayAssignment;
import br.ufop.tomaz.util.ReaderFilesUtils;
import br.ufop.tomaz.util.Screen;
import br.ufop.tomaz.util.TimeTablingTableView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLTimeTablingController implements Initializable, AppScreen {

    @FXML
    private MenuBar menubar;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnValidate;
    @FXML
    private Button btnUndo;
    @FXML
    private Button btnRedo;
    @FXML
    private Button btnExport;
    @FXML
    private ComboBox<?> cbmResource;
    @FXML
    private TextField edtSearch;
    @FXML
    private ListView<ClassE> resourcesListView;
    @FXML
    private TableView<?> violatedConstraintsTableView;
    @FXML
    private VBox rightContent;
    private TimeTablingTableView tablingGrid;
    private List<EventAssignment> assignments;

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.HOME);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.assignments = readAssignments();
        this.loadClasses(assignments);
        this.configureResourceListView();
        tablingGrid = new TimeTablingTableView();
        VBox.setVgrow(tablingGrid, Priority.ALWAYS);
        rightContent.getChildren().add(0, tablingGrid);
    }

    private List<EventAssignment> readAssignments() {
        try {
            FileChooser fileChooser = new FileChooser();
            ExtensionFilter filter = new ExtensionFilter("Event Assignments File", "*.sol");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(App.getWindow());
            return  new ReaderFilesUtils().importEventsAssignments(file);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private List<EventAssignment> getAssignmentsOf(ClassE c){
        return assignments.stream()
                .filter(assignment -> assignment.getClassE().equals(c))
                .collect(Collectors.toList());
    }

    private List<EventAssignment> getAssignmentsOf(Professor p){
        return assignments.stream()
                .filter(assignment -> assignment.getProfessor().equals(p))
                .collect(Collectors.toList());
    }

    private void loadProfessors(List<EventAssignment> eventAssignments){
        ProfessorDAO professorDAO = ProfessorDAOImpl.getInstance();
        List<Professor> professors = eventAssignments.stream()
                .map(eventAssignment -> eventAssignment.getProfessor())
                .distinct()
                .map(professor -> professorDAO.getProfessorByName(professor.getName()))
                .collect(Collectors.toList());
        //resourcesListView.getItems().setAll(professors);
    }

    private void loadClasses(List<EventAssignment> eventAssignments){
        ClassDAO classDAO = ClassDAOImpl.getInstance();
        List<ClassE> classes = eventAssignments.stream()
                .map(eventAssignment -> eventAssignment.getClassE())
                .distinct()
                .map(classE -> classDAO.getClassByName(classE.getName()))
                .collect(Collectors.toList());
        resourcesListView.getItems().setAll(classes);
    }

    private void loadAssignmentsOf(ClassE c){
        tablingGrid.getItems().clear();
        List<EventAssignment> assignments = getAssignmentsOf(c);
        Map<Integer, List<EventAssignment>> mapAssignments = assignments.stream()
                .collect(Collectors.groupingBy(EventAssignment::getDay));
        mapAssignments.forEach((k, v) ->{
           String dayName =  AppSettings.getInstance().getDaysList().get(k - 1);
           tablingGrid.getItems().add(new DayAssignment(dayName, v));
        });

    }

    private void configureResourceListView(){
        resourcesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, ov, nv) ->{
                    loadAssignmentsOf(nv);
                });
    }

}
