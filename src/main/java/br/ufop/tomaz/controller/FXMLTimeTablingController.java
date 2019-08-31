package br.ufop.tomaz.controller;

import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.dao.ClassDAOImpl;
import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.EventAssignment;
import br.ufop.tomaz.util.ReaderFilesUtils;
import br.ufop.tomaz.util.TimeTablingGrid;
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
    private ComboBox<?> cbmResource;
    @FXML
    private TextField edtSearch;
    @FXML
    private ListView<?> resourcesListView;
    @FXML
    private TableView<?> violatedConstraintsTableView;
    @FXML
    private VBox rightContent;
    private TimeTablingGrid tablingGrid;
    private List<EventAssignment> assignments;

    @Override
    public void close() throws IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO -- FILL WITH SOME VALID LIST
        readAssignments();
        final ClassE classE = ClassDAOImpl.getInstance().getClassByName("EC_01");
        final ClassE classE1 = ClassDAOImpl.getInstance().getClassByName("EC_08");
        tablingGrid = new TimeTablingGrid();
        tablingGrid.getItems().setAll(assignments.stream()
                .filter(ea -> ea.getClassE().getId() == classE.getId())
                .collect(Collectors.toList())
        );
        tablingGrid.getItems().addAll(assignments.stream()
                .filter(ea -> ea.getClassE().getId() == classE1.getId())
                .collect(Collectors.toList())
        );
        VBox.setVgrow(tablingGrid, Priority.ALWAYS);
        rightContent.getChildren().add(0, tablingGrid);
    }

    private void readAssignments() {
        File file = new File("C:\\Users\\Felipe Tomaz\\Google Drive\\Easytables\\Instances\\ICEA2018-2+DECSI\\ET.sol");
        try {
            assignments = new ReaderFilesUtils().importEventsAssignments(file);
        } catch (IOException e) {
            assignments = new ArrayList<>();
            e.printStackTrace();
        }
    }

}
