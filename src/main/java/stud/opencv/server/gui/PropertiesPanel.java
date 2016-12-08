package stud.opencv.server.gui;

import stud.opencv.server.network.properties.PropertiesServer;
import stud.opencv.server.network.properties.protocol.out.ChangePropertyPacket;
import stud.opencv.server.network.properties.protocol.structures.DoubleProperty;
import stud.opencv.server.network.properties.protocol.structures.IntProperty;
import stud.opencv.server.network.properties.protocol.structures.Property;
import stud.opencv.server.network.properties.protocol.structures.SelectProperty;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.swing.BorderFactory.createBevelBorder;

/**
 * Created by DiaLight on 26.10.2016.
 */
public class PropertiesPanel extends JPanel {

    public static final int PROP_WIDTH = 150;

    private final Map<String, JComponent> properties = new HashMap<>();

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

    public void pong(long time) {
        if(time < 0) {
            ping.setText("");
        } else {
            ping.setText(String.format("ping: %d", System.currentTimeMillis() - time));
        }
    }

    public void put(String key, Property value) {
        switch (value.getType()) {
            case INT:
                IntProperty intProperty = (IntProperty) value;
                putInt(key, intProperty.get());
                break;
            case DOUBLE:
                DoubleProperty doubleProperty = (DoubleProperty) value;
                putDouble(key, doubleProperty.get());
                break;
            case SELECT:
                SelectProperty selectProperty = (SelectProperty) value;
                putSelect(key, selectProperty.getSelected(), selectProperty.getSelections());
                break;
        }
    }

    private void putInt(String key, int value) {
        EventQueue.invokeLater(() -> {
            JLabel propKey = new JLabel(key);
            JSpinner propValue = (JSpinner) properties.get(key);
            if(propValue == null) {
                propValue = new JSpinner();
                propValue.setValue(value);
                properties.put(key, propValue);
                propValue.addChangeListener(e -> {
                    int newVal = (int) ((JSpinner) e.getSource()).getValue();
                    callback.trySendPacket(new ChangePropertyPacket(key, new IntProperty(newVal)));
                });
                addProperty(propKey, propValue);
            } else {
                propValue.setValue(value);
            }
        });
    }

    private void addProperty(JLabel propKey, JComponent propValue) {
        this.horizPropGroup.addGroup(propertiesPanelLayout.createSequentialGroup()
                .addComponent(propKey, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(propValue, GroupLayout.PREFERRED_SIZE, PROP_WIDTH, GroupLayout.PREFERRED_SIZE));
        this.vertPropGroup
                .addGroup(propertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(propKey)
                        .addComponent(propValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
    }

    private void putDouble(String key, double value) {
        EventQueue.invokeLater(() -> {
            JLabel propKey = new JLabel(key);
            JFormattedTextField propValue = (JFormattedTextField) properties.get(key);
            if(propValue == null) {
                propValue = new JFormattedTextField(NumberFormat.getNumberInstance());
                propValue.setValue(value);
                properties.put(key, propValue);
                propValue.addActionListener(e -> {
                    Object val = ((JFormattedTextField) e.getSource()).getValue();
                    double newVal;
                    if(val instanceof Long) {
                        newVal = (Long) val;
                    } else {
                        newVal = (double) val;
                    }
                    callback.trySendPacket(new ChangePropertyPacket(key, new DoubleProperty(newVal)));
                });
                addProperty(propKey, propValue);
            } else {
                propValue.setValue(value);
            }
        });
    }

    private void putSelect(String key, int selected, HashMap<Integer, String> selections) {
        EventQueue.invokeLater(() -> {
            JLabel propKey = new JLabel(key);
            JComboBox<String> propValue = (JComboBox<String>) properties.get(key);
            if(propValue == null) {
                propValue = new JComboBox<>(selections.values().toArray(new String[0]));
                propValue.setSelectedIndex(selected);
                properties.put(key, propValue);
                Integer[] keys = selections.keySet().toArray(new Integer[0]);
                propValue.addActionListener(e -> {
                    int newSel = ((JComboBox) e.getSource()).getSelectedIndex();
                    callback.trySendPacket(new ChangePropertyPacket(key, new SelectProperty(keys[newSel])));
                });
                addProperty(propKey, propValue);
            } else {
                if (propValue.getSelectedIndex() != selected) {
                    propValue.setSelectedIndex(selected);
                }
            }
        });
    }
}
