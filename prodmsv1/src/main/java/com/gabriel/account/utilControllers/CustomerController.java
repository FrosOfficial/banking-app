package com.gabriel.account.utilControllers;

import com.gabriel.account.model.Account;
import com.gabriel.account.serviceImpl.AccountService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Data;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Data
public class CustomerController implements Initializable {
    @Setter
    Stage stage;

    @FXML
    private TextField tfSearchId;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblOwner;
    @FXML
    private Label lblType;
    @FXML
    private Label lblBalance;
    @FXML
    private TextField tfAmount;

    private Account currentAccount = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearDetails();
    }

    private void clearDetails() {
        lblStatus.setText("Enter Account ID and click Load.");
        lblOwner.setText("Owner: -");
        lblType.setText("Account Type: -");
        lblBalance.setText("Balance: -");
        currentAccount = null;
    }

    @FXML
    public void onLoadAccount(ActionEvent actionEvent) {
        String idStr = tfSearchId.getText().trim();
        if (idStr.isEmpty()) {
            showAlert("Error", "Please enter an Account ID.", Alert.AlertType.ERROR);
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Account account = AccountService.getService().getAccount(id);
            if (account == null) {
                showAlert("Not Found", "Account ID " + id + " does not exist.", Alert.AlertType.ERROR);
                clearDetails();
            } else {
                currentAccount = account;
                lblOwner.setText("Owner: " + account.getName());
                lblType.setText("Account Type: " + account.getAccountTypeName());
                lblBalance.setText("Balance: ₱" + account.getBalance());
                lblStatus.setText("Account loaded successfully.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Account ID must be a number.", Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Error", "Failed to retrieve account: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onDeposit(ActionEvent actionEvent) {
        if (currentAccount == null) {
            showAlert("Error", "Please load an account first.", Alert.AlertType.ERROR);
            return;
        }
        String amountStr = tfAmount.getText().trim();
        if (amountStr.isEmpty()) {
            showAlert("Error", "Please enter a deposit amount.", Alert.AlertType.ERROR);
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert("Error", "Amount must be positive.", Alert.AlertType.ERROR);
                return;
            }
            Account updated = AccountService.getService().deposit(currentAccount.getId(), amount);
            currentAccount = updated;
            lblBalance.setText("Balance: ₱" + currentAccount.getBalance());
            tfAmount.clear();
            showAlert("Success", "₱" + amount + " deposited successfully.\nyour money has been deposited to your account", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Amount must be a valid number.", Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Error", "Deposit failed: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onWithdraw(ActionEvent actionEvent) {
        if (currentAccount == null) {
            showAlert("Error", "Please load an account first.", Alert.AlertType.ERROR);
            return;
        }
        String amountStr = tfAmount.getText().trim();
        if (amountStr.isEmpty()) {
            showAlert("Error", "Please enter a withdraw amount.", Alert.AlertType.ERROR);
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert("Error", "Amount must be positive.", Alert.AlertType.ERROR);
                return;
            }
            if (currentAccount.getBalance() < amount) {
                showAlert("Error", "no money brokie", Alert.AlertType.ERROR);
                return;
            }
            Account updated = AccountService.getService().withdraw(currentAccount.getId(), amount);
            currentAccount = updated;
            lblBalance.setText("Balance: ₱" + currentAccount.getBalance());
            tfAmount.clear();
            showAlert("Success", "₱" + amount + " withdrawn successfully.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Amount must be a valid number.", Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Error", "Withdraw failed: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onBalanceInquiry(ActionEvent actionEvent) {
        if (currentAccount == null) {
            showAlert("Error", "Please load an account first.", Alert.AlertType.ERROR);
            return;
        }
        showAlert("Information", "Balance for " + currentAccount.getName() + ": ₱" + currentAccount.getBalance(), Alert.AlertType.INFORMATION);
    }

    @FXML
    public void onLogOut(ActionEvent actionEvent) {
        System.out.println("CustomerController:onLogOut ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("user-selection-view.fxml"));
            Parent root = fxmlLoader.load();
            UserSelectionController userSelectionController = fxmlLoader.getController();
            userSelectionController.setStage(stage);

            Scene scene = new Scene(root, 300, 600);
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Role Selection");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error returning to selection: " + ex.getMessage());
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
