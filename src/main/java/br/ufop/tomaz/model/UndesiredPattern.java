package br.ufop.tomaz.model;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

@Embeddable
public class UndesiredPattern implements Serializable {

    @Id
    private int id;
    private char[] pattern;

    public UndesiredPattern() {

    }

    public UndesiredPattern(char[] pattern) {
        this.pattern = pattern;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public char[] getPattern() {
        return pattern;
    }

    public void setPattern(char[] pattern) {
        this.pattern = pattern;
    }
}
