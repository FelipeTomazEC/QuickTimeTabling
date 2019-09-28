package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.dao.EventDAOImpl;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.model.ProfessorWeight;
import br.ufop.tomaz.util.FileUtils;
import br.ufop.tomaz.util.Screen;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class FXMLManagerEventsController implements Initializable, AppScreen {

    @FXML
    public MenuBar menubar;
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
    private Button btnNoFilters;
    @FXML
    private Button btnExport;
    @FXML
    private ComboBox<String> cmbTag;
    @FXML
    private ComboBox<String> cmbClass;
    @FXML
    private ComboBox<String> cmbSubject;
    @FXML
    private ComboBox<String> cmbProfessor;
    @FXML
    private ListView<Event> eventsListView;
    @FXML
    private TextField edtSubject;
    @FXML
    private TextField edtTag;
    @FXML
    private TextField edtDuration;
    @FXML
    private TextField edtSearch;
    @FXML
    private TextField edtClass;
    @FXML
    private TableView<ProfessorWeight> tabCompatibleProfessors;
    private ObservableList<Event> events;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        events = FXCollections.observableList(EventDAOImpl.getInstance().getAllEvents());
        loadEventsListView();
        loadCmbFilters();
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        //Init buttons
        btnDelete.disableProperty().bind(eventsListView.getSelectionModel().selectedItemProperty().isNull());
        btnEdit.disableProperty().bind(eventsListView.getSelectionModel().selectedItemProperty().isNull());

        //Init combobox
        ChangeListener changeListener = (ob, ov, nv) -> {
            edtSearch.setText("");
            eventsListView.getSelectionModel().select(-1);
            eventsListView.setItems(FXCollections.observableList(filter()));
        };
        cmbSubject.getSelectionModel().selectedItemProperty().addListener(changeListener);
        cmbClass.getSelectionModel().selectedItemProperty().addListener(changeListener);
        cmbProfessor.getSelectionModel().selectedItemProperty().addListener(changeListener);
        cmbTag.getSelectionModel().selectedItemProperty().addListener(changeListener);

        //Configure search textfield
        edtSearch.textProperty().addListener((ob, ov, nv) -> {
            if (!nv.isEmpty()) {
                eventsListView.setItems(FXCollections.observableList(search(nv)));
            } else
                eventsListView.setItems(FXCollections.observableList(filter()));

        });
    }

    @Override
    public void close() throws IOException {
       App.setScreen(Screen.HOME);
    }

    @FXML
    public void addEvent() throws IOException {
        App.setScreen(Screen.ADD_EVENT);
    }

    private List<Event> filter() {
        final String tag = cmbTag.getValue();
        final String classE = cmbClass.getValue();
        final String subject = cmbSubject.getValue();
        final String professor = cmbProfessor.getValue();

        List<Event> filter = events.stream()
                .filter(e -> e.getTag().equals(tag) || tag.equals("All"))
                .filter(e -> e.getClassE().getName().equals(classE) || classE.equals("All"))
                .filter(e -> e.getSubject().equals(subject) || subject.equals("All"))
                .filter(e -> e.getProfessorWeights()
                        .stream()
                        .map(pw -> pw.getProfessor().getName())
                        .collect(Collectors.toList())
                        .contains(professor) || professor.equals("All"))
                .sorted(Comparator.comparing(Event::getSubject))
                .collect(Collectors.toList());
        return filter;
    }

    @FXML
    private void noFilters() {
        cmbTag.getSelectionModel().select(0);
        cmbProfessor.getSelectionModel().select(0);
        cmbClass.getSelectionModel().select(0);
        cmbSubject.getSelectionModel().select(0);
    }

    private void loadCmbFilters() {
        SortedSet<String> tags = new TreeSet<>();
        SortedSet<String> classes = new TreeSet<>();
        SortedSet<String> subjects = new TreeSet<>();
        SortedSet<String> professors = new TreeSet<>();
        events.forEach((e) -> {
            tags.add(e.getTag());
            classes.add(e.getClassE().getName());
            subjects.add(e.getSubject());
            List<String> professorList = e.getProfessorWeights()
                    .stream()
                    .map(pw -> pw.getProfessor().getName())
                    .collect(Collectors.toList());
            professors.addAll(professorList);
        });

        cmbTag.setItems(FXCollections.observableList(new ArrayList<>(tags)));
        cmbClass.setItems(FXCollections.observableList(new ArrayList<>(classes)));
        cmbProfessor.setItems(FXCollections.observableList(new ArrayList<>(professors)));
        cmbSubject.setItems(FXCollections.observableList(new ArrayList<>(subjects)));

        //Add 'All' option in combobox
        cmbTag.getItems().add(0, "All");
        cmbSubject.getItems().add(0, "All");
        cmbProfessor.getItems().add(0, "All");
        cmbClass.getItems().add(0, "All");
        noFilters();
    }

    private void loadEventsListView() {
        eventsListView.setItems(events);
        eventsListView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                edtClass.textProperty().bind(nv.getClassE().nameProperty());
                edtTag.textProperty().bind(nv.tagProperty());
                edtDuration.textProperty().bind(nv.durationProperty().asString());
                edtSubject.textProperty().bind(nv.subjectProperty());
                tabCompatibleProfessors.setItems(FXCollections.observableList(nv.getProfessorWeights()));
            } else {
                edtClass.textProperty().unbind();
                edtSubject.textProperty().unbind();
                edtDuration.textProperty().unbind();
                edtTag.textProperty().unbind();
                edtClass.clear();
                edtTag.clear();
                edtDuration.clear();
                edtSubject.clear();
                tabCompatibleProfessors.setItems(FXCollections.emptyObservableList());
            }
        });
    }

    @FXML
    private void deleteEvent() {
        Event event = eventsListView.getSelectionModel().getSelectedItem();
        EventDAOImpl.getInstance().deleteEvent(event);
        eventsListView.getItems().remove(event);
    }

    @FXML
    private void editEvent() throws IOException {
        Event event = eventsListView.getSelectionModel().getSelectedItem();
        App.openEditScene(event);
    }

    @FXML
    private void importEvents() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Events_File.csv", "*.csv"));
        File eventsFile = fileChooser.showOpenDialog(null);
        if (eventsFile != null) {
            FileUtils reader = new FileUtils();
            try {
                List<Event> events = reader.importEvents(eventsFile);
                this.events.addAll(events);
                this.events.sort(Comparator.comparing(Event::getSubject));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Occurred a problem when tried to open the file. Please try again.");
        }
    }

    @FXML
    private void exportEvents() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Events");
        ExtensionFilter filter = new ExtensionFilter("Events", "*.csv");
        fileChooser.setInitialFileName("events.csv");
        fileChooser.setSelectedExtensionFilter(filter);

        File exportFile = fileChooser.showSaveDialog(App.getWindow());
        if(exportFile != null){
            String filename = (exportFile.getName().endsWith(".csv")) ?
                    exportFile.getName() : exportFile.getName().concat(".csv");
            String linkedEventsFilename = "linked".concat(filename);
            String separator = System.getProperty("file.separator");
            String eventsFilepath = exportFile.getParent().concat(separator).concat(filename);
            String linkedEventsFilepath = exportFile.getParent().concat(separator).concat(linkedEventsFilename);

            FileUtils fileUtils = new FileUtils();
            fileUtils.exportEvents(events, new File(eventsFilepath));
            fileUtils.exportLinkedEvents(events, new File(linkedEventsFilepath));
        }
    }

    @FXML
    private void importLinkedEvents () {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Linked Events.csv", "*.csv"));
        File eventsFile = fileChooser.showOpenDialog(App.getWindow());
        if (eventsFile != null) {
            FileUtils reader = new FileUtils();
            try {
                reader.importLinkedEvents(eventsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Occurred a problem when tried import the linked events. Please try again.");
        }
    }

    private List<Event> search(String name) {
        return filter().stream()
                .filter(event -> event.getSubject()
                        .toUpperCase()
                        .contains(name.toUpperCase())
                )
                .collect(Collectors.toList());
    }

}
