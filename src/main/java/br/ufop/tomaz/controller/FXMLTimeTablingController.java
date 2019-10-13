package br.ufop.tomaz.controller;

import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.dao.*;
import br.ufop.tomaz.model.*;
import br.ufop.tomaz.services.SolutionGenerator;
import br.ufop.tomaz.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private ComboBox<String> cbmResource;
    @FXML
    private TextField edtSearch;
    @FXML
    private ListView<Resource> resourcesListView;
    @FXML
    private TableView<?> violatedConstraintsTableView;
    @FXML
    private VBox rightContent;

    private TimeTablingGrid tablingGrid;
    private List<EventAssignment> assignments;

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.HOME);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.generateSolution();
        //this.assignments = readAssignments();
        this.configureResourceListView();
        this.initTimeTabling();
        //this.initResourceComboBox();
        this.initSearchBar();
    }

    private void initResourceComboBox(){
        this.cbmResource.getItems().addAll("Classes", "Professors");
        this.cbmResource.getSelectionModel()
                .selectedItemProperty()
                .addListener(((ob, ov, nv) -> {
                    List<Resource> resources = (nv.equals("Classes")) ?
                            loadResources(ClassE.class) : loadResources(Professor.class);
                    resourcesListView.getItems().setAll(resources);
                    edtSearch.textProperty().setValue("");
                }));
        this.cbmResource.getSelectionModel().select(0);
    }

    private void initSearchBar(){
        this.resourcesListView.getItems().addListener((ListChangeListener<Resource>) change -> {
            boolean isEmpty = resourcesListView.getItems().isEmpty();
            edtSearch.setDisable(isEmpty);
        });

        this.edtSearch.textProperty()
                .addListener(((ob, ov, nv) -> {
                    String resourceType = cbmResource.getSelectionModel().getSelectedItem();
                    Class resourceClass = (resourceType.equals("Classes")) ? ClassE.class : Professor.class;
                    List<Resource> filtered = loadResources(resourceClass).stream()
                            .filter(res -> res.getName().toLowerCase().contains(nv.toLowerCase()))
                            .collect(Collectors.toList());
                    resourcesListView.getItems().setAll(filtered);
                }));
    }

    private void initTimeTabling(){
        this.tablingGrid = new TimeTablingGrid();
        VBox.setVgrow(tablingGrid, Priority.ALWAYS);
        rightContent.getChildren().add(0, tablingGrid);
    }

    private List<EventAssignment> readAssignments() {
        try {
//            FileChooser fileChooser = new FileChooser();
//            ExtensionFilter filter = new ExtensionFilter("Event Assignments File", "*.sol");
//            fileChooser.getExtensionFilters().add(filter);
//            File file = fileChooser.showOpenDialog(App.getWindow());
            File file = new File(System.getProperty("user.home").concat("/ET.sol"));
            System.out.println(file.getAbsolutePath());
            return  new FileUtils().importEventsAssignments(file);
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

    private List<Resource> loadProfessors(List<EventAssignment> eventAssignments){
        ProfessorDAO professorDAO = ProfessorDAOImpl.getInstance();

        return eventAssignments.stream()
                .map(eventAssignment -> eventAssignment.getProfessor())
                .distinct()
                .map(professor -> professorDAO.getProfessorByName(professor.getName()))
                .collect(Collectors.toList());
    }

    private List<Resource> loadClasses(List<EventAssignment> eventAssignments){
        ClassDAO classDAO = ClassDAOImpl.getInstance();

        return eventAssignments.stream()
                .map(eventAssignment -> eventAssignment.getClassE())
                .distinct()
                .map(classE -> classDAO.getClassByName(classE.getName()))
                .collect(Collectors.toList());
    }

    private List<Resource> loadResources(Class resourceClass){
        return (resourceClass == ClassE.class) ? loadClasses(assignments) : loadProfessors(assignments);
    }

    private void loadAssignmentsOf(Resource resource){
        List<EventAssignment> assignments = (resource.getClass() == ClassE.class) ?
                getAssignmentsOf((ClassE) resource) : getAssignmentsOf((Professor) resource);
        tablingGrid.getItems().addAll(assignments);
    }

    private void configureResourceListView(){
        resourcesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, ov, nv) ->{
                    tablingGrid.getItems().clear();
                    if(nv != null)
                        loadAssignmentsOf(nv);
                });
    }

    private void generateSolution(){
        //TODO -- Create function to generate the solution
        List<Professor> professorList = ProfessorDAOImpl.getInstance().getProfessors();
        List<ClassE> classList = ClassDAOImpl.getInstance().getClasses();
        List<Event> eventList = EventDAOImpl.getInstance().getAllEvents();

        try {
            SolutionGenerator solutionGenerator = new SolutionGenerator(professorList, classList, eventList);
            //solutionGenerator.getSolution(300.0);
        } catch (IOException e) {
            System.err.println("Occurred some error when trying to create Solution File!");
            e.printStackTrace();
        }
    }
}
