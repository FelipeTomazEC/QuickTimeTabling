package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.dao.ClassDAOImpl;
import br.ufop.tomaz.dao.EventDAOImpl;
import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.util.ReaderFilesUtils;
import br.ufop.tomaz.util.Screen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLManagerClassesController implements Initializable, AppScreen {

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
    private ListView<ClassE> classesListView;
    @FXML
    private TextField edtClassName;
    @FXML
    private TextField edtSearch;
    @FXML
    private TableView<Event> tabRelatedEvents;
    @FXML
    private TableView<?> tabConstraints;
    private ObservableList<ClassE> classesList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComponents();
        List<ClassE> classesDB = ClassDAOImpl.getInstance().getClasses();
        classesDB.sort(Comparator.comparing(ClassE::getName));
        classesList = FXCollections.observableList(classesDB);
        classesListView.setItems(classesList);
    }

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.HOME);
    }

    @FXML
    private void addClass() throws IOException {
        App.setScreen(Screen.ADD_CLASS);
    }

    private void initComponents() {
        classesListView.setCellFactory(new Callback<ListView<ClassE>, ListCell<ClassE>>() {
            @Override
            public ListCell<ClassE> call(ListView<ClassE> param) {
                return new ListCell<ClassE>() {
                    @Override
                    protected void updateItem(ClassE item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            setText("");
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });
        classesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, ov, nv) -> {
                    if (nv != null) {
                        edtClassName.textProperty().bind(nv.nameProperty());
                        List<Event> eventList = EventDAOImpl.getInstance().getEventsByClass(nv);
                        tabRelatedEvents.setItems(FXCollections.observableList(eventList));
                    } else {
                        edtClassName.textProperty().unbind();
                        edtClassName.setText("");
                        tabRelatedEvents.setItems(FXCollections.emptyObservableList());
                    }
                });
        edtSearch.textProperty().addListener((ob, ov, nv) -> {
            if (!nv.isEmpty()) {
                classesListView.setItems(FXCollections.observableList(filter(nv)));
            } else {
                classesListView.setItems(classesList);
            }
        });
        btnEdit.disableProperty().bind(classesListView.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(classesListView.getSelectionModel().selectedItemProperty().isNull());
    }

    private List<ClassE> filter(String name) {
        return classesList.stream()
                .filter(classE -> classE.getName().toUpperCase().contains(name.toUpperCase()))
                .collect(Collectors.toList());
    }

    @FXML
    private void importClasses() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Classes_File.csv", "*.csv"));
        File classesFile = fileChooser.showOpenDialog(null);
        if (classesFile != null) {
            ReaderFilesUtils reader = new ReaderFilesUtils();
            try {
                List<ClassE> classes = reader.importClasses(classesFile);
                classesList.addAll(classes);
                classesList.sort(Comparator.comparing(ClassE::getName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Occurred a problem when tried to open the file. Please try again.");
        }
    }

    @FXML
    private void deleteClass() {
        ClassE classE = classesListView.getSelectionModel().getSelectedItem();
        ClassDAOImpl.getInstance().deleteClass(classE);
        classesList.remove(classE);
    }

    @FXML
    private void editClass() throws IOException {
        ClassE classE = classesListView.getSelectionModel().getSelectedItem();
        App.openEditScene(classE);
    }
}
