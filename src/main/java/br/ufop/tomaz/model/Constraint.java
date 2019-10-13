package br.ufop.tomaz.model;

import javafx.beans.property.*;

public class Constraint {
    private ConstraintType type;
    private StringProperty applyTo = new SimpleStringProperty("");
    private StringProperty info = new SimpleStringProperty("");
    private IntegerProperty weight = new SimpleIntegerProperty(0);

    public Constraint(ConstraintType type, String applyTo, String info, Integer weight) {
        this.type = type;
        setApplyTo(applyTo);
        setInfo(info);
        setWeight(weight);
    }

    public String getTypeDescription() {
        return type.getTypeDescription();
    }

    public  ConstraintType getType(){ return this.type; }

    public StringProperty typeProperty(){
        return type.typeDescriptionProperty();
    }

    public void setType(ConstraintType type) {
        this.type = type;
    }

    public String getApplyTo() {
        return applyTo.get();
    }

    public StringProperty applyToProperty() {
        return applyTo;
    }

    public void setApplyTo(String applyTo) {
        this.applyTo.set(applyTo);
    }

    public String getInfo() {
        return info.get();
    }

    public StringProperty infoProperty() {
        return info;
    }

    public void setInfo(String info) {
        this.info.set(info);
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

    public enum ConstraintType {
        UNDESIRED_TIMES("Undesired Times"), UNAVAILABLE_TIMES("Unavailable Times"),
        GAP_BETWEEN_MEETINGS("Gap Between Meetings"), SPLIT("Split"),
        UNDESIRED_PATTERNS("Undesired Patterns"), NUMBER_OF_BUSY_DAYS("Number of Busy Days");

        private final StringProperty typeDescription = new SimpleStringProperty("");

        ConstraintType (String typeDescription){
            setTypeDescription(typeDescription);
        }

        public String getTypeDescription() {
            return typeDescription.get();
        }

        public StringProperty typeDescriptionProperty() {
            return typeDescription;
        }

        public void setTypeDescription(String typeDescription) {
            this.typeDescription.set(typeDescription);
        }
    }
}

