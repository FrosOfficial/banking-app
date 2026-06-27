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
import javafx.scene.control.ComboBox;
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
    private ComboBox<AccountType> cbUom; // Account Type combo box

    public void refresh() throws Exception {
        Account account = AccountManController.selectedAccount;
        if (account != null) {
            tfId.setText(Integer.toString(account.getId()));
            tfName.setText(account.getName());
            tfDesc.setText(account.getDescription());
            tfBalance.setText(Double.toString(account.getBalance()));
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
            refresh();
        } catch (Exception ex) {
            System.out.println("UpdateAccountController: " + ex.getMessage());
        }
    }

    public void onSubmit(ActionEvent actionEvent) {
        Account account = new Account();
        account.setId(Integer.parseInt(tfId.getText()));
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
            account = AccountService.getService().update(account);
            controller.refresh();
            controller.setControlTexts(account);
            onBack(actionEvent);
        } catch (Exception ex) {
            System.out.println("UpdateAccountController:onSubmit Error: " + ex.getMessage());
        }
    }

    public void onBack(ActionEvent actionEvent) {
        System.out.println("UpdateAccountController:onBack ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();

        stage.setScene(parentScene);
        stage.show();
    }
}
