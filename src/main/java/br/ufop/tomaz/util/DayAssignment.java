package br.ufop.tomaz.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import br.ufop.tomaz.model.EventAssignment;

import java.util.List;

public class DayAssignment {

    private StringProperty description;
    private List<EventAssignment> assignments;

    public DayAssignment(String description, List<EventAssignment> assignments) {
        this.description = new SimpleStringProperty(description);
        this.assignments = assignments;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public List<EventAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<EventAssignment> assignments) {
        this.assignments = assignments;
    }
}
