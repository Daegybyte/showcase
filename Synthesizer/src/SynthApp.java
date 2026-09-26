import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.util.ArrayList;

public class SynthApp extends Application {

    private class AudioListener implements  LineListener{
        public AudioListener (Clip c) {
            clip_ = c;
        }

        @Override
        public void update(LineEvent event){
            if(event.getType() == LineEvent.Type.STOP) {
                System.out.println("Close Clip");
                clip_.close();
            }
        }
        private Clip clip_;
    }

    double windowWidth_ = 700.00;
    double windowHeight_ = 500.00;
    int btnWidth_ = 30;
    int btnHeight_ = 60;
    public double appVolume_ = 1;
    public static Circle speaker_;
    public static ArrayList<AudioComponent> speakerConnections_ = new ArrayList<>();

    AnchorPane canvas = new AnchorPane();


    
    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane borderPane = new BorderPane();

        //Slider
        Slider slider = new Slider(0.0, 1.0, 0.5);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.25f);
        slider.setOnMouseDragged(e -> handleSlider(slider));

        //Top
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-background-color: grey");
        top.setPadding(new Insets(10));
        borderPane.setTop(top);
        top.setSpacing(btnWidth_ / 2);

        //Bottom
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.setStyle("-fx-background-color: grey");
        bottom.getChildren().add(slider);
        borderPane.setBottom(bottom);
        bottom.setPadding(new Insets(10));
        Text text = new Text("Volume");
        bottom.getChildren().add(text);

        //right position
        VBox right = new VBox();
        right.setStyle("-fx-background-color: grey");
        right.setAlignment(Pos.CENTER);
        createAudioComponentSidebar(right);
        borderPane.setRight(right);
        right.setPadding(new Insets(10));

        //center-ish canvas. I dunno. Sometimes things don't fit neatly into boxes.
        // Which this is. It is a box. but not like that kind of box.
        borderPane.setCenter(canvas);
        borderPane.setStyle("-fx-background-color: dimgray");

        speaker_ = new Circle(15);
        speaker_.setFill(Color.SILVER);
        canvas.getChildren().add(speaker_);
        speaker_.setLayoutX(windowWidth_-150);
        speaker_.setLayoutY(windowHeight_-150);


        //Buttons
        Button btnA = new Button();
        top.getChildren().add(btnA);
        btnA.setText("A");

        Button btnB = new Button();
        top.getChildren().add(btnB);
        btnB.setText("B");

        Button btnC = new Button();
        top.getChildren().add(btnC);
        btnC.setText("C");

        Button btnD = new Button();
        top.getChildren().add(btnD);
        btnD.setText("D");

        Button btnE = new Button();
        top.getChildren().add(btnE);
        btnE.setText("E");

        Button btnF = new Button();
        top.getChildren().add(btnF);
        btnF.setText("F");

        Button btnG = new Button();
        top.getChildren().add(btnG);
        btnG.setText("G");

        Button doNotPress = new Button();
        top.getChildren().add(doNotPress);
        doNotPress.setText("Do Not Press");

        Button play = new Button();
        right.getChildren().add(play);
        play.setText("Play");

//        Button btnZoop = new Button("Zoop");
//        right.getChildren().add(btnZoop);
//        btnZoop.setOnAction(e -> createAcComponenent("Zoop"));

        LinearRamp linearRamp = new LinearRamp(50.F, 10000.F);
        System.out.println("zoop");
        FrequencyWaveGen makeZoop = new FrequencyWaveGen();

//        btnZoop.setOnAction(e -> handleZoop(linearRamp));
        btnA.setOnAction(e -> handleButtonPress(440));
        btnB.setOnAction(e -> handleButtonPress(494));
        btnC.setOnAction(e -> handleButtonPress(523));
        btnD.setOnAction(e -> handleButtonPress(587));
        btnE.setOnAction(e -> handleButtonPress(659));
        btnF.setOnAction(e -> handleButtonPress(698));
        btnG.setOnAction(e -> handleButtonPress(784));
        doNotPress.setOnAction(e -> handleButtonPress(Integer.MAX_VALUE));
        slider.setOnMouseDragged(e -> handleSlider(slider));
        play.setOnAction(e -> playSound(play));


        primaryStage.setScene(new Scene(borderPane, windowWidth_, windowHeight_));
        primaryStage.show();
    }


    private void playSound(Button play) {
        playNoise();
    }

    private void createAudioComponentSidebar(VBox right) {
        right.setSpacing(10);
        right.setAlignment(Pos.CENTER);

        Button btnSine = new Button("SineWave");
        right.getChildren().add(btnSine);
        btnSine.setOnAction(e-> createAcComponenent("SineWave"));

        Button btnZoop = new Button("Zoop");
        right.getChildren().add(btnZoop);
        btnZoop.setOnAction(e -> createAcComponenent("Zoop"));

    }

    private void handleSlider(Slider slider) {
        appVolume_ = slider.getValue();
        System.out.println(slider.getValue());
    }

    private void createAcComponenent(String componentName) {
        switch (componentName) {
            case "SineWave":
                SineWave sineWave = new SineWave(440);
                AudioComponentWidget audioComponentWidget = new SineWidget(sineWave, canvas, "SineWave");
                break;
        }

        switch (componentName){
            case "Zoop" :
                LinearRamp linearRamp = new LinearRamp(50.F, 10000.F);
                FrequencyWaveGen makeZoop = new FrequencyWaveGen();
                AudioComponentWidget audioComponentWidget = new SineWidget(linearRamp, canvas, "Zoop");
                break;
        }
    }

    private AudioComponent makeSoundWave(int frequency, float volume) {
        AudioComponent result = new Filter(volume);
        AudioComponent sineWave = new SineWave(frequency);
        result.connectInput(sineWave);
        return result;
    }

    private EventHandler<ActionEvent> handleButtonPress(int frequency) {
        AudioComponent audioComponent = makeSoundWave(frequency, (float) appVolume_);
        speakerConnections_.add(audioComponent);
        playNoise();
        speakerConnections_.remove(audioComponent);
        return null;
    }

//    private EventHandler<ActionEvent> handleZoop(LinearRamp linearRamp) {
//        AudioComponent audioComponent = makeSoundWave(linearRamp, (float) appVolume_);
//
//        speakerConnections_.add(linearRamp);
//        playNoise();
//        speakerConnections_.remove(linearRamp);
//        return null;
//    }

    private void playNoise() {
        System.out.println("Playing noise");
        LinearRamp zoop = new LinearRamp(50, 10000);
        AudioComponent frequencyWaveGen = new FrequencyWaveGen();
        frequencyWaveGen.connectInput(zoop);

        for (AudioComponent ac : speakerConnections_) {
            Clip c = null;
            try {
                System.out.println("Playing " + ac.toString());
//                AudioComponent note = Main.makeWave(frequency, volume_);
                AudioFormat format16 = new AudioFormat(AudioClip.sampleRate, 16, 1, true, false);
                c = AudioSystem.getClip();

                AudioListener listener = new AudioListener(c);
                byte[] byteData = ac.getClip().getData();
//                if(btnName.equals("zoop")){
//                    byte[] byteData = frequencyWaveGen.getClip().getData();
//                }

                c.open(format16, byteData, 0, byteData.length); // Reads data from our byte array to play it.
                System.out.println("About to play...");
                c.start(); // Plays it.

                c.addLineListener(listener);
                System.out.println("Done.");

            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }
}
