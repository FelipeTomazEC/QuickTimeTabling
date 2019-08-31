package br.ufop.tomaz.util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Time {

    public static final int UNAVAILABLE = -1;
    public static final int AVAILABLE = 1;
    public static final int UNDESIRED = 0;

    private StringProperty time;
    private IntegerProperty availability;

    public Time(String time, int availability) {
        this.time = new SimpleStringProperty(time);
        this.availability = new SimpleIntegerProperty(availability);
    }

    public Time() {
        this.time = new SimpleStringProperty("Time");
        this.availability = new SimpleIntegerProperty(AVAILABLE);
    }


    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public int getAvailability() {
        return availability.get();
    }

    public IntegerProperty availabilityProperty() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability.set(availability);
    }

}