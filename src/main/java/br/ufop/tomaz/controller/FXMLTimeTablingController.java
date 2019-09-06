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
import br.ufop.tomaz.model.Resource;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

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
        this.assignments = readAssignments();
        this.configureResourceListView();
        this.initTimeTabling();
        this.initResourceComboBox();
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

}
