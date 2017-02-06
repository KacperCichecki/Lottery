package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger logger =
            Logger.getLogger(Main.class.getName());

    static Scene scene = null;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Lottery!");
        primaryStage.getIcons().add(new Image(getClass().getResource("/sample/pic/CrazySanta.png").toString()));

        scene = new Scene(root, 615, 380);
        setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        Controller.programStarted = false;
        Controller.timer.cancel();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
