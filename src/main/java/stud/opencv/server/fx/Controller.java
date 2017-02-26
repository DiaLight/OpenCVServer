package stud.opencv.server.fx;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import stud.opencv.server.utils.FXUtils;
import sun.plugin.util.UIUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by DiaLight on 01.02.2017.
 */
public class Controller {

    private static final boolean DEBUG = false;

    private Stage primaryStage;

    //elements
    public final MediaView streamView;
    public final ImageView imageView;

    public final PropertiesFxFrame properties;

    public Controller(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.properties = new PropertiesFxFrame(primaryStage);
        this.streamView = new MediaView() {{
            getStyleClass().add("main-view");
            FXUtils.setMovable(primaryStage, this);
        }};
        this.imageView = new ImageView() {{
            setPickOnBounds(true);
            setPreserveRatio(true);
            setCache(false);
            getStyleClass().add("main-view");
            imageProperty().addListener((observable, oldValue, newValue) -> {
                if(oldValue == null || oldValue.getWidth() != newValue.getWidth() || oldValue.getHeight() != newValue.getHeight()) {
                    setFitWidth(newValue.getWidth());
                    setFitHeight(newValue.getHeight());
                    System.out.println(newValue.getWidth() + ":" + newValue.getHeight());
                    primaryStage.sizeToScene();
                }
            });
            setImage(createImage());
            FXUtils.setMovable(primaryStage, this);
        }};
    }

    private Image createImage() {
        BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(new Color(0x82, 0x82, 0x82));
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
        g2.drawString("No UDP Image packet received", 300 - 110, 200 - 10);

        try {
            int y = 25;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.isLoopback()) continue;
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress.isLoopbackAddress()) continue;
                    if (!(inetAddress instanceof Inet4Address)) continue;
                    g2.drawString(inetAddress.getHostAddress(), 10, y);
                    y += 20;
                }
            }
        } catch (SocketException ignored) {}


        g2.dispose();
        return SwingFXUtils.toFXImage(img, null);
    }


    public void onPressTab(Event event) {
        Tab tab = (Tab) event.getSource();
        if (tab.isSelected()) {
//            System.out.println("Event  src: " + event.getSource() + " trg: " + event.getTarget());
            String id = tab.getId();
            if(id == null) throw new NullPointerException(String.format("Tab %s have no Id", tab.getText()));
            switch (id) {
                case "imageViewTab":
//                    imageViewSelect();
                    break;
                case "streamViewTab":

                    break;
            }
        }
    }

    private void imageViewSelect() {
        Image im = new Image("build.jpg");
        if(DEBUG) System.out.println("Image: " + im.getWidth() + " " + im.getHeight());

        imageView.setFitWidth(im.getWidth());
        imageView.setFitHeight(im.getHeight());
        imageView.setImage(im);
        if(DEBUG) System.out.println("ImageView: " + imageView.getFitWidth() + " " + imageView.getFitHeight());

//        Region p1 = (AnchorPane) imageView.getParent();
//        while(p1 != null) {
//            p1.autosize();
//            printRegion(p1.getClass().getSimpleName(), p1);
//            p1 = (Region) p1.getParent();
//        }

        primaryStage.sizeToScene();
    }

    private void printRegion(String pane, Region r) {
        if(DEBUG) System.out.printf(
                "%s: %s %s pref: %s %s%n",
                pane,
                r.getWidth(),
                r.getHeight(),
                r.getPrefWidth(),
                r.getPrefHeight()
        );
    }

}
