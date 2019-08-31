package br.ufop.tomaz.controller.interfaces;

import javafx.fxml.FXML;

import java.io.IOException;

public interface AppScreen {

    @FXML
    void close() throws IOException;
}
