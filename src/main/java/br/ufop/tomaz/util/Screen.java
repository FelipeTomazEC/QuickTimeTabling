package br.ufop.tomaz.util;

public enum Screen {

    HOME("FXMLHome.fxml"), ADD_EVENT("FXMLAddEvent.fxml"),
    MANAGER_PROFESSORS("FXMLManagerProfessors.fxml"), ADD_CLASSE("FXMLAddClassE.fxml"),
    TIMES("FXMLTimes.fxml"), MANAGER_CLASSES("FXMLManagerClasses.fxml"),
    MANAGER_CONSTRAINTS("FXMLManagerConstraints.fxml"), MANAGER_EVENTS("FXMLManagerEvents.fxml"),
    TIME_TABLING("FXMLTimeTabling.fxml"), ADD_PROFESSOR("FXMLAddProfessor.fxml");

    private final String fxmlName;

    Screen(String fxmlName){
        this.fxmlName = fxmlName;
    }

    public String getPath(){
        String separator = "/";
        return separator.concat("br")
                .concat(separator)
                .concat("ufop")
                .concat(separator)
                .concat("tomaz")
                .concat(separator)
                .concat("fxml")
                .concat(separator)
                .concat(fxmlName);
    }
}
