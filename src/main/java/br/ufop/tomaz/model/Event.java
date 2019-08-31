package br.ufop.tomaz.model;

import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Access(AccessType.PROPERTY)
@NamedQuery(name = "Event.findAll", query = "SELECT E FROM Event E")
public class Event implements Serializable, Editable {


    private LongProperty id;

    private StringProperty subject;
    private StringProperty tag;
    private IntegerProperty minGapBetweenMeetings;
    private IntegerProperty maxGapBetweenMeetings;
    private IntegerProperty duration;
    private StringProperty split;

    @ManyToOne
    @Access(AccessType.FIELD)
    @JoinColumn(name = "class_id")
    private ClassE classE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "compatible_professors", joinColumns = @JoinColumn(name = "event_id"))
    @Access(AccessType.FIELD)
    private List<ProfessorWeight> professorWeights;

    public Event() {
        this.id = new SimpleLongProperty();
        this.subject = new SimpleStringProperty("");
        this.tag = new SimpleStringProperty("");
        this.minGapBetweenMeetings = new SimpleIntegerProperty(0);
        this.maxGapBetweenMeetings = new SimpleIntegerProperty(0);
        this.duration = new SimpleIntegerProperty(0);
        this.split = new SimpleStringProperty("");
        this.classE = new ClassE();
        this.professorWeights = new ArrayList<>();
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.setValue(id);
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
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

    public int getMinGapBetweenMeetings() {
        return minGapBetweenMeetings.get();
    }

    public IntegerProperty minGapBetweenMeetingsProperty() {
        return minGapBetweenMeetings;
    }

    public void setMinGapBetweenMeetings(int minGapBetweenMeetings) {
        this.minGapBetweenMeetings.set(minGapBetweenMeetings);
    }

    public int getMaxGapBetweenMeetings() {
        return maxGapBetweenMeetings.get();
    }

    public IntegerProperty maxGapBetweenMeetingsProperty() {
        return maxGapBetweenMeetings;
    }

    public void setMaxGapBetweenMeetings(int maxGapBetweenMeetings) {
        this.maxGapBetweenMeetings.set(maxGapBetweenMeetings);
    }

    public int getDuration() {
        return duration.get();
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration.set(duration);
    }

    public String getSplit() {
        return split.get();
    }

    public StringProperty splitProperty() {
        return split;
    }

    public void setSplit(String split) {
        this.split.set(split);
    }

    public ClassE getClassE() {
        return classE;
    }

    public void setClassE(ClassE classE) {
        this.classE = classE;
    }

    public List<ProfessorWeight> getProfessorWeights() {
        return professorWeights;
    }

    public void setProfessorWeights(List<ProfessorWeight> professorWeights) {
        this.professorWeights = professorWeights;
    }

    @Override
    public String toString() {
        return getSubject();
    }
}
