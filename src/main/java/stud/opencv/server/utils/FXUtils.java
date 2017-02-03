package stud.opencv.server.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stud.opencv.server.fx.PropertiesFxFrame;

import java.util.concurrent.TimeUnit;

/**
 * Created by Light on 03.02.2017.
 */
public class FXUtils {

    public static void runStyleDevHelper(Stage stage, String host) {
        Scheduler.runWithDelay(() -> Platform.runLater(() -> {
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(host + "?v=" + System.currentTimeMillis());
            stage.sizeToScene();
        }), 1, TimeUnit.SECONDS);
    }

    public static void setMovable(Stage dialog, Node node) {
        Delta dragDelta = new Delta();
        node.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
            dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
        });
        node.setOnMouseDragged(mouseEvent -> {
            dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
            dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
    }

    private static class Delta { double x, y; }


}
