package br.ufop.tomaz.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.stream.Collectors;

public class Day {

    private StringProperty description;

    private List<Time> times;

    public Day(String description, List<String> times) {
        this.description = new SimpleStringProperty(description);
        this.times = times.stream().map(time -> new Time(time, Time.AVAILABLE)).collect(Collectors.toList());
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

    public List<Time> getTimes() {
        return times;
    }

    public void setTimes(List<Time> times) {
        this.times = times;
    }

}
