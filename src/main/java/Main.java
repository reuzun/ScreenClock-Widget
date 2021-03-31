import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author reuzun
 */
public class Main extends Application {

    double xOffset;
    double yOffset;
    StackPane mainPanel = new StackPane();
    static Stage stage;
    Text text = new Text();
    Scene scene = new Scene(mainPanel);
    javafx.scene.robot.Robot robot = new Robot();
    boolean autoColor = true;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setOpacity(0);
        primaryStage.setHeight(0);
        primaryStage.setWidth(0);
        primaryStage.show();

        Stage localStage = new Stage();
        localStage.initOwner(primaryStage);

        text.setText(getTime());
        text.setStyle("" +
                "-fx-cursor: HAND;" +
                "-fx-underline: false;"
        );


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(autoColor) {
                            WritableImage imgReturn = robot.getScreenCapture(null, new Rectangle2D(stage.getX(), stage.getY(), 10, 10));
                            PixelReader px = ((Image) imgReturn).getPixelReader();
                            Color pixel = px.getColor(4, 4);
                            if ((pixel.getRed() * 255) * 0.299 + (pixel.getGreen() * 255) * 0.587 + (pixel.getBlue() * 255 * 0.114) < 186) {
                                text.setFill(Color.NAVAJOWHITE);
                            } else {
                                text.setFill(Color.BLACK);
                            }
                        }
                        text.setText(getTime());
                    }
                });
            }
        }, 0, 500);



        text.setFont(Font.font(16));
        text.setFill(Color.NAVAJOWHITE);

        stage = localStage;
        makeStageDrageable();
        mainPanel.getChildren().add(text);

        scene.setFill(Color.TRANSPARENT);
        localStage.initStyle(StageStyle.TRANSPARENT);
        localStage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setX(Screen.getPrimary().getBounds().getWidth() - Screen.getPrimary().getBounds().getWidth() * 0.04);
        stage.setY(Screen.getPrimary().getBounds().getHeight() - Screen.getPrimary().getBounds().getHeight() * 0.9475);
        stage.show();

    }

    private String getTime(){
        ZonedDateTime dateTime = ZonedDateTime.now();
        StringBuilder sb = new StringBuilder();

        String hour = String.valueOf(dateTime.getHour());
        String minute = dateTime.getMinute() < 10 ? String.valueOf("0"+dateTime.getMinute()) : String.valueOf(dateTime.getMinute());

        sb.append(hour + ":" + minute);

        return sb.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }

    //Draggable stage and App properties with mouse clicking
    private void makeStageDrageable() {
        mainPanel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        mainPanel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.stage.setX(event.getScreenX() - xOffset);
                Main.stage.setY(event.getScreenY() - yOffset);
                Main.stage.setOpacity(0.5f);
            }
        });
        mainPanel.setOnDragDone((e) -> {
            Main.stage.setOpacity(1.0f);
        });
        mainPanel.setOnMouseReleased((e) -> {
            Main.stage.setOpacity(1.0f);
        });
        mainPanel.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.MIDDLE)
                System.exit(-1);
            if(e.getButton() == MouseButton.PRIMARY ){
                if(e.isControlDown()){
                    text.setFont(Font.font(text.getFont().getSize() + 4));
                    stage.setHeight(stage.getHeight() + 11);
                    stage.setWidth(stage.getWidth() + 11);
                }
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                if(e.isControlDown()){
                    text.setFont(Font.font(text.getFont().getSize() - 4));
                    stage.setHeight(stage.getHeight() - 11);
                    stage.setWidth(stage.getWidth() - 11);
                }
                else if(e.isShiftDown()){
                    autoColor = !autoColor;
                }
                else {
                    if (text.getFill() == Color.BLACK)
                        text.setFill(Color.NAVAJOWHITE);
                    else
                        text.setFill(Color.BLACK);
                }

            }
            if (e.getButton() == MouseButton.MIDDLE) {
                    text.setFont(Font.font(text.getFont().getSize() + 4));
                    stage.setHeight(stage.getHeight() + 25);
                    stage.setWidth(stage.getWidth() + 25);
            }
        });
    }
}
