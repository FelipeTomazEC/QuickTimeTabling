package br.ufop.tomaz.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Access(AccessType.PROPERTY)
@NamedQuery(name = "ClassE.findAll", query = "SELECT c FROM ClassE c")
public class ClassE implements Serializable, Editable {

    private LongProperty id;
    private StringProperty name;

    @ElementCollection
    @CollectionTable(name = "classes_unavailabilities", joinColumns = @JoinColumn(name = "class_id"))
    @Access(AccessType.FIELD)
    private List<Unavailability> unavailabilities;

    public ClassE(String name, List<Unavailability> unavailabilities) {
        this.name = new SimpleStringProperty(name);
        this.unavailabilities = unavailabilities;
        this.id = new SimpleLongProperty();
    }

    public ClassE() {
        //empty constructor
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.unavailabilities = new ArrayList<>();
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public List<Unavailability> getUnavailabilities() {
        return unavailabilities;
    }

    public void setUnavailabilities(List<Unavailability> unavailabilities) {
        this.unavailabilities = unavailabilities;
    }

    @Override
    public String toString() {
        return getName();
    }
}
