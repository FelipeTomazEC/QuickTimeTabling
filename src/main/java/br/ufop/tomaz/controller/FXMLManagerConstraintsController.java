package br.ufop.tomaz.controller;


import br.ufop.tomaz.App;
import br.ufop.tomaz.controller.interfaces.AppScreen;
import br.ufop.tomaz.model.Constraint;
import br.ufop.tomaz.services.AppSettings;
import br.ufop.tomaz.util.Screen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FXMLManagerConstraintsController implements Initializable, AppScreen {

    @FXML private MenuBar menubar;

    @FXML private Button btnClose;

    @FXML private Button btnEdit;

    @FXML private Button btnImport;

    @FXML private ListView<Constraint> constraintsListView;

    @FXML private TextField edtType;

    @FXML private TextField edtApplyTo;

    @FXML private Spinner<Integer> spnWeight;

    @FXML private TextArea edtAdditionalInfo;

    @FXML
    private Button btnConfirm;

    private ObservableList<Constraint> constraintList;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        constraintList = FXCollections.observableList(AppSettings.getInstance().getConstraintMap());
        initConstraintListView();
        initButtons();
        initWeightSpinner();
    }

    private void initWeightSpinner() {
        spnWeight.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0, 1)
        );
    }

    private void initButtons() {
        btnEdit.disableProperty().bind(constraintsListView.getSelectionModel()
                .selectedItemProperty()
                .isNull()
                .or(btnConfirm.disabledProperty().not())
        );
    }

    @FXML
    private void edit(){
        Constraint constraint = constraintsListView.getSelectionModel().getSelectedItem();
        spnWeight.setDisable(false);
        spnWeight.getValueFactory().valueProperty().unbind();
        constraint.weightProperty().bind(spnWeight.valueProperty());

        edtAdditionalInfo.setDisable(false);
        constraintsListView.setDisable(true);
        btnConfirm.setDisable(false);
    }

    @FXML
    private void confirmEdition(){
        Constraint constraint = constraintsListView.getSelectionModel().getSelectedItem();
        constraint.weightProperty().unbind();
        spnWeight.setDisable(true);
        edtAdditionalInfo.setDisable(true);
        constraintsListView.setDisable(false);
        btnConfirm.setDisable(true);
        constraintsListView.requestFocus();
    }

    private void initConstraintListView() {
        constraintsListView.setItems(constraintList);
        constraintsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Constraint> call(ListView<Constraint> param) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(Constraint item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item == null || empty){
                            setText("");
                        }else{
                            setText(item.getTypeDescription());
                        }
                    }
                };
            }
        });
        constraintsListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, ov, nv) -> {
                    if(ov != null){
                        edtAdditionalInfo.textProperty().unbindBidirectional(ov.infoProperty());
                    }
                    edtType.textProperty().bind(nv.typeProperty());
                    edtAdditionalInfo.textProperty().bindBidirectional(nv.infoProperty());
                    edtApplyTo.textProperty().bind(nv.applyToProperty());
                    spnWeight.getValueFactory().valueProperty().bind(nv.weightProperty().asObject());
        });
    }

    @Override
    public void close() throws IOException {
        App.setScreen(Screen.HOME);
        AppSettings.getInstance().setConstraintMap(
                constraintList.stream()
                        .collect(Collectors.toMap(Constraint::getType, Function.identity()))
        );
        AppSettings.getInstance().saveSettings();
    }
}
