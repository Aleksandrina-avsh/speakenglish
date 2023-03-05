import javafx.animation.PauseTransition;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MediaControlSpeech extends BorderPane {
    private static final String dirMP3 = System.getProperty("user.home") + "/SpeechEnglish/";
    private final static ArrayList<String> textSpeech = new ArrayList<>();
    private static final File dir = new File(System.getProperty("user.home") + "/SpeechEnglish");
    private static String fileTextName =  System.getProperty("user.home") + "/test.txt";
    private MediaPlayer mp;
    private MediaPlayer.Status status;
    MediaView mediaView;
    private final ArrayList<Button> buttons = new ArrayList<>();
    private int count = 0;
    private final ArrayList<Integer> speechOrderNumberFile = new ArrayList<>();
    private SaveTextToMP3 saveTask;
    public MediaControlSpeech() {

        Pane mvPane = new Pane();
        mvPane.setStyle("-fx-background-color: #fffff0;");

        Button btnTextSpeech = new Button();
        btnTextSpeech.setLayoutX(10);
        btnTextSpeech.setLayoutY(50);
        btnTextSpeech.setPrefSize(580,140);
        btnTextSpeech.setStyle("-fx-font-size: 24px; -fx-border-color: #fffff0; -fx-background-color: #fffff0; ");
        btnTextSpeech.setWrapText(true);

        buttons.add(btnTextSpeech);

        File fileA00 = new File(dirMP3 + "A00.txt");

        if (fileA00.exists()) {

            saveArrayTextSpeech(dirMP3 + "A00.txt");
            for (int i = 0; i <= textSpeech.size() - 1; i++) {
                speechOrderNumberFile.add(i);
            }
            mp = createMediaPlayer();
            mediaView = new MediaView(mp);
            mvPane.getChildren().add(mediaView);
        }

        HBox hBox = new HBox();
        hBox.setSpacing(2);
        hBox.setPrefWidth(600);

        Button fileSaveMP3 = new Button("Creating MP3 files");
        fileSaveMP3.setMaxWidth(Double.MAX_VALUE);
        fileSaveMP3.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                " -fx-background-color: #f5f5dc; ");

        Button speechOrder = new Button("Read in order");
        speechOrder.setMaxWidth(Double.MAX_VALUE);
        speechOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                " -fx-background-color: #c0c0c0; -fx-text-fill: #fffff0");
        Button speechRandomOrder = new Button("Read in random order");
        speechRandomOrder.setMaxWidth(Double.MAX_VALUE);
        speechRandomOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                " -fx-background-color: #f5f5dc; ");

        HBox.setHgrow(fileSaveMP3, Priority.ALWAYS);
        HBox.setHgrow(speechOrder, Priority.ALWAYS);
        HBox.setHgrow(speechRandomOrder, Priority.ALWAYS);
        hBox.getChildren().addAll(fileSaveMP3, speechOrder, speechRandomOrder);
        mvPane.getChildren().add(hBox);

        fileSaveMP3.setOnAction(event -> {

// if directory dir exists, remove MP3 files
// otherwise create dir directory
// create file A00.txt from test.txt to fill speech text when reading MP3 files
            try {
                if (dir.isDirectory()) {
                    for (File f : Objects.requireNonNull(dir.listFiles())) {
                        f.delete();
                    }
                } else {
                    dir.mkdir();
                }
                textSpeech.clear();
                speechOrderNumberFile.clear();
                copyFileUsingStream(fileTextName, dirMP3 + "A00.txt");
                saveArrayTextSpeech(dirMP3 + "A00.txt");
                for (int i = 0; i <= textSpeech.size() - 1; i++) {
                    speechOrderNumberFile.add(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final ProgressBar pb = new ProgressBar();
            pb.setStyle("-fx-accent: #808080; ");
            pb.setPrefWidth(200);
            pb.setProgress(0);

            final ProgressIndicator pin = new ProgressIndicator();

            pin.setMinSize(50, 50);
            pin.setStyle("-fx-accent: #808080; ");
            pin.setProgress(0);

            final Button statusButton = new Button();
            statusButton.setMinWidth(230);
            statusButton.setStyle("-fx-text-fill: #000000;");

            final HBox hb = new HBox();
            hb.setLayoutX(5);
            hb.setLayoutY(140);
            hb.setSpacing(3);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(pb, pin, statusButton);

            fileSaveMP3.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                    "-fx-background-color: #c0c0c0; -fx-text-fill: #fffff0");

            buttons.get(0).setText("Recording MP3 files");
            mvPane.getChildren().add(hb);

            saveTask = new SaveTextToMP3(textSpeech, dirMP3);

            pb.progressProperty().unbind();
            pb.progressProperty().bind(saveTask.progressProperty());

            pin.progressProperty().unbind();
            pin.progressProperty().bind(saveTask.progressProperty());

            statusButton.textProperty().unbind();
            statusButton.textProperty().bind(saveTask.messageProperty());

            saveTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                    t -> {
                        mvPane.getChildren().remove(hb);
                        buttons.get(0).setText("");
                        fileSaveMP3.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                                " -fx-background-color: #f5f5dc; ");
                        mp = createMediaPlayer();
                        mediaView = new MediaView(mp);
                        mvPane.getChildren().add(mediaView);
                        buttons.get(1).setText("►");
                        speechOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                                "-fx-background-color: #c0c0c0; -fx-text-fill: #fffff0");
                        speechRandomOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                                "-fx-background-color: #f5f5dc; -fx-text-fill: #000000");
                    });

            new Thread(saveTask).start();

        });

        speechOrder.setOnAction(e -> {
// Read in order
            Collections.sort(speechOrderNumberFile);
            speechOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                    "-fx-background-color: #c0c0c0; -fx-text-fill: #fffff0");
            speechRandomOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                    "-fx-background-color: #f5f5dc; -fx-text-fill: #000000");
            count = 0;
        });

        speechRandomOrder.setOnAction(e -> {
// Read in random order
            Collections.shuffle(speechOrderNumberFile);
            speechOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                    "-fx-background-color: #f5f5dc; -fx-text-fill: #000000");
            speechRandomOrder.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-border-color: #000000;" +
                    "-fx-background-color: #c0c0c0; -fx-text-fill: #fffff0");
            count = 0;
        });

        mvPane.setStyle("-fx-background-color: #fffff0;");
        setCenter(mvPane);

        final Button btnStart = new Button("►");
        btnStart.setLayoutX(8);
        btnStart.setLayoutY(194);
        btnStart.setStyle("-fx-font-size: 20px; -fx-border-radius: 5; -fx-border-color: #000000; -fx-background-color: #f5f5dc; ");
        btnStart.setPrefSize(17,15);

        final Button btnPevious = new Button("≤");
        btnPevious.setLayoutX(54);
        btnPevious.setLayoutY(194);
        btnPevious.setStyle("-fx-font-size: 20px; -fx-border-radius: 5; -fx-border-color: #000000; -fx-background-color: #f5f5dc; ");
        btnPevious.setPrefSize(17,15);

        final Button btnNext = new Button("≥");
        btnNext.setLayoutX(101);
        btnNext.setLayoutY(194);
        btnNext.setStyle("-fx-font-size: 20px; -fx-border-radius: 5; -fx-border-color: #000000; -fx-background-color: #f5f5dc; ");
        btnNext.setPrefSize(17,15);

        buttons.add(btnStart);
        buttons.add(btnPevious);
        buttons.add(btnNext);

        btnStart.setOnAction(e -> {
            if (fileA00.exists()) {
                status = mp.getStatus();
                buttons.get(0).setText(textSpeech.get(speechOrderNumberFile.get(count)));
                mediaButtonEvent(buttons);
            } else {
            buttons.get(0).setText("No MP3 files or directory " + dirMP3);
            }
        });

        btnPevious.setOnAction(e -> {
            mp.stop();
            mp.dispose();
            if (count > 0) {
                count = count - 1;
                mp = createMediaPlayer();
                buttons.get(0).setText(textSpeech.get(speechOrderNumberFile.get(count)));
                mediaButtonEvent(buttons);
            }
        });

        btnNext.setOnAction(e -> {
            mp.stop();
            mp.dispose();
            if (count < textSpeech.size() - 1) {
                count = count + 1;
                mp = createMediaPlayer();
                buttons.get(0).setText(textSpeech.get(speechOrderNumberFile.get(count)));
                mediaButtonEvent(buttons);
            }
        });

        btnTextSpeech.setOnAction( e -> {

            Event.fireEvent(btnStart, new ActionEvent());
        });

        mvPane.getChildren().addAll(buttons.get(0), buttons.get(1), buttons.get(2), buttons.get(3));
    }

    public void mediaButtonEvent(ArrayList<Button> buttons) {

        if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return;
        }

        if (status == MediaPlayer.Status.PAUSED
                || status == MediaPlayer.Status.READY
                || status == MediaPlayer.Status.STOPPED) {
            buttons.get(1).setText("►");
            mp.play();
        } else {
            // status == PLAYING
            buttons.get(1).setText("||");
            mp.pause();
        }

        mp.setOnEndOfMedia(() -> {
            PauseTransition pt = new PauseTransition(Duration.millis(600));
            pt.play();
            pt.setOnFinished(e -> {
                mp.stop();
                mp.dispose();

                if (count < textSpeech.size() - 1) {
                    count = count + 1;
                }
                else {
                    count = 0;
                }
                buttons.get(0).setText(textSpeech.get(speechOrderNumberFile.get(count)));
                mp = createMediaPlayer();
                mediaButtonEvent(buttons);
            });
        });
    }
    private MediaPlayer createMediaPlayer() {
        File file = new File(dirMP3 + "A" + speechOrderNumberFile.get(count) + ".mp3");
        Media media = new Media(file.toURI().toString());
        return new MediaPlayer(media);
    }
    private void saveArrayTextSpeech(String fileInput) {
//save ArrayList textspeech
        try (BufferedReader br = new BufferedReader(new FileReader(fileInput))) {
            String str;

            while ((str = br.readLine()) != null) {
                textSpeech.add(str.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void copyFileUsingStream(String source, String dest) {
// create file A00.txt
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

