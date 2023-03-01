import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

public class SpeechEnglishGoogle extends Application {

    public static void main(String[] args) {
            Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("SpeechEnglish");

        Group root = new Group();
        Scene scene = new Scene(root, 600, 240, Color.web("#fffff0"));

        MediaControlSpeech mediaControl = new MediaControlSpeech();

        scene.setRoot(mediaControl);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

    }
}
