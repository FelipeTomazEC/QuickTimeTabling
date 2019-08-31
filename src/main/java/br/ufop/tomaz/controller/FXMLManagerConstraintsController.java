package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.util.Screen;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLManagerConstraintsController implements Initializable, AppScreen {

    @FXML private MenuBar menubar;

    @FXML private Button btnClose;

    @FXML private Button btnAdd;

    @FXML private Button btnEdit;

    @FXML private Button btnDelete;

    @FXML private Button btnImport;

    @FXML private Button btnNoFilters;

    @FXML private ComboBox<?> cmbApplieTo;

    @FXML private ComboBox<?> cmbType;

    @FXML private ComboBox<?> cmbNameSubject;

    @FXML private ListView<?> constraintsListView;

    @FXML private TextField edtType;

    @FXML private TextField edtApplieTo;

    @FXML private TextField edtWeight;

    @FXML private TextArea edtAdditionalInfo;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.HOME);
    }
}
