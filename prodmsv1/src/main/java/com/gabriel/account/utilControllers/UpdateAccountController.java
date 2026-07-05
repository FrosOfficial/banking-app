package com.gabriel.account.utilControllers;

import com.gabriel.account.model.Account;
import com.gabriel.account.model.AccountType;
import com.gabriel.account.serviceImpl.AccountService;
import com.gabriel.account.serviceImpl.AccountTypeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Setter
public class UpdateAccountController implements Initializable {
    @Setter
    Stage stage;
    @Setter
    Scene parentScene;
    @Setter
    AccountManController controller;

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfDesc;
    @FXML
    private TextField tfBalance;
    @FXML
    private TextField tfEmail;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private TextField tfPasswordPlain;
    @FXML
    private CheckBox chkShowPassword;
    @FXML
    private ComboBox<AccountType> cbUom; // Account Type combo box

    public void refresh() throws Exception {
        Account account = AccountManController.selectedAccount;
        if (account != null) {
            tfId.setText(Integer.toString(account.getId()));
            tfName.setText(account.getName());
            tfDesc.setText(account.getDescription());
            tfBalance.setText(Double.toString(account.getBalance()));
            tfEmail.setText(account.getEmail() != null ? account.getEmail() : "");
            tfPassword.setText(account.getPassword() != null ? account.getPassword() : "");
            setupNumericField(tfBalance);
            cbUom.getItems().clear();
            AccountType[] types = AccountTypeService.getService().getAccountTypes();
            cbUom.getItems().addAll(types);
            cbUom.getSelectionModel().select(AccountTypeService.getService().getAccountType(account.getAccountTypeId()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("UpdateAccountController: initialize");
        try {
            if (tfPassword != null && tfPasswordPlain != null) {
                tfPasswordPlain.textProperty().bindBidirectional(tfPassword.textProperty());
            }
            refresh();
        } catch (Exception ex) {
            System.out.println("UpdateAccountController: " + ex.getMessage());
        }
    }

    public void onToggleShowPassword(ActionEvent actionEvent) {
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

    public void onSubmit(ActionEvent actionEvent) {
        String email = tfEmail.getText().trim();
        String password = tfPassword.getText();
        String balanceText = tfBalance.getText().trim();

        if (email.isEmpty() || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }

        if (password.length() < 8 || 
            !password.matches(".*[a-z].*") ||
            !password.matches(".*[A-Z].*") ||
            !password.matches(".*\\d.*") ||
            !password.matches(".*[^a-zA-Z0-9].*")) {
            showAlert("Weak Password", "Password must be at least 8 characters long, containing at least one uppercase letter, one lowercase letter, one digit, and one special character (e.g. _, @, $, !, %, etc.).", Alert.AlertType.ERROR);
            return;
        }

        if (!balanceText.matches("\\d+(\\.\\d{1,2})?")) {
            showAlert("Invalid Balance", "Balance must contain only numbers and optional decimals.", Alert.AlertType.ERROR);
            return;
        }

        Account account = new Account();
        account.setId(Integer.parseInt(tfId.getText()));
        account.setName(tfName.getText());
        account.setDescription(tfDesc.getText());
        account.setEmail(email);
        account.setPassword(password);

        double balance = 0.0;
        try {
            balance = Double.parseDouble(balanceText);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid balance format: " + nfe.getMessage());
        }
        account.setBalance(balance);

        AccountType type = cbUom.getSelectionModel().getSelectedItem();
        if (type != null) {
            account.setAccountTypeId(type.getId());
            account.setAccountTypeName(type.getName());
        }

        try {
            account = AccountService.getService().update(account);
            controller.refresh();
            controller.setControlTexts(account);
            onBack(actionEvent);
        } catch (Exception ex) {
            System.out.println("UpdateAccountController:onSubmit Error: " + ex.getMessage());
            showAlert("Error", "Failed to update account: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupNumericField(TextField field) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                field.setText(oldValue == null ? "" : oldValue);
            }
        });
    }

    public void onBack(ActionEvent actionEvent) {
        System.out.println("UpdateAccountController:onBack ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();

        stage.setScene(parentScene);
        stage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
