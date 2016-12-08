package stud.opencv.server;

import stud.opencv.server.gui.PropertiesPanel;
import stud.opencv.server.gui.StreamPanel;
import stud.opencv.server.network.properties.PropertiesServer;
import stud.opencv.server.network.stream.StreamServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static stud.opencv.server.AppState.*;

public class MainFrame extends JFrame {

    private static MainFrame instance;

    public static void repackAndFix() {
        EventQueue.invokeLater(() -> {
            instance.pack();
        });
    }

    private final StreamPanel streamPanel;
    private final StreamServer streamServer;
    private final PropertiesPanel propertiesPanel;
    private final PropertiesServer propertiesServer;

    public MainFrame(int port) {
        instance = this;
        streamPanel = new StreamPanel();
        streamServer = new StreamServer(this.streamPanel, port);
        propertiesPanel = new PropertiesPanel();
        propertiesServer = new PropertiesServer(this.propertiesPanel, port);
        propertiesPanel.setCallback(propertiesServer);
        initComponents();
    }

    private void startServers() {
        streamServer.start();
        propertiesServer.startAsync();
    }

    private void initComponents() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                alive = false;
                streamServer.stopAll();
                propertiesServer.stopAll();
                EventQueue.invokeLater(() -> {
                    for (Frame frame : Frame.getFrames()) {
                        frame.dispose();
                    }
                });
                System.exit(0);
            }
        });
        setTitle("OpenCV Server");
        setMinimumSize(new Dimension(484 + PropertiesPanel.PROP_WIDTH, 279));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(streamPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(propertiesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(propertiesPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(streamPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null); //set frame location to center
    }

    public static void main(String[] args) {
        try {
            int port;
            if(args.length < 1) {
                port = 2016;
            } else {
                port = Integer.parseInt(args[0]);
                if (port < 0 || port >= 65536) {
                    System.err.println("port must be between 0 and 65536");
                    return;
                }
            }
            mainGui(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private static void mainGui(int port) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        EventQueue.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(port);
            mainFrame.setVisible(true);
            EventQueue.invokeLater(mainFrame::startServers);
        });
    }

}
