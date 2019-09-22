package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.dao.EventDAOImpl;
import br.ufop.tomaz.dao.ProfessorDAOImpl;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.model.Professor;
import br.ufop.tomaz.util.FileUtils;
import br.ufop.tomaz.util.Screen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLManagerProfessorsController implements Initializable, AppScreen {

    @FXML
    private MenuBar menubar;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnImport;
    @FXML
    private ListView<Professor> professorsListView;
    @FXML
    private TextField edtProfessorName;
    @FXML
    private TextField edtTag;
    @FXML
    private TextField edtWorkload;
    @FXML
    private TextField edtPriority;
    @FXML
    private TextField edtSearch;
    @FXML
    private TableView<Event> tabRelatedEvents;
    @FXML
    private TableView<?> tabConstraints;
    private ObservableList<Professor> professorsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        professorsList = FXCollections.observableList(ProfessorDAOImpl.getInstance().getProfessors());
        professorsList.sort(Comparator.comparing(Professor::getName));
        initComponents();
    }

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.HOME);
    }

    @FXML
    private void openAddProfessorScreen() throws IOException {
        App.setScreen(Screen.ADD_PROFESSOR);
    }

    private void initComponents() {

        //init buttons
        btnDelete.disableProperty().bind(professorsListView.getSelectionModel().selectedItemProperty().isNull());
        btnEdit.disableProperty().bind(professorsListView.getSelectionModel().selectedItemProperty().isNull());

        //init search bar
        edtSearch.textProperty().addListener((ob, ov, nv) -> {
            if (!nv.isEmpty())
                professorsListView.setItems(FXCollections.observableList(search(nv)));
            else
                professorsListView.setItems(professorsList);
        });

        //init ListView
        professorsListView.setItems(professorsList);
        professorsListView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null)
                loadProfessor(nv);
            else
                clearFields();
        });

        //configure events table
        tabRelatedEvents.setRowFactory(param -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Event e = row.getItem();
                    try {
                        App.openEditScene(e);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

    @FXML
    private void importProfessors() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Professors_file.csv", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                List<Professor> professors = new FileUtils().importProfessors(file);
                professorsList.setAll(ProfessorDAOImpl.getInstance().getProfessors());
                professorsList.sort(Comparator.comparing(Professor::getName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Occurred a problem when tried to open the file. Please try again.");
        }
    }

    @FXML
    private void deleteProfessor() {
        Professor professor = professorsListView.getSelectionModel().getSelectedItem();
        ProfessorDAOImpl.getInstance().deleteProfessor(professor);
        professorsList.remove(professor);
    }

    @FXML
    private void editProfessor() throws IOException {
        Professor professor = professorsListView.getSelectionModel().getSelectedItem();
        App.openEditScene(professor);
    }

    private List<Professor> search(String name) {
        return professorsList.stream()
                .filter(professor -> professor.getName().toUpperCase().contains(name.toUpperCase()))
                .collect(Collectors.toList());
    }

    private void clearFields() {
        edtPriority.textProperty().unbind();
        edtWorkload.textProperty().unbind();
        edtTag.textProperty().unbind();
        edtProfessorName.textProperty().unbind();
        edtProfessorName.setText("");
        edtWorkload.setText("");
        edtPriority.setText("");
        edtTag.setText("");
        tabRelatedEvents.setItems(FXCollections.emptyObservableList());
    }

    private void loadProfessor(Professor professor) {
        edtProfessorName.textProperty().bind(professor.nameProperty());
        edtWorkload.textProperty().bind(professor.workloadProperty().asString());
        edtPriority.textProperty().bind(professor.priorityProperty().asString());
        edtTag.textProperty().bind(professor.tagProperty());
        List<Event> relatedEvents = EventDAOImpl.getInstance().getEventsByProfessor(professor);
        tabRelatedEvents.setItems(FXCollections.observableList(relatedEvents));
    }
}
