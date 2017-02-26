package stud.opencv.server.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import stud.opencv.server.utils.FXUtils;
import stud.opencv.server.utils.Scheduler;

/**
 * Created by DiaLight on 08.12.2016.
 */
public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static StackPane headerArea = null;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Arguments args = Arguments.parse(getParameters());

        // initialize the stage
        primaryStage.setTitle("OpenCV server");

        Controller controller = new Controller(primaryStage);
        Scene manualScene = new Scene(new AnchorPane(
                new TabPane(
                        new Tab("ImageView") {{
                            setContent(controller.imageView);
                            setOnSelectionChanged(controller::onPressTab);
                            setId("imageViewTab");
                        }},
                        new Tab("VideoView") {{
                            setContent(controller.streamView);
                            setOnSelectionChanged(controller::onPressTab);
                            setId("streamViewTab");
                        }}
                ) {{
                    Platform.runLater(() -> {
                        headerArea = (StackPane) lookup(".tab-header-area");
                    });
                    setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
                    getStyleClass().add("main-view");
                    FXUtils.setMovable(primaryStage, this);
                }},
                new ToggleButton("Properties") {{
                    setMnemonicParsing(false);
                    AnchorPane.setRightAnchor(this, 0.0);
                    AnchorPane.setTopAnchor(this, 0.0);
                    Platform.runLater(() -> {
                        AnchorPane.setTopAnchor(this, (headerArea.getHeight() - getHeight())/2);
                    });
                    setOnAction(e -> {
                        if (isSelected()) {
                            controller.properties.show();
                        } else {
                            controller.properties.close();
                        }
                    });
                }}
        )) {{
            getStylesheets().add(ClassLoader.getSystemResource("scene-main.css").toExternalForm());
        }};

        Servers servers = new Servers(args.getPort(), controller);
        servers.startServers();

        primaryStage.setOnCloseRequest(event -> {
            servers.stopServers();
            controller.properties.close();
            Scheduler.stop();
//            EventQueue.invokeLater(() -> {
//                for (Frame frame : Frame.getFrames()) {
//                    frame.dispose();
//                }
//            });
        });

        primaryStage.setScene(manualScene);
        primaryStage.setResizable(false);
        primaryStage.show();
//        FXUtils.runStyleDevHelper(primaryStage, "http://localhost/scene-main.css");
    }

}
