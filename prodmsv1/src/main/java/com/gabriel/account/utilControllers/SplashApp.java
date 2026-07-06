package com.gabriel.account.utilControllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.IOException;

public class SplashApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("SplashApp:start ");
        FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("splash-view.fxml"));
        Parent root = fxmlLoader.load();
        SplashController splashController = fxmlLoader.getController();
        splashController.setStage(stage);
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("/css/splash.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Banking Application!");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(540);
        stage.setMaximized(true);
        stage.show();
    }
}
