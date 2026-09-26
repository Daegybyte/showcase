import javafx.application.Application;
import javax.sound.sampled.LineUnavailableException;

public class Main{
    public static void main(String[] args) throws LineUnavailableException {

        System.out.println("Hello, world!");
        Application.launch(SynthApp.class);

    }
}
