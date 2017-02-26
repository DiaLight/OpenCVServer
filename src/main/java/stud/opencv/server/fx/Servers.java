package stud.opencv.server.fx;

import javafx.application.Platform;
import stud.opencv.server.network.properties.PropertiesServer;
import stud.opencv.server.network.stream.StreamServer;

/**
 * Created by DiaLight on 01.02.2017.
 */
public class Servers {

    private final StreamServer streamServer;
    private final PropertiesServer propertiesServer;

    public Servers(int port, Controller controller) {
        streamServer = new StreamServer(port);
        streamServer.setHandler(imagePacket -> {
            Platform.runLater(() -> {
                controller.imageView.setImage(imagePacket.getFXImage());
            });
        });

        propertiesServer = new PropertiesServer(port);
        propertiesServer.setPropertiesHandle(controller.properties);
        controller.properties.setCallback(propertiesServer);
    }

    public void startServers() {
        streamServer.start();
        propertiesServer.startAsync();
    }

    public void stopServers() {
        try {
            streamServer.stopAll();
        } catch (Exception ignore) {}
        try {
            propertiesServer.stopAll();
        } catch (Exception ignore) {}
    }
}
