package com.gabriel.account.utilControllers;

import com.gabriel.account.model.Account;
import com.gabriel.account.serviceImpl.AccountService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Setter
public class DeleteAccountController implements Initializable {
    @FXML
    public TextField tfId;
    @FXML
    public TextField tfName;
    @FXML
    public TextField tfDesc;
    @FXML
    public TextField tfUom; // Account Type name
    @FXML
    public TextField tfBalance;

    @Setter
    Stage stage;
    @Setter
    Scene parentScene;
    @Setter
    AccountService accountService;
    @Setter
    AccountManController controller;

    public void refresh() {
        Account account = AccountManController.selectedAccount;
        if (account != null) {
            tfId.setText(Integer.toString(account.getId()));
            tfName.setText(account.getName());
            tfDesc.setText(account.getDescription());
            tfUom.setText(account.getAccountTypeName());
            tfBalance.setText(Double.toString(account.getBalance()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("DeleteAccountController: initialize");
        try {
            refresh();
        } catch (Exception ex) {
            System.out.println("DeleteAccountController init: " + ex.getMessage());
        }
    }

    public void onBack(ActionEvent actionEvent) {
        System.out.println("DeleteAccountController:onBack ");
        Node node = ((Node) (actionEvent.getSource()));
        Window window = node.getScene().getWindow();
        window.hide();

        stage.setScene(parentScene);
        stage.show();
    }

    public void onSubmit(ActionEvent actionEvent) {
        try {
            Account account = toObject(true);
            AccountService.getService().delete(account.getId());
            controller.refresh();
            controller.clearControlTexts();
            Node node = ((Node) (actionEvent.getSource()));
            Window window = node.getScene().getWindow();
            window.hide();
            stage.setTitle("Manage Account");
            stage.setScene(parentScene);
            stage.show();
        } catch (Exception e) {
            String message = "Error encountered deleting account";
            showErrorDialog(message, e.getMessage());
        }
    }

    protected Account toObject(boolean isEdit) {
        Account account = new Account();
        try {
            if (isEdit) {
                account.setId(Integer.parseInt(tfId.getText()));
            }
            account.setName(tfName.getText());
            account.setDescription(tfDesc.getText());
        } catch (Exception e) {
            showErrorDialog("Error", e.getMessage());
        }
        return account;
    }

    public void showErrorDialog(String message, String addtlMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(addtlMessage)));
        alert.showAndWait();
    }
}
