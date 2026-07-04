package com.gabriel.account.utilControllers;

import com.gabriel.account.model.Account;
import com.gabriel.account.serviceImpl.AccountService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Setter;

public class LoginController {

    @FXML
    private TextField tfEmail;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private TextField tfPasswordPlain;
    @FXML
    private CheckBox chkShowPassword;

    @Setter
    private Stage stage;

    @FXML
    public void initialize() {
        if (tfPassword != null && tfPasswordPlain != null) {
            tfPasswordPlain.textProperty().bindBidirectional(tfPassword.textProperty());
        }
    }

    @FXML
    public void onToggleShowPassword(ActionEvent event) {
        if (chkShowPassword.isSelected()) {
            tfPasswordPlain.setVisible(true);
            tfPasswordPlain.setManaged(true);
            tfPassword.setVisible(false);
            tfPassword.setManaged(false);
            tfPasswordPlain.requestFocus();
        } else {
            tfPassword.setVisible(true);
            tfPassword.setManaged(true);
            tfPasswordPlain.setVisible(false);
            tfPasswordPlain.setManaged(false);
            tfPassword.requestFocus();
        }
    }

    @FXML
    public void onLogin(ActionEvent actionEvent) {
        String email = tfEmail.getText().trim();
        String password = tfPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Required Fields", "Please enter both email and password.", Alert.AlertType.WARNING);
            return;
        }

        // 1. Check for Admin Login
        if (email.equalsIgnoreCase("admin@bank.com") && password.equals("AdminPass123!")) {
            System.out.println("LoginController: Admin logged in successfully");
            transitionToAdmin(actionEvent);
            return;
        }

        // 2. Check for Customer Login
        try {
            Account[] accounts = AccountService.getService().getAccounts();
            Account matchingAccount = null;
            if (accounts != null) {
                for (Account acc : accounts) {
                    if (acc.getEmail() != null && acc.getEmail().equalsIgnoreCase(email) &&
                        acc.getPassword() != null && acc.getPassword().equals(password)) {
                        matchingAccount = acc;
                        break;
                    }
                }
            }

            if (matchingAccount != null) {
                System.out.println("LoginController: Customer logged in: " + matchingAccount.getName());
                transitionToCustomer(actionEvent, matchingAccount);
            } else {
                showAlert("Login Failed", "Invalid email or password.", Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            System.out.println("LoginController: Error fetching accounts: " + ex.getMessage());
            ex.printStackTrace();
            showAlert("Error", "Could not connect to the backend server. Make sure sbprodms is running.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onCancel(ActionEvent actionEvent) {
        // Redirect back to splash view
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("splash-view.fxml"));
            Parent root = fxmlLoader.load();
            SplashController splashController = fxmlLoader.getController();
            splashController.setStage(stage);

            Scene scene = new Scene(root, 300, 600);
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Banking Application!");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error returning to splash: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void transitionToAdmin(ActionEvent actionEvent) {
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("accountman-view.fxml"));
            Parent root = fxmlLoader.load();
            AccountManController accountManController = fxmlLoader.getController();
            accountManController.setStage(stage);

            Scene scene = new Scene(root, 350, 650);
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Account Management System - Admin");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error opening admin dashboard: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void transitionToCustomer(ActionEvent actionEvent, Account customerAccount) {
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("customer-view.fxml"));
            Parent root = fxmlLoader.load();
            CustomerController customerController = fxmlLoader.getController();
            customerController.setStage(stage);
            customerController.loadCustomerAccount(customerAccount);

            Scene scene = new Scene(root, 350, 650);
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Banking Transactions - " + customerAccount.getName());
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error opening customer dashboard: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
