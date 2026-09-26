import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

public class SineWidget extends AudioComponentWidget {
    SineWidget(AudioComponent ac, AnchorPane parent, String componentName) {
        super(ac, parent, componentName);

        Slider sliderWidget = new Slider(440, 784, 587);
        sliderWidget.setMajorTickUnit(0.25f);
        bottom_.getChildren().add(sliderWidget);
        sliderWidget.setOnMouseDragged(e -> handleSliderWidget(sliderWidget));

    }

    private void handleSliderWidget(Slider sliderWidget) {
        SineWave.frequency_ = sliderWidget.getValue();
        System.out.println(sliderWidget.getValue());
    }

}
