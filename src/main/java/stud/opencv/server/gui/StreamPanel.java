package stud.opencv.server.gui;

import stud.opencv.server.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

/**
 * Created by DiaLight on 27.10.2016.
 */
public class StreamPanel extends JPanel {

    private BufferedImage image;
    private long lastUpdate = -1;

    public StreamPanel() {
        initComponents();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                MainFrame.repackAndFix();
            }
        });
    }

    private void initComponents() {

        setBackground(new Color(102, 102, 102));
        setPreferredSize(new Dimension(320, 240));

        GroupLayout streamPanelLayout = new GroupLayout(this);
        this.setLayout(streamPanelLayout);
        streamPanelLayout.setHorizontalGroup(
                streamPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 320, Short.MAX_VALUE)
        );
        streamPanelLayout.setVerticalGroup(
                streamPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 240, Short.MAX_VALUE)
        );
    }

    @Override
    public void paint(Graphics g) {
        if (image == null) {
            super.paint(g);
            if(lastUpdate > 0) lastUpdate = -1;
            return;
        }
        g.drawImage(image, 0, 0, this);
        if(lastUpdate > 0) {
            long fps = System.currentTimeMillis() - lastUpdate;
            g.setColor(Color.RED);
            g.drawString(String.format("FPS: %d", fps), 10, 20);
        }
        lastUpdate = System.currentTimeMillis();
    }

    public void apply(BufferedImage img) {
        this.image = img;
        if(img == null) {
            repaint();
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        Dimension prefSize = getPreferredSize();
        repaint();
        if(prefSize.width != width || prefSize.height != height) {
            Dimension size = new Dimension(width, height);
            setPreferredSize(size);
        }
    }

}
