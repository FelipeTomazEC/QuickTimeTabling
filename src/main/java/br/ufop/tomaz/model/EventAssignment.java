package br.ufop.tomaz.model;

import javafx.beans.property.*;

import java.io.Serializable;

public class EventAssignment implements Serializable {

    private LongProperty id;
    private ObjectProperty<ClassE> classE;
    private ObjectProperty<Professor> professor;
    private ObjectProperty<Event> event;
    private IntegerProperty day;
    private IntegerProperty time;

    public EventAssignment() {
        this.id = new SimpleLongProperty();
        this.classE = new SimpleObjectProperty<>();
        this.professor = new SimpleObjectProperty<>();
        this.event = new SimpleObjectProperty<>();
        this.day = new SimpleIntegerProperty();
        this.time = new SimpleIntegerProperty();
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public ClassE getClassE() {
        return classE.get();
    }

    public ObjectProperty<ClassE> classEProperty() {
        return classE;
    }

    public void setClassE(ClassE classE) {
        this.classE.set(classE);
    }

    public Professor getProfessor() {
        return professor.get();
    }

    public ObjectProperty<Professor> professorProperty() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor.set(professor);
    }

    public Event getEvent() {
        return event.get();
    }

    public ObjectProperty<Event> eventProperty() {
        return event;
    }

    public void setEvent(Event event) {
        this.event.set(event);
    }

    public int getDay() {
        return day.get();
    }

    public IntegerProperty dayProperty() {
        return day;
    }

    public void setDay(int day) {
        this.day.set(day);
    }

    public int getTime() {
        return time.get();
    }

    public IntegerProperty timeProperty() {
        return time;
    }

    public void setTime(int time) {
        this.time.set(time);
    }
}
