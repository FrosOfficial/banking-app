package com.gabriel.account.utilControllers;

import com.gabriel.account.model.Account;
import com.gabriel.account.model.Transaction;
import com.gabriel.account.serviceImpl.AccountService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Data;
import lombok.Setter;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

@Data
public class CustomerController implements Initializable {
    @Setter
    Stage stage;

    @FXML
    private TextField tfSearchId;
    @FXML
    private VBox vboxSearch;
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

    public void loadCustomerAccount(Account account) {
        if (vboxSearch != null) {
            vboxSearch.setVisible(false);
            vboxSearch.setManaged(false);
        }
        currentAccount = account;
        lblOwner.setText("Owner: " + account.getName());
        lblType.setText("Account Type: " + account.getAccountTypeName());
        lblBalance.setText("Balance: ₱" + account.getBalance());
        lblStatus.setText("Account loaded successfully.");
    }

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
    public void onTransactionHistory(ActionEvent actionEvent) {
        if (currentAccount == null) {
            showAlert("Error", "Please load an account first.", Alert.AlertType.ERROR);
            return;
        }
        try {
            Transaction[] txs = AccountService.getService().getTransactions(currentAccount.getId());
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Transaction History");
            dialog.setHeaderText("Transaction History for " + currentAccount.getName());
            
            ListView<String> listView = new ListView<>();
            listView.setPrefWidth(320);
            listView.setPrefHeight(250);
            
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            if (txs == null || txs.length == 0) {
                listView.getItems().add("No transactions found.");
            } else {
                for (Transaction tx : txs) {
                    String timeStr = tx.getCreated() != null ? df.format(tx.getCreated()) : "-";
                    String amountStr = String.format("₱%,.2f", tx.getAmount());
                    listView.getItems().add(timeStr + " - " + tx.getType() + " - " + amountStr);
                }
            }
            
            dialog.getDialogPane().setContent(listView);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            dialog.getDialogPane().getStylesheets().add(css);
            
            dialog.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to retrieve transactions: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onLogOut(ActionEvent actionEvent) {
        System.out.println("CustomerController:onLogOut ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("login-view.fxml"));
            Parent root = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.setStage(stage);

            Scene scene = new Scene(root, 300, 600);
            String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error returning to login: " + ex.getMessage());
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
