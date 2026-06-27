package com.gabriel.account.utilControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Data;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Data
public class SplashController implements Initializable {
    @Setter
    Stage stage;

    @javafx.fxml.FXML
    @Setter
    public ImageView productImage;

    @Setter
    Image image;

    @javafx.fxml.FXML
    private Button btnProceed;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            image = new Image(getClass().getResourceAsStream("/images/wink.gif"));
            if (image != null) {
                productImage.setImage(image);
            }
        } catch (Exception ex) {
            System.out.println("Error loading splash image: " + ex.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void onProceed(ActionEvent actionEvent) {
        System.out.println("SplashApp:onProceed ");
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
            System.out.println("Error occurred opening user selection screen: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
