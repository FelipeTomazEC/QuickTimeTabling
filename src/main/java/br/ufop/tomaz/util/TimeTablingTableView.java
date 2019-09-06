package br.ufop.tomaz.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.model.EventAssignment;
import br.ufop.tomaz.services.AppSettings;

import java.util.*;
import java.util.stream.Collectors;

public class TimeTablingTableView extends TableView<DayAssignment> {

    private List<String> availableColors;
    private Map<Long, String> assignmentColorMap;
    private Map<Long, EventAssignment> eventAssignmentMap;
    private static DataFormat dataFormat = new DataFormat("eventAssignment");

    public TimeTablingTableView() {
        super();
        eventAssignmentMap = new HashMap<>();
        initColors();
        colorItemsCellsConfig();
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.getStyleClass().add("time-tabling");
        this.getStylesheets().add(getClass().getResource("/br/ufop/tomaz/styles/timeTablingStyle.css").toString());
        this.setSelectionModel(null);
        initColumns();
    }

    private void initColumns() {
        TableColumn<DayAssignment, String> dayColumn = new TableColumn<>("Day");
        dayColumn.setCellValueFactory((param -> param.getValue().descriptionProperty()));
        getColumns().add(dayColumn);

        //times columns
        List<String> times = AppSettings.getInstance().getTimesList();
        for (int i = 0; i < times.size(); i++) {
            TableColumn<DayAssignment, EventAssignment> col = new TableColumn<>(times.get(i));
            int finalI = i;
            col.setCellValueFactory(param -> {
                List<EventAssignment> assignmentsList = param.getValue()
                        .getAssignments()
                        .stream()
                        .filter(a -> a.getTime() == finalI - 1)
                        .collect(Collectors.toList());

                if(assignmentsList.isEmpty()){
                   return null;
                } else{
                    EventAssignment assignment = assignmentsList.get(0);
                    eventAssignmentMap.put(assignment.getId(), assignment);
                    return new SimpleObjectProperty<>(assignment);
                }
            });
            col.setCellFactory(param -> new TimeTablingCell());
            getColumns().add(col);
        }
    }

    private void initColors() {
        availableColors = new ArrayList<>();
        assignmentColorMap = new HashMap<>();
        List<String> colors = Arrays.asList("#f5f341", "#00ffff", "#ff00ff", "#470092", "#ff9a9a", "#212842");
        availableColors.addAll(colors);
    }

    private void colorItemsCellsConfig() {
        getItems().addListener((ListChangeListener<DayAssignment>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(
                            assignment -> assignment.getAssignments().forEach(ea -> {
                                String color = assignmentColorMap.remove(ea.getEvent().getId());
                                availableColors.add(color);

                                System.out.println("assignment color size: " + assignmentColorMap.size());
                                System.out.println("available colors size: " + availableColors.size());
                            })
                    );
                }
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(
                            assignment -> assignment.getAssignments().forEach(ea -> {
                                Event event = ea.getEvent();
                                if(!assignmentColorMap.containsKey(event.getId())){
                                    assignmentColorMap.put(event.getId(), availableColors.remove(0));
                                }
                            })
                    );
                    System.out.println("assignment color size: " + assignmentColorMap.size());
                    System.out.println("available colors size: " + availableColors.size());
                }
            }
        });
    }

    private class TimeTablingCell extends TableCell<DayAssignment, EventAssignment> {

        private Label label;

        public TimeTablingCell() {
            this.label = new Label();
            configureDragAndDrop();
        }

        @Override
        protected void updateItem(EventAssignment item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                label.setText("");
                setGraphic(null);
            } else {
                label.setText(item.getEvent().getSubject());
                label.setWrapText(true);
                setGraphic(label);
                String color = assignmentColorMap.get(item.getEvent().getId());
                setBackground(new Background(new BackgroundFill(Paint.valueOf(color), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }

        private void configureDragAndDrop() {
            this.setOnDragDetected(event -> {
                System.out.println("On drag detected.");
            });

            this.setOnDragOver(event -> {
                if (this != event.getGestureSource()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            });

            this.setOnDragDropped(event -> {
                System.out.println("On drag dropped.");
                Dragboard dragboard = event.getDragboard();
                TimeTablingCell source = (TimeTablingCell) event.getGestureSource();
                boolean success = false;
                if (dragboard.hasContent(dataFormat)) {
                    EventAssignment assignment = eventAssignmentMap.get(getItem().getId());
                    setItem((EventAssignment) dragboard.getContent(dataFormat));
                    source.setItem(assignment);
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });

            this.setOnDragDone(event -> {
                System.out.println("On drag done");
                event.consume();
            });

            this.setOnDragEntered(event -> {
                System.out.println("On drag entered.");
                event.consume();
            });
            this.setOnDragExited(event -> {
                System.out.println("On drag exited.");
                String color = assignmentColorMap.get(getItem().getEvent());
                setBackground(new Background(new BackgroundFill(Paint.valueOf(color), CornerRadii.EMPTY, Insets.EMPTY)));
            });
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }
    }

    //TODO - SWAP EVENT ASSIGNMENTS INSTEAD JUST STRINGS WHEN DRAG AND DROP CELLS TABLE

}
