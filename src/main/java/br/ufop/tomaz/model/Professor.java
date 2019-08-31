package br.ufop.tomaz.model;

import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Access(AccessType.PROPERTY)
@NamedQuery(name = "Professor.findAll", query = "SELECT p FROM Professor p")

public class Professor implements Serializable, Editable {


    private LongProperty id;
    private StringProperty name;
    private StringProperty tag;
    private IntegerProperty workload;
    private DoubleProperty priority;
    private IntegerProperty preferredMinNumberOfWorkingDays;
    private IntegerProperty preferredMaxNumberOfWorkingDays;

    @ElementCollection
    @Access(AccessType.FIELD)
    @CollectionTable(name = "professor_unavailabilities", joinColumns = @JoinColumn(name = "professor_id"))
    private List<Unavailability> unavailabilities;

    @ElementCollection
    @CollectionTable(name = "undesired_pattern", joinColumns = @JoinColumn(name = "professor_id"))
    @Access(AccessType.FIELD)
    private List<UndesiredPattern> undesiredPatterns;


    public Professor() {
        //empty constructor
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.tag = new SimpleStringProperty();
        this.priority = new SimpleDoubleProperty();
        this.workload = new SimpleIntegerProperty();
        this.preferredMaxNumberOfWorkingDays = new SimpleIntegerProperty();
        this.preferredMinNumberOfWorkingDays = new SimpleIntegerProperty();
        this.undesiredPatterns = new ArrayList<>();
        this.unavailabilities = new ArrayList<>();
    }

    public Professor(String name,
                     String tag,
                     Integer workload,
                     Double priority,
                     Integer preferredMinNumberOfWorkingDays,
                     Integer preferredMaxNumberOfWorkingDays,
                     List<Unavailability> unavailabilities,
                     List<UndesiredPattern> undesiredPatterns
    ) {
        this.name = new SimpleStringProperty(name);
        this.tag = new SimpleStringProperty(tag);
        this.workload = new SimpleIntegerProperty(workload);
        this.priority = new SimpleDoubleProperty(priority);
        this.preferredMinNumberOfWorkingDays = new SimpleIntegerProperty(preferredMinNumberOfWorkingDays);
        this.preferredMaxNumberOfWorkingDays = new SimpleIntegerProperty(preferredMaxNumberOfWorkingDays);
        this.undesiredPatterns = undesiredPatterns;
        this.unavailabilities = unavailabilities;
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

    public String getTag() {
        return tag.get();
    }

    public StringProperty tagProperty() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag.set(tag);
    }

    public int getWorkload() {
        return workload.get();
    }

    public IntegerProperty workloadProperty() {
        return workload;
    }

    public void setWorkload(int workload) {
        this.workload.set(workload);
    }

    public double getPriority() {
        return priority.get();
    }

    public DoubleProperty priorityProperty() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority.set(priority);
    }

    public int getPreferredMinNumberOfWorkingDays() {
        return preferredMinNumberOfWorkingDays.get();
    }

    public IntegerProperty preferedMinNumberOfWorkingDaysProperty() {
        return preferredMinNumberOfWorkingDays;
    }

    public void setPreferredMinNumberOfWorkingDays(int preferredMinNumberOfWorkingDays) {
        this.preferredMinNumberOfWorkingDays.set(preferredMinNumberOfWorkingDays);
    }

    public int getPreferredMaxNumberOfWorkingDays() {
        return preferredMaxNumberOfWorkingDays.get();
    }

    public IntegerProperty preferredMaxNumberOfWorkingDaysProperty() {
        return preferredMaxNumberOfWorkingDays;
    }

    public void setPreferredMaxNumberOfWorkingDays(int preferredMaxNumberOfWorkingDays) {
        this.preferredMaxNumberOfWorkingDays.set(preferredMaxNumberOfWorkingDays);
    }

    public IntegerProperty preferredMinNumberOfWorkingDaysProperty() {
        return preferredMinNumberOfWorkingDays;
    }


    public List<Unavailability> getUnavailabilities() {
        return unavailabilities;
    }

    public void setUnavailabilities(List<Unavailability> unavailabilities) {
        this.unavailabilities = unavailabilities;
    }

    public List<UndesiredPattern> getUndesiredPatterns() {
        return undesiredPatterns;
    }

    public void setUndesiredPatterns(List<UndesiredPattern> undesiredPatterns) {
        this.undesiredPatterns = undesiredPatterns;
    }

    @Override
    public String toString() {
        return getName();
    }

}
