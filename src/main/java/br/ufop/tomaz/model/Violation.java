package br.ufop.tomaz.model;

import javafx.beans.property.*;

import java.io.Serializable;

public class Violation implements Serializable {

    private ObjectProperty<Professor> professor;
    private StringProperty type;
    private IntegerProperty number;
    private IntegerProperty weight;

    public Violation(Professor professor, String type, Integer number, Integer weight) {
        this.professor = new SimpleObjectProperty<>(professor);
        this.type = new SimpleStringProperty(type);
        this.number = new SimpleIntegerProperty(number);
        this.weight = new SimpleIntegerProperty(weight);
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

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public int getNumber() {
        return number.get();
    }

    public IntegerProperty numberProperty() {
        return number;
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public int getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }
}
