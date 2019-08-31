package br.ufop.tomaz.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import br.ufop.tomaz.model.EventAssignment;
import br.ufop.tomaz.services.AppSettings;

import java.util.List;

public class TimeTablingGrid extends GridPane {

    private ObservableList<EventAssignment> items = FXCollections.observableArrayList();

    public TimeTablingGrid() {
        super();
        setStyle("-fx-background-color: yellow");
        setVgap(5);
        setHgap(5);
        initRowsAndColumns();
        initComponents();
    }

    private void initComponents() {
        items.addListener((ListChangeListener<EventAssignment>) c -> {
            c.getList().forEach(ea -> {
                int row = ea.getTime();
                int column = ea.getDay();
                GridItem gridItem = new GridItem(row, column, ea);
                add(gridItem, column, row);
            });
        });
    }

    private void initRowsAndColumns() {
        List<String> days = AppSettings.getInstance().getDaysList();
        List<String> times = AppSettings.getInstance().getTimesList();
        ColumnConstraints columnConstraints = new ColumnConstraints();
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(10);
        Label firstColumn = new Label("Time");
        add(firstColumn, 0, 0);
        getColumnConstraints().add(columnConstraints);
        getRowConstraints().add(rowConstraints);

        for (int i = 0; i < days.size(); i++) {
            Label columnLabel = new Label(days.get(i));
            add(columnLabel, i + 1, 0);
            getColumnConstraints().add(columnConstraints);
        }

        double rowPercent = 90.0 / times.size();
        for (int i = 0; i < times.size(); i++) {
            Label rowLabel = new Label(times.get(i));
            add(rowLabel, 0, i + 1);
            rowLabel.setWrapText(true);
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPercentHeight(rowPercent);
            rowConstraint.setVgrow(Priority.ALWAYS);
            rowConstraint.setFillHeight(true);
            getRowConstraints().add(rowConstraint);
        }

        final double columnPercent = 100.0 / getColumnConstraints().size();
        getColumnConstraints().forEach(c -> {
            c.setPercentWidth(columnPercent);
            c.setFillWidth(true);
            c.setHgrow(Priority.ALWAYS);
            c.setHalignment(HPos.CENTER);
        });
    }

    public ObservableList<EventAssignment> getItems() {
        return items;
    }

    public void setItems(ObservableList<EventAssignment> items) {
        this.items = items;
    }

    private class GridItem extends Label {

        private int rowIndex;
        private int columnIndex;
        private ObjectProperty<EventAssignment> eventAssigment = new SimpleObjectProperty<>(null);

        public GridItem(int rowIndex, int columnIndex, EventAssignment eventAssignment) {
            super();
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            setEventAssigment(eventAssignment);
            this.textProperty().bind(this.eventAssigment.get().getEvent().subjectProperty());
            this.setMaxHeight(Double.MAX_VALUE);
            this.setMaxWidth(Double.MAX_VALUE);
            this.setWrapText(true);
            setStyle("-fx-background-color: #67ff56");
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public EventAssignment getEventAssigment() {
            return eventAssigment.get();
        }

        public ObjectProperty<EventAssignment> eventAssigmentProperty() {
            return eventAssigment;
        }

        public void setEventAssigment(EventAssignment eventAssigment) {
            this.eventAssigment.set(eventAssigment);
        }

    }
}
