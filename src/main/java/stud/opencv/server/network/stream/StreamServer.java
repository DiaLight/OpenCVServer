package stud.opencv.server.network.stream;

import stud.opencv.server.gui.StreamPanel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static stud.opencv.server.AppState.*;

/**
 * Created by DiaLight on 27.10.2016.
 */
public class StreamServer extends Thread {

    private static final boolean PROFILE = false;
    public static final int bufSize = 64 * 1024 - 20 - 8 - 1;
    private final Map<Long, ImagePacket> frameMap = new HashMap<>();

    private Consumer<ImagePacket> handler = i -> {};
    private final int port;
    private DatagramSocket socket;

    public StreamServer(int port) {
        super("Stream server thread");
        this.port = port;
    }

    public void setHandler(Consumer<ImagePacket> handler) {
        if(handler == null) handler = i -> {};
        this.handler = handler;
    }

    @Override
    public void run() {

        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            byte[] buf = new byte[bufSize];
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            DataInputStream dis = new DataInputStream(bis);
            long start = System.currentTimeMillis();
            while (alive) {
                //System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                //Receive a packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                if(PROFILE) {
                    System.out.printf("\rPacket[%dms]",
                            System.currentTimeMillis() - start
                    );
                    start = System.currentTimeMillis();
                }

                long frameIndex = dis.readLong();
                ImagePacket imagePacket = frameMap.get(frameIndex);
                if(imagePacket == null) frameMap.put(frameIndex, (imagePacket = new ImagePacket()));
                imagePacket.read(dis);
                bis.reset();
                if(imagePacket.complete()) {
                    handler.accept(imagePacket);
                    frameMap.remove(frameIndex);
                }
            }
        } catch (SocketException e) {
            System.out.println("Stream server closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAll() {
        socket.close();
    }
}
