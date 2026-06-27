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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clearControlTexts() {
        tfName.setText("");
        tfDesc.setText("");
        tfBalance.setText("0.0");
        cbUom.getSelectionModel().clearSelection();
    }

    public void onNext(ActionEvent actionEvent) {
        onBack(actionEvent);
    }

    public void onSubmit(ActionEvent actionEvent) throws Exception {
        Account account = new Account();
        account.setName(tfName.getText());
        account.setDescription(tfDesc.getText());

        double balance = 0.0;
        try {
            balance = Double.parseDouble(tfBalance.getText());
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
        }
    }

    public void onBack(ActionEvent actionEvent) {
        System.out.println("CreateAccountController:onBack ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();

        stage.setScene(parentScene);
        stage.show();
    }
}