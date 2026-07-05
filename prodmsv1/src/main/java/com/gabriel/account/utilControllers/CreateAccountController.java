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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Setter
public class CreateAccountController implements Initializable {
    @Setter
    AccountManController accountManController;
    @FXML
    public TextField tfName;
    @FXML
    public TextField tfDesc;
    @FXML
    public TextField tfBalance;
    public TextField tfPhoto;
    @FXML
    public TextField tfEmail;
    @FXML
    public PasswordField tfPassword;
    @FXML
    private ComboBox<AccountType> cbUom; // Account Type combo box
    public Button btnSubmit;
    public Button btnNext;
    public Button btnPhoto;

    @Setter
    Stage stage;
    @Setter
    Scene parentScene;
    @Setter
    AccountService accountService;
    @Setter
    AccountTypeService accountTypeService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("CreateAccountController: initialize");

        try {
            AccountType[] types = AccountTypeService.getService().getAccountTypes();
            cbUom.getItems().clear();
            cbUom.getItems().addAll(types);

            tfName.setText("");
            tfDesc.setText("");
            tfBalance.setText("0.0");
            tfEmail.setText("");
            tfPassword.setText("");
            setupNumericField(tfBalance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clearControlTexts() {
        tfName.setText("");
        tfDesc.setText("");
        tfBalance.setText("0.0");
        tfEmail.setText("");
        tfPassword.setText("");
        cbUom.getSelectionModel().clearSelection();
    }

    public void onNext(ActionEvent actionEvent) {
        onBack(actionEvent);
    }

    public void onSubmit(ActionEvent actionEvent) throws Exception {
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
            account = accountService.create(account);
            accountManController.refresh();
            onBack(actionEvent);
        } catch (Exception ex) {
            System.out.println("CreateAccountController:onSubmit Error: " + ex.getMessage());
            showAlert("Error", "Failed to create account: " + ex.getMessage(), Alert.AlertType.ERROR);
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
        System.out.println("CreateAccountController:onBack ");
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