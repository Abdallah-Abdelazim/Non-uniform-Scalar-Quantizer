package quantizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Images Archiver");
        primaryStage.setScene(new Scene(root, 475, 250));
        //primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.getIcons().add(
                new Image(
                        Main.class.getResourceAsStream( "icon.png" )));
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(475);
        primaryStage.setMinHeight(250);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
