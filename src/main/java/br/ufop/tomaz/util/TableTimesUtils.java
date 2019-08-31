package br.ufop.tomaz.util;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import br.ufop.tomaz.model.Unavailability;
import br.ufop.tomaz.services.AppSettings;

import java.util.ArrayList;
import java.util.List;

public class TableTimesUtils {

    public static final int PROFESSORS_TABLE_TYPE = 0;
    public static final int CLASSES_TABLE_TYPE = 1;
    private int tableType;

    public TableTimesUtils(int tableType) {
        this.tableType = tableType;
    }

    public void prepareTableTimes(TableView<Day> tableView) {
        List<TableColumn<Day, Integer>> tableColumns = new ArrayList<>();
        TableColumn<Day, String> descriptionColumn = new TableColumn<>("Day");
        descriptionColumn.setSortable(false);
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(param -> new DayDescriptionTableCell());
        List<String> times = AppSettings.getInstance().getTimesList();
        for (int i = 0; i < times.size(); i++) {
            TableColumn<Day, Integer> tabTime = new TableColumn<>();
            final int index = i;
            tabTime.setCellValueFactory(cellData ->
                    cellData.getValue()
                            .getTimes()
                            .get(index)
                            .availabilityProperty()
                            .asObject());
            tabTime.setCellFactory(param -> new AvailabilityTableCell());
            tabTime.setSortable(false);
            initTableHeader(tabTime, times.get(i));
            tableColumns.add(tabTime);
        }
        tableView.getColumns().add(descriptionColumn);
        tableView.getColumns().addAll(tableColumns);
    }

    private void initTableHeader(TableColumn<Day, Integer> tableColumn, String textHeader) {
        Label header = new Label(textHeader);
        header.setMaxWidth(Double.MAX_VALUE);
        tableColumn.setGraphic(header);
        header.setOnMouseClicked(event -> {
            List<Day> days = tableColumn.getTableView().getItems();
            final int index = tableColumn.getTableView().getColumns().indexOf(tableColumn) - 1;
            if (isAllInSameState(days, index)) {
                int oldAvailability = days.get(0).getTimes().get(index).getAvailability();
                days.forEach(day -> day.getTimes().get(index).setAvailability(getNewState(oldAvailability)));
            } else {
                days.forEach(day -> day.getTimes().get(index).setAvailability(Time.AVAILABLE));
            }
        });
    }

    private boolean isAllInSameState(List<Day> days, final int tIndex) {
        for (int i = 1; i < days.size(); i++) {
            Time t1 = days.get(i).getTimes().get(tIndex);
            Time t2 = days.get(i - 1).getTimes().get(tIndex);
            if (t1.getAvailability() != t2.getAvailability())
                return false;
        }
        return true;
    }

    private int getNewState(int oldAvailability) {
        if (oldAvailability == Time.AVAILABLE && tableType == PROFESSORS_TABLE_TYPE)
            return Time.UNDESIRED;
        else if (oldAvailability == Time.UNDESIRED || (oldAvailability == Time.AVAILABLE && tableType == CLASSES_TABLE_TYPE))
            return Time.UNAVAILABLE;
        else return Time.AVAILABLE;
    }

    private class DayDescriptionTableCell extends TableCell<Day, String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                disableProperty().setValue(true);
                setGraphic(null);
            } else {
                setText(item);
                setOnMouseClicked((event -> this.changeRowAvailability()));
            }
        }

        private void changeRowAvailability() {
            int dIndex = this.getTableRow().getIndex();
            Day day = this.getTableView().getItems().get(dIndex);
            if (isAllInSameState(day)) {
                int oldAvailability = day.getTimes().get(0).getAvailability();
                day.getTimes().forEach(time -> time.setAvailability(getNewState(oldAvailability)));
            } else {
                day.getTimes().forEach(time -> time.setAvailability(Time.AVAILABLE));
            }

        }

        private boolean isAllInSameState(Day d) {
            for (int i = 1; i < d.getTimes().size(); i++) {
                Time t1 = d.getTimes().get(i);
                Time t2 = d.getTimes().get(i - 1);
                if (t1.getAvailability() != t2.getAvailability())
                    return false;
            }
            return true;
        }
    }

    private class AvailabilityTableCell extends TableCell<Day, Integer> {

        private AvailabilityTableCell() {
            this.setOnMouseClicked(event -> setClickAction());
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setGraphic(null);
                disableProperty().setValue(true);
            } else if (item == Time.AVAILABLE)
                this.setStyle("-fx-background-color: green; -fx-border-color: rgba(0,0,0,0.84)");
            else if (item == Time.UNAVAILABLE)
                this.setStyle("-fx-background-color: red; -fx-border-color: rgba(0,0,0,0.84)");
            else if (item == Time.UNDESIRED)
                this.setStyle("-fx-background-color: yellow; -fx-border-color: rgba(0,0,0,0.84)");
        }

        //Sets the time availability in each cell when clicking
        private void setClickAction() {
            int tIndex = getTableView().getColumns().indexOf(getTableColumn()) - 1;
            int dIndex = getIndex();
            int value = getItem();
            Day day = getTableView().getItems().get(dIndex);
            Time time = day.getTimes().get(tIndex);
            time.setAvailability(getNewState(value));
        }

    }

    public static List<Unavailability> getUnavailabilities(TableView<Day> tableView) {

        List<Unavailability> unavailabilities = new ArrayList<>();

        List<Day> daysList = tableView.getItems();
        for (int i = 0; i < daysList.size(); i++) {
            List<Time> timeList = daysList.get(i).getTimes();
            for (int j = 0; j < timeList.size(); j++) {
                Time time = timeList.get(j);
                if (time.getAvailability() == Time.UNDESIRED)
                    unavailabilities.add(new Unavailability(i + 1, j + 1, Unavailability.SOFT_UNAVAILABILITY));
                if (time.getAvailability() == Time.UNAVAILABLE)
                    unavailabilities.add(new Unavailability(i + 1, j + 1, Unavailability.HARD_UNAVAILABILITY));
            }
        }
        return unavailabilities;
    }

    public static void loadUnavailabilities(TableView<Day> tableView, List<Unavailability> unavailabilities) {
        ObservableList<Day> days = tableView.getItems();
        unavailabilities.forEach(unavailability -> {
            int day = unavailability.getDay() - 1;
            int time = unavailability.getTime() - 1;
            int type = unavailability.getType();
            Day d = days.get(day);
            type = type == Unavailability.HARD_UNAVAILABILITY ? Time.UNAVAILABLE : Time.UNDESIRED;
            d.getTimes().get(time).setAvailability(type);
        });
    }

}
