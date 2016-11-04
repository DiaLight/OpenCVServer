package stud.opencv.server.network.properties;

import stud.opencv.server.gui.PropertiesPanel;
import stud.opencv.server.network.properties.protocol.InPacket;
import stud.opencv.server.network.properties.protocol.OutPacket;
import stud.opencv.server.network.properties.protocol.Packet;
import stud.opencv.server.network.properties.protocol.in.AddPropertyPacket;
import stud.opencv.server.network.properties.protocol.in.AllPropertiesPacket;
import stud.opencv.server.network.properties.protocol.in.PongPacket;
import stud.opencv.server.network.properties.protocol.out.GetAllPropertiesPacket;
import stud.opencv.server.network.properties.protocol.out.PingPacket;
import stud.opencv.server.network.properties.protocol.structures.Property;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by DiaLight on 27.10.2016.
 */
public class PropertiesServer extends TCPPacketServer {

    public static final boolean DEBUG = true;
    public static final List<Class<? extends Packet>> IGNORE_PACKETS = Arrays.asList(
            PingPacket.class, PongPacket.class
    );

    private final PropertiesPanel propertiesPanel;

    private Thread thread = null;

    public PropertiesServer(PropertiesPanel propertiesPanel, int port) {
        super(port);
        this.propertiesPanel = propertiesPanel;
    }

    public void startAsync() {
        if(thread != null) return;
        this.thread = new Thread(this::start, "Properties server thread");
        thread.start();
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(
                () -> trySendPacket(new PingPacket(System.currentTimeMillis())),
                3, 3, TimeUnit.SECONDS //try ping-pong every 3 seconds
        );
    }

    private void start() {
        protocol.register(AllPropertiesPacket.ID, AllPropertiesPacket::new);
        protocol.register(AddPropertyPacket.ID, AddPropertyPacket::new);
        protocol.register(PongPacket.ID, PongPacket::new);

        registerInPacketHandler(this::onPacketReceived);
        registerOutPacketHandler(this::onPacketSend);

        try {
            bind();
            try {
                while(true) {
                    try {
                        try {
                            //prepare before waiting
                            propertiesPanel.clear();
                            propertiesPanel.setConnection("waiting for connection...");
                            propertiesPanel.pong(-1);

                            receiveConnection(); //waiting for connection

                            socket.setSoTimeout(5000); //5 second timeout(ping-pong every 3 seconds)
                            propertiesPanel.setConnection(socket.getInetAddress().toString());
                            sendPacket(new GetAllPropertiesPacket());

                            while (!closed) {
                                processPacket();
                            }
                        } finally {
                            closeConnection();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                closeServer();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void onPacketSend(OutPacket packet) {
        if(DEBUG && !IGNORE_PACKETS.contains(packet.getClass())) {
            System.out.printf("Out packet(%d): %s%n", packet.getId(), packet.toString());
        }
    }

    private void onPacketReceived(InPacket packet) {
        if(DEBUG && !IGNORE_PACKETS.contains(packet.getClass())) {
            System.out.printf("In packet(%d): %s%n", packet.getId(), packet.toString());
        }
        switch (packet.getId()) {
            case AllPropertiesPacket.ID:
                AllPropertiesPacket propertiesPacket = (AllPropertiesPacket) packet;
                for (Map.Entry<String, Property> entry : propertiesPacket.getProperties().entrySet()) {
                    propertiesPanel.put(entry.getKey(), entry.getValue());
                }
                break;
            case AddPropertyPacket.ID:
                AddPropertyPacket propertyPacket = (AddPropertyPacket) packet;
                propertiesPanel.put(propertyPacket.getKey(), propertyPacket.getValue());
                break;
            case PongPacket.ID:
                PongPacket pongPacket = (PongPacket) packet;
                propertiesPanel.pong(pongPacket.getTime());
                break;
            default:
                System.err.println("Unhandled packet id: " + packet.getId());
                break;
        }
    }

}
