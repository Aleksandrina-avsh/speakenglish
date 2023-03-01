import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SaveTextToMP3 extends Task<Void> {
// writing an array of textSpeech strings to MP3 files
    ArrayList<String> textSpeech;
    String dirMP3;

    SaveTextToMP3() {

    }

    SaveTextToMP3(ArrayList<String> textSpeech, String dirMP3) {

        this.textSpeech = textSpeech;
        this.dirMP3 = dirMP3;
    }

    @Override
    protected Void call() throws Exception {

        String fileMP3;

        for (int i = 0; i < this.textSpeech.size(); i ++) {
            fileMP3 = dirMP3 + "A" + i + ".mp3";
            this.updateMessage("Save: " + fileMP3);
            this.updateProgress(i, this.textSpeech.size() - 1);
            saveMP3(fileMP3, textToGoogleTranslate(textSpeech.get(i)));
        }
        return null;
    }

    private void saveMP3(String file, String text) throws Exception {
//  writing to MP3 files
        ProcessBuilder processBuilder = new ProcessBuilder();


        String str = "wget -O " + file +
                " http://translate.google.com/translate_tts?client=tw-ob'&'tl=en" + "'&'q=" + text;
        processBuilder.command("bash", "-c", str);

        BufferedReader reader;

        Process process = processBuilder.start();
        processBuilder.directory(new File(System.getProperty("user.home")));

        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        reader.close();

        Thread.sleep(500);
    }

    private String textToGoogleTranslate(String str) {
// character conversion for GoogleTranslate
        StringBuilder sb = new StringBuilder(str);

        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == ' ') {
                sb.replace(i, i + 1, "%20");    // space
            }
            if (sb.charAt(i) == '?') {
                sb.replace(i, i + 1, "%3F");    // ?
            }
            if (sb.charAt(i) == 39) {
                sb.replace(i, i + 1, "%27");    // '
            }
        }
        return sb.toString();
    }
}
