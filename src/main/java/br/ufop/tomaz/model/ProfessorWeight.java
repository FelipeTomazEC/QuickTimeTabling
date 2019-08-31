package br.ufop.tomaz.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class ProfessorWeight implements Serializable {

    @ManyToOne
    @Access(AccessType.FIELD)
    @JoinColumn(name = "professor_id", referencedColumnName = "id")
    private Professor professor;

    private IntegerProperty weight;

    public ProfessorWeight() {
        //empty constructor
        this.weight = new SimpleIntegerProperty(0);
    }

    public ProfessorWeight(Professor professor, Integer weight) {
        this.professor = professor;
        this.weight = new SimpleIntegerProperty(weight);
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
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
