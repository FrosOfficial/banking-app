package com.gabriel.account.utilControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Data;
import lombok.Setter;

@Data
public class UserSelectionController {
    @Setter
    Stage stage;

    @FXML
    public void onAdminClick(ActionEvent actionEvent) {
        System.out.println("UserSelectionController:onAdminClick ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("accountman-view.fxml"));
            Parent root = fxmlLoader.load();
            AccountManController accountManController = fxmlLoader.getController();
            accountManController.setStage(stage);

            Scene scene = new Scene(root, 960, 540);
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Account Management System - Admin");
            stage.setScene(scene);
            stage.setMinWidth(960);
            stage.setMinHeight(540);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error occurred opening admin screen: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void onCustomerClick(ActionEvent actionEvent) {
        System.out.println("UserSelectionController:onCustomerClick ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("customer-view.fxml"));
            Parent root = fxmlLoader.load();
            CustomerController customerController = fxmlLoader.getController();
            customerController.setStage(stage);

            Scene scene = new Scene(root, 960, 540);
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Banking Transactions - Customer");
            stage.setScene(scene);
            stage.setMinWidth(960);
            stage.setMinHeight(540);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error occurred opening customer screen: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
