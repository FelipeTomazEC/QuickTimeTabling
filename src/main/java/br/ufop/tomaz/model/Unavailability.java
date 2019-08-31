package br.ufop.tomaz.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Unavailability implements Serializable {


    private int day;
    private int time;
    private int type; //1 for hard; 0 for soft
    public static final int HARD_UNAVAILABILITY = 1;
    public static final int SOFT_UNAVAILABILITY = 0;

    public Unavailability(int day, int time, int type) {
        this.day = day;
        this.time = time;
        this.type = type;
    }

    public Unavailability() {
        //empty constructor
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
