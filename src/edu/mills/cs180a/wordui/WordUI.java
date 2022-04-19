package edu.mills.cs180a.wordui;

import java.io.*;
import java.net.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class WordUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(filenameToUrl("Scene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(filenameToUrl("Styles.css").toString());
        stage.setTitle("Word UI Example");
        stage.setScene(scene);
        stage.show();
    }

    // https://stackoverflow.com/a/48110002/631051
    private static URL filenameToUrl(String filename) throws MalformedURLException {
        return new File("src/resources/" + filename).toURI().toURL();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
