package br.ufop.tomaz;

import br.ufop.tomaz.controller.interfaces.EditScreen;
import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.Editable;
import br.ufop.tomaz.util.Screen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App extends Application {

    private static Stage stage;
    public static final String APP_DIRECTORY = System.getProperty("user.home")
            .concat(File.separator)
            .concat("Quick Time Tabling");

    public static void main(String[] args) {
        if(!Files.exists(Path.of(APP_DIRECTORY))){
            createUserDirectories();
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        setScreen(Screen.ADD_EVENT);
    }

    public static void setScreen(Screen screen) throws IOException {
        Parent newScreen = FXMLLoader.load(App.class.getResource(screen.getPath()));
        loadNewScene(newScreen);
    }

    public static void openEditScene(Editable editable) throws IOException {
        String [] splitClassName = editable.getClass().getName().split("\\.");
        String screenFxmlName = "ADD_".concat(splitClassName[splitClassName.length - 1]).toUpperCase();
        Screen screen = Screen.valueOf(screenFxmlName);
        FXMLLoader loader = new FXMLLoader(App.class.getResource(screen.getPath()));
        Parent root = loader.load();
        EditScreen editScreen = loader.getController();
        editScreen.loadEditable(editable);
        loadNewScene(root);
    }

    private static void loadNewScene(Parent root){
        Scene oldScene = stage.getScene();
        double width = oldScene != null ? oldScene.getWidth() : 800.0;
        double height = oldScene != null ? oldScene.getHeight() : 560.0;
        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.show();
    }

    public static Window getWindow(){
        return stage.getOwner();
    }

    private static void createUserDirectories(){
        File dir = new File(APP_DIRECTORY);
        if (dir.mkdirs()) {
            System.out.println("Directories created successfully!");
        } else {
            System.out.println("There was an error creating directories...");
            System.out.println("Please try again!");
        }
    }
}
