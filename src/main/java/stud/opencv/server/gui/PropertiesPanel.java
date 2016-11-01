package stud.opencv.server.gui;

import stud.opencv.server.network.properties.PropertiesServer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.BorderFactory.createBevelBorder;

/**
 * Created by DiaLight on 26.10.2016.
 */
public class PropertiesPanel extends JPanel {

    private final Map<String, JSpinner> properties = new HashMap<>();

    private PropertiesServer callback;
    public void setCallback(PropertiesServer callback) {this.callback = callback;}

    public PropertiesPanel() {
        initComponents();
    }

    private JLabel connectionInfo;
    private JLabel ping;
    private JPanel contentPanel;

    private GroupLayout propertiesPanelLayout;
    private GroupLayout.ParallelGroup horizPropGroup;
    private GroupLayout.SequentialGroup vertPropGroup;

    private void initComponents() {

        setBackground(new Color(102, 102, 102));

        connectionInfo = new JLabel();
        ping = new JLabel();
        contentPanel = new JPanel();

        contentPanel.setBackground(new Color(102, 102, 102));
        contentPanel.setBorder(createBevelBorder(BevelBorder.RAISED));

        propertiesPanelLayout = new GroupLayout(contentPanel);
        contentPanel.setLayout(propertiesPanelLayout);

        GroupLayout propertiesServerPanelLayout = new GroupLayout(this);
        this.setLayout(propertiesServerPanelLayout);
        propertiesServerPanelLayout.setHorizontalGroup(
                propertiesServerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, propertiesServerPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(propertiesServerPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(connectionInfo, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                                        .addComponent(ping, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                                        .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        propertiesServerPanelLayout.setVerticalGroup(
                propertiesServerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(propertiesServerPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(connectionInfo)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ping)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
    }

    public void clear() {
        EventQueue.invokeLater(() -> {
            properties.clear();
            contentPanel.removeAll();
            this.horizPropGroup = propertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
            propertiesPanelLayout.setHorizontalGroup(
                    propertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(propertiesPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(this.horizPropGroup)
                                    .addContainerGap())
            );
            this.vertPropGroup = propertiesPanelLayout.createSequentialGroup();
            propertiesPanelLayout.setVerticalGroup(
                    propertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(propertiesPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(this.vertPropGroup)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        });
    }

    public void setConnection(String connection) {
        EventQueue.invokeLater(() -> {
            connectionInfo.setText(connection);
        });
    }

    public void put(String key, int value) {
        EventQueue.invokeLater(() -> {
            JLabel propKey = new JLabel(key);
            JSpinner propValue = properties.get(key);
            if(propValue == null) {
                propValue = new JSpinner();
                propValue.setValue(value);
                properties.put(key, propValue);
                propValue.addChangeListener(e -> {
                    int newVal = (int) ((JSpinner) e.getSource()).getValue();
                    callback.setProperty(key, newVal);
                });
                this.horizPropGroup.addGroup(propertiesPanelLayout.createSequentialGroup()
                        .addComponent(propKey, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(propValue, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE));
                this.vertPropGroup
                        .addGroup(propertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(propKey)
                                .addComponent(propValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            } else {
                propValue.setValue(value);
            }
        });
    }

    public void pong(long time) {
        if(time < 0) {
            ping.setText("");
        } else {
            ping.setText(String.format("ping: %d", System.currentTimeMillis() - time));
        }
    }
}
