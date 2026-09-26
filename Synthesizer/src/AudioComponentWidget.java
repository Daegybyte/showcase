import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class AudioComponentWidget extends Pane {

    private AnchorPane parent_;
    private HBox baseLayout_ = new HBox();
    private AudioComponent audioComp_ = null;
    String name_ = "ACW Name Not Initialised";
    private Line line_ = null;
    private Point2D mouseStart_;
    protected HBox bottom_;


    AudioComponentWidget(AudioComponent ac, AnchorPane parent, String componentName) {

        parent_ = parent; //used to place the widget
        audioComp_ = ac;
        baseLayout_.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2");
        VBox rightSide = new VBox();
        Button closeBtn = new Button("close");
        closeBtn.setOnAction(e -> close());

        bottom_ = new HBox();

        baseLayout_.getChildren().add(bottom_);

        Circle outputCircle = new Circle(10);
        outputCircle.setFill(Color.SILVER);
        outputCircle.setOnMousePressed(e -> startConnection(e, outputCircle));
        outputCircle.setOnMouseDragged(e -> moveConnection(e, outputCircle));
        outputCircle.setOnMouseReleased(e -> stopConnecting(e, outputCircle));

        rightSide.setAlignment(Pos.CENTER);
        rightSide.setSpacing(5);
        rightSide.setPadding(new Insets(2));
        rightSide.getChildren().add(closeBtn);
        rightSide.getChildren().add(outputCircle);

        VBox leftSide = new VBox();
        Text title = new Text(componentName);
        title.setOnMousePressed(e -> handleMouseClick(e));
        title.setOnMouseDragged(e -> handleMove(e));

        leftSide.setAlignment(Pos.CENTER);
        leftSide.getChildren().add(title);


        //add in all pieces.
        baseLayout_.getChildren().add(leftSide);
        baseLayout_.getChildren().add(rightSide);

        //Get us on the screen
        this.getChildren().add(baseLayout_);
        parent.getChildren().add(this);

        AnchorPane.setTopAnchor(this, 50.0);
        AnchorPane.setLeftAnchor(this, 100.0);
    }

    private void stopConnecting(MouseEvent e, Circle outputCircle) {
        Circle speaker = SynthApp.speaker_;
        Bounds bounds = speaker.localToScene(speaker.getBoundsInLocal());
        double distance = Math.sqrt(Math.pow(bounds.getCenterX() - e.getSceneX(), 2.0) + Math.pow(bounds.getCenterY() - e.getSceneY(), 2.0));
        if (distance < 20) {
            //made connection.
            SynthApp.speakerConnections_.add(audioComp_);
        } else {
            parent_.getChildren().remove(line_);
        }
    }

    private void moveConnection(MouseEvent e, Circle outputCircle) {
        Bounds bounds = parent_.getBoundsInParent();
        if (line_ != null) {
            line_.setEndX(e.getSceneX() - bounds.getMinX());
            line_.setEndY(e.getSceneY() - bounds.getMinY());
        }
    }

    private void startConnection(MouseEvent e, Circle outputCircle) {
        if (line_ != null) {
            parent_.getChildren().remove(line_);
            SynthApp.speakerConnections_.remove(audioComp_);
        }
        Bounds bounds = parent_.getBoundsInParent();
        line_ = new Line();
        line_.setStrokeWidth(3);
        line_.setStartX(e.getSceneX() - bounds.getMinX());
        line_.setStartY(e.getSceneY() - bounds.getMinY());
        line_.setEndX(e.getSceneX() - bounds.getMinX());
        line_.setEndY(e.getSceneY() - bounds.getMinY());

        parent_.getChildren().add(line_);

    }

    private void handleMove(MouseEvent e) {
        Bounds bounds = parent_.getBoundsInParent();
        Point2D mouseNow = new Point2D(e.getSceneX(), e.getSceneY());
        AnchorPane.setTopAnchor(this, AnchorPane.getTopAnchor(this) +  mouseNow.getY() - mouseStart_.getY());
        AnchorPane.setLeftAnchor(this, AnchorPane.getLeftAnchor(this) + mouseNow.getX()- mouseStart_.getX());

        if (line_ != null) {
            line_.setStartX(line_.getStartX() + (mouseNow.getX() - mouseStart_.getX()));
            line_.setStartY(line_.getStartY() + (mouseNow.getY() - mouseStart_.getY()));
        }
        mouseStart_ = mouseNow;
    }

    private void close() {
        parent_.getChildren().remove(this);
        SynthApp.speakerConnections_.remove(this.audioComp_);
        if (line_ != null) {
            parent_.getChildren().remove(line_);
        }
    }


    private void handleMouseClick(MouseEvent mouseClick){
        Point2D clickXY = new Point2D(mouseClick.getSceneX(), mouseClick.getSceneY() );
        mouseStart_ = clickXY;
    }
}
