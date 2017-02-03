package stud.opencv.server.fx;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import stud.opencv.server.network.properties.PropertiesHandle;
import stud.opencv.server.network.properties.PropertiesServer;
import stud.opencv.server.network.properties.protocol.out.ChangePropertyPacket;
import stud.opencv.server.network.properties.protocol.structs.DoubleProperty;
import stud.opencv.server.network.properties.protocol.structs.IntProperty;
import stud.opencv.server.network.properties.protocol.structs.Property;
import stud.opencv.server.network.properties.protocol.structs.SelectProperty;
import stud.opencv.server.utils.FXUtils;

import java.util.*;

/**
 * Created by DiaLight on 01.02.2017.
 */
public class PropertiesFxFrame implements PropertiesHandle {

    private final Map<String, Object> properties = new HashMap<>();
    public final Stage stage;
    private final VBox propertiesVBox;
    private final Label connectionInfo;
    private final Label ping;
    private final Stage primaryStage;

    private PropertiesServer callback;
    public void setCallback(PropertiesServer callback) {this.callback = callback;}

    public PropertiesFxFrame(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.stage = new Stage(StageStyle.TRANSPARENT);
        this.stage.setTitle("Properties");
        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initOwner(primaryStage);
        propertiesVBox = new VBox();
        connectionInfo = new Label();
        ping = new Label();
        stage.setScene(new Scene(
                new VBox(
                        connectionInfo,
                        ping,
                        propertiesVBox
                ) {{
                    getStyleClass().addAll("server-properties");
                    FXUtils.setMovable(stage, this);
                }},
                Color.TRANSPARENT
        ) {{
            getStylesheets().add(ClassLoader.getSystemResource("scene-properties.css").toExternalForm());
        }});
//        FXUtils.runStyleDevHelper(stage, "http://localhost/scene-properties.css");
    }

    public void close() {
        this.stage.close();
    }

    @Override
    public void clear() {
        Platform.runLater(() -> {
            properties.clear();
            propertiesVBox.getChildren().clear();
        });
    }

    @Override
    public void setConnection(String connection) {
        Platform.runLater(() -> {
            connectionInfo.setText(connection);
        });
    }

    @Override
    public void pong(long time) {
        Platform.runLater(() -> {
            if(time < 0) {
                ping.setText("");
            } else {
                ping.setText(String.format("ping: %d", System.currentTimeMillis() - time));
            }
        });
    }

    @Override
    public void put(String key, Property value) {
        switch (value.getType()) {
            case INT:
                IntProperty intProperty = (IntProperty) value;
                Platform.runLater(() -> {
                    putInt(key, intProperty.get());
                });
                break;
            case DOUBLE:
                DoubleProperty doubleProperty = (DoubleProperty) value;
                Platform.runLater(() -> {
                    putDouble(key, doubleProperty.get());
                });
                break;
            case SELECT:
                SelectProperty selectProperty = (SelectProperty) value;
                Platform.runLater(() -> {
                    putSelect(key, selectProperty.getSelected(), selectProperty.getSelections());
                });
                break;
        }
    }

    private void putSelect(String key, int selected, HashMap<Integer, String> selections) {
        SingleSelectionModel<String> selectionModel = (SingleSelectionModel<String>) properties.get(key);
        if(selectionModel == null) {
            ComboBox<String> comboBox = new ComboBox<String>(new ObservableListWrapper<>(new ArrayList<>(selections.values()))) {{
                getStyleClass().addAll("server-property-value");
            }};
            selectionModel = comboBox.getSelectionModel();
            selectionModel.select(selected);
            properties.put(key, selectionModel);
            Integer[] keys = selections.keySet().toArray(new Integer[0]);
            selectionModel.selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                    callback.trySendPacket(new ChangePropertyPacket(key, new SelectProperty(keys[(int) newValue])))
            );
            addProperty(key, comboBox);
        } else {
            selectionModel.select(selected);
        }
    }

    private void putDouble(String key, double value) {
        SpinnerValueFactory<Double> valueFactory = (SpinnerValueFactory<Double>) properties.get(key);
        if(valueFactory == null) {
            valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0);
            valueFactory.setValue(value);
            properties.put(key, valueFactory);
            valueFactory.valueProperty().addListener((observable, oldValue, newValue) ->
                    callback.trySendPacket(new ChangePropertyPacket(key, new DoubleProperty(newValue)))
            );
            addProperty(key, new Spinner<Double>(valueFactory) {{
                getStyleClass().addAll("server-property-value");
                setEditable(true);
            }});
        } else {
            valueFactory.setValue(value);
        }
    }

    private void putInt(String key, int value) {
        SpinnerValueFactory<Integer> valueFactory = (SpinnerValueFactory<Integer>) properties.get(key);
        if(valueFactory == null) {
            valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000);
            valueFactory.setValue(value);
            properties.put(key, valueFactory);
            valueFactory.valueProperty().addListener((observable, oldValue, newValue) ->
                    callback.trySendPacket(new ChangePropertyPacket(key, new IntProperty(newValue)))
            );
            addProperty(key, new Spinner<Integer>(valueFactory) {{
                getStyleClass().addAll("server-property-value");
                setEditable(true);
            }});
        } else {
            valueFactory.setValue(value);
        }
    }

    private void addProperty(String key, Node value) {
        HBox hBox = new HBox(new Label(key) {{
            getStyleClass().addAll("server-property-key");
        }}, new Region() {{
            HBox.setHgrow(this, Priority.ALWAYS);
        }}, value) {{
            getStyleClass().addAll("server-property");
        }};
        ObservableList<Node> children = propertiesVBox.getChildren();
        ListIterator<Node> iterator = children.listIterator();
        boolean added = false;
        int i = 0;
        while (iterator.hasNext()) {
            HBox box = (HBox) iterator.next();
            Label label = (Label) box.getChildren().get(0);
            int compare = key.compareToIgnoreCase(label.getText());
            if(compare < 0) {
                children.add(i, hBox);
                added = true;
                break;
            }
            i++;
        }
        if(!added) {
            children.add(hBox);
        }
        stage.sizeToScene();
    }

    public void show() {
        if (!stage.isShowing()) {
            stage.show();
            stage.toFront();
        }
        Platform.runLater(() -> {
            double x = primaryStage.getX();
            double y = primaryStage.getY();
            x += primaryStage.getWidth()/2;
            y += primaryStage.getHeight()/2;
            stage.sizeToScene();
            x -= stage.getWidth()/2;
            y -= stage.getHeight()/2;
            stage.setX(x);
            stage.setY(y);
        });
    }
}
