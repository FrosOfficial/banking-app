package com.gabriel.account.utilControllers;

import com.gabriel.account.model.Account;
import com.gabriel.account.serviceImpl.AccountService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Alert;
import lombok.Data;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Data
public class AccountManController implements Initializable {
    @Setter
    Stage stage;
    @Setter
    Scene createViewScene;
    @Setter
    Scene updateViewScene;
    @Setter
    Scene deleteViewScene;

    public TextField tfId;
    public TextField tfName;
    public TextField tfDesc;
    public TextField tfUom; // now serves as Account Type
    public TextField tfBalance;
    public ImageView productImage;
    public BorderPane prodman;

    Image puffy;
    Image wink;

    @FXML
    public Button createButton;
    @FXML
    public Button updateButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button closeButton;

    public static Account selectedAccount;
    @FXML
    private ListView<Account> lvAccounts;

    UpdateAccountController updateProductController;
    DeleteAccountController deleteProductController;
    CreateAccountController createProductController;
    AccountService accountService;

    void refresh() throws Exception {
        accountService = AccountService.getService();
        Account[] accounts = accountService.getAccounts();
        lvAccounts.getItems().clear();
        lvAccounts.getItems().addAll(accounts);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("AccountManController: initialize");
        disableControls();

        try {
            refresh();
            try {
                puffy = new Image(getClass().getResourceAsStream("/images/puffy.gif"));
                wink = new Image(getClass().getResourceAsStream("/images/wink.gif"));
                productImage.setImage(puffy);
            } catch (Exception ex) {
                System.out.println("Error loading image: " + ex.getMessage());
            }
        } catch (Exception ex) {
            showErrorDialog("Message: " + ex.getMessage());
        }
    }

    public void disableControls() {
        tfId.setEditable(false);
        tfName.setEditable(false);
        tfDesc.setEditable(false);
        tfUom.setEditable(false);
        tfBalance.setEditable(false);
    }

    public void setControlTexts(Account account) {
        tfName.setText(account.getName());
        tfDesc.setText(account.getDescription());
        tfUom.setText(account.getAccountTypeName());
        tfBalance.setText(Double.toString(account.getBalance()));
    }

    public void clearControlTexts() {
        tfId.setText("");
        tfName.setText("");
        tfDesc.setText("");
        tfUom.setText("");
        tfBalance.setText("");
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        selectedAccount = lvAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            return;
        }
        tfId.setText(Integer.toString(selectedAccount.getId()));
        setControlTexts(selectedAccount);
        System.out.println("clicked on " + selectedAccount);
    }

    public void onCreate(ActionEvent actionEvent) {
        System.out.println("AccountManController:onCreate ");
        Scene currentScene = prodman.getScene();
        Window window = currentScene.getWindow();
        window.hide();
        try {
            if (createViewScene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("create-account.fxml"));
                Parent root = fxmlLoader.load();
                createProductController = fxmlLoader.getController(); // holds the instance
                createProductController.setStage(this.stage);
                createProductController.setParentScene(currentScene);
                createProductController.setAccountService(accountService);
                createProductController.setAccountManController(this);
                createViewScene = new Scene(root, 300, 600);
                String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
                createViewScene.getStylesheets().add(css);
                stage.setTitle("Manage Account");
                stage.setScene(createViewScene);
                stage.show();
            } else {
                stage.setScene(createViewScene);
                stage.show();
            }
            createProductController.clearControlTexts();
            clearControlTexts();
        } catch (Exception ex) {
            System.out.println("AccountManController onCreate: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void onUpdate(ActionEvent actionEvent) {
        if (selectedAccount == null) {
            showErrorDialog("Please select an account first.");
            return;
        }
        System.out.println("AccountManController:onUpdate ");
        Scene currentScene = prodman.getScene();
        Window window = currentScene.getWindow();
        window.hide();
        try {
            if (updateViewScene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("update-account.fxml"));
                Parent root = fxmlLoader.load();
                updateProductController = fxmlLoader.getController();
                updateProductController.setController(this);
                updateProductController.setStage(this.stage);
                updateProductController.setParentScene(currentScene);
                updateViewScene = new Scene(root, 300, 600);
                String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
                updateViewScene.getStylesheets().add(css);
            } else {
                updateProductController.refresh();
            }
            stage.setTitle("Update Account");
            stage.setScene(updateViewScene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("AccountManController onUpdate: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void onDelete(ActionEvent actionEvent) {
        if (selectedAccount == null) {
            showErrorDialog("Please select an account first.");
            return;
        }
        System.out.println("AccountManController:onDelete ");
        Scene currentScene = prodman.getScene();
        Window window = currentScene.getWindow();
        window.hide();
        try {
            if (deleteViewScene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("delete-account.fxml"));
                Parent root = fxmlLoader.load();
                deleteProductController = fxmlLoader.getController();
                deleteProductController.setController(this);
                deleteProductController.setStage(this.stage);
                deleteProductController.setParentScene(currentScene);
                deleteViewScene = new Scene(root, 300, 600);
                String css = SplashApp.class.getResource("/css/splash.css").toExternalForm();
                deleteViewScene.getStylesheets().add(css);
            } else {
                deleteProductController.refresh();
            }
            stage.setTitle("Delete Account");
            stage.setScene(deleteViewScene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("AccountManController onDelete: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void onClose(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Exit and lose changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Platform.exit();
        }
    }

    public void onLogOut(ActionEvent actionEvent) {
        System.out.println("AccountManController:onLogOut ");
        Scene currentScene = prodman.getScene();
        Window window = currentScene.getWindow();
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
        }
    }

    public void onAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Banking Management System");
        alert.setContentText("Banking Management System version 1.0\nDeveloped with JavaFX and Spring Boot.");
        alert.showAndWait();
    }

    void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void addItem(Account account) {
        lvAccounts.getItems().add(account);
    }
}
