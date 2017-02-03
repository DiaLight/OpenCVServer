package stud.opencv.server.network.properties;

import stud.opencv.server.network.properties.protocol.InPacket;
import stud.opencv.server.network.properties.protocol.OutPacket;
import stud.opencv.server.network.properties.protocol.Packet;
import stud.opencv.server.network.properties.protocol.in.AddPropertyPacket;
import stud.opencv.server.network.properties.protocol.in.AllPropertiesPacket;
import stud.opencv.server.network.properties.protocol.in.PongPacket;
import stud.opencv.server.network.properties.protocol.out.GetAllPropertiesPacket;
import stud.opencv.server.network.properties.protocol.out.PingPacket;
import stud.opencv.server.network.properties.protocol.structs.Property;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static stud.opencv.server.AppState.*;

/**
 * Created by DiaLight on 27.10.2016.
 */
public class PropertiesServer extends TCPPacketServer {

    public static final boolean DEBUG = true;
    public static final List<Class<? extends Packet>> IGNORE_PACKETS = Arrays.asList(
            PingPacket.class, PongPacket.class
    );

    private PropertiesHandle propertiesHandle = PropertiesHandle.dummy();

    private Thread thread = null;
    private ScheduledExecutorService pingSchedulePool = Executors.newScheduledThreadPool(1);

    public PropertiesServer(int port) {
        super(port);
    }

    public void setPropertiesHandle(PropertiesHandle propertiesHandle) {
        if(propertiesHandle == null) propertiesHandle = PropertiesHandle.dummy();
        this.propertiesHandle = propertiesHandle;
    }

    public void startAsync() {
        if(thread != null) return;
        this.thread = new Thread(this::start, "Properties server thread");
        thread.start();
        pingSchedulePool.scheduleWithFixedDelay(
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
                while(alive) {
                    try {
                        //prepare before waiting
                        propertiesHandle.clear();
                        propertiesHandle.setConnection("waiting for connection...");
                        propertiesHandle.pong(-1);

                        receiveConnection(); //waiting for connection
                        try {
                            socket.setSoTimeout(5000); //5 second timeout(ping-pong every 3 seconds)
                            propertiesHandle.setConnection(socket.getInetAddress().toString());
                            sendPacket(new GetAllPropertiesPacket());

                            while (!closed && alive) {
                                processPacket();
                            }
                        } finally {
                            closeConnection();
                        }
                    } catch (SocketException ignore) {
                        System.out.println("Properties server closed");
                        break;
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
            System.out.printf("Out packet(id=%d): %s%n", packet.getId(), packet.toString());
        }
    }

    private void onPacketReceived(InPacket packet) {
        if(DEBUG && !IGNORE_PACKETS.contains(packet.getClass())) {
            System.out.printf("In packet(id=%d): %s%n", packet.getId(), packet.toString());
        }
        switch (packet.getId()) {
            case AllPropertiesPacket.ID:
                AllPropertiesPacket propertiesPacket = (AllPropertiesPacket) packet;
                for (Map.Entry<String, Property> entry : propertiesPacket.getProperties().entrySet()) {
                    propertiesHandle.put(entry.getKey(), entry.getValue());
                }
                break;
            case AddPropertyPacket.ID:
                AddPropertyPacket propertyPacket = (AddPropertyPacket) packet;
                propertiesHandle.put(propertyPacket.getKey(), propertyPacket.getValue());
                break;
            case PongPacket.ID:
                PongPacket pongPacket = (PongPacket) packet;
                propertiesHandle.pong(pongPacket.getTime());
                break;
            default:
                System.err.println("Unhandled packet id: " + packet.getId());
                break;
        }
    }

    public void stopAll() {
        pingSchedulePool.shutdown();
        closeConnection();
        try {
            closeServer();
        } catch (IOException ignored) {}
    }
}
