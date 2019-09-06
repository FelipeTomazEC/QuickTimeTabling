package br.ufop.tomaz.util;

import br.ufop.tomaz.model.Event;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import br.ufop.tomaz.model.EventAssignment;
import br.ufop.tomaz.services.AppSettings;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.*;

public class TimeTablingGrid extends GridPane {

    private ObservableList<EventAssignment> items = FXCollections.observableArrayList();
    private final List<String> availableColors = new ArrayList<>();
    private final Map<Long, String> colorsMap = new HashMap<>();

    public TimeTablingGrid() {
        super();
        setVgap(5);
        setHgap(5);
        initRowsAndColumns();
        initTimeTabling();
        initColorsElements();
    }

    private void initTimeTabling() {
        items.addListener((ListChangeListener<EventAssignment>) c -> {
            if(c.next()){
                if(c.wasAdded()){
                    c.getList().forEach(ea -> {
                        Long eventId = ea.getEvent().getId();
                        int row = ea.getTime();
                        int column = ea.getDay();
                        if(!colorsMap.containsKey(eventId)){
                            colorsMap.put(eventId, availableColors.remove(0));
                        }
                        String color = colorsMap.get(eventId);
                        GridItem gridItem = new GridItem(row, column, ea, color);
                        add(gridItem, column, row);
                    });
                }

                if(c.wasRemoved()){
                    c.getRemoved().forEach(ea -> {
                        Long eventId = ea.getEvent().getId();
                        int row = ea.getTime();
                        int column = ea.getDay();
                        if(colorsMap.containsKey(eventId)){
                            String color = colorsMap.remove(eventId);
                            availableColors.add(color);
                        }
                        GridItem item = getItemFrom(row, column);
                        getChildren().remove(item);
                    });
                }
            }

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

    private void initColorsElements(){
        List<String> colors = List.of("#ff5848", "#ffdc50", "#7bf3ff",
                "#6e7dda", "#21dfb9", "#e3f57a", "#ed705a", "#f8a3e6");
        this.availableColors.addAll(colors);
    }

    public ObservableList<EventAssignment> getItems() {
        return items;
    }

    public void setItems(ObservableList<EventAssignment> items) {
        this.items = items;
    }

    public GridItem getItemFrom(int row, int column){
        for(Node item : getChildren()) {
            if (GridPane.getColumnIndex(item) == column && GridPane.getRowIndex(item) == row)
                return (GridItem) item;
        }
        return null;
    }



    private class GridItem extends Label {

        private String color;
        private int rowIndex;
        private int columnIndex;
        private ObjectProperty<EventAssignment> eventAssigment = new SimpleObjectProperty<>(null);

        public GridItem(int rowIndex, int columnIndex, EventAssignment eventAssignment, String color) {
            super();
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            this.color = color;
            setEventAssigment(eventAssignment);
            this.textProperty().bind(this.eventAssigment.get().getEvent().subjectProperty());
            this.setMaxHeight(Double.MAX_VALUE);
            this.setMaxWidth(Double.MAX_VALUE);
            this.setWrapText(true);
            setBackground(new Background(new BackgroundFill(Paint.valueOf(getColor()), null, null)));
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
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

        public int getRowIndex() {
            return this.rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public int getColumnIndex() {
            return this.columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }
    }
}
