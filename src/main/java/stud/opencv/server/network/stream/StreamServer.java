package stud.opencv.server.network.stream;

import stud.opencv.server.gui.StreamPanel;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DiaLight on 27.10.2016.
 */
public class StreamServer extends Thread {

    private static final boolean PROFILE = false;
    public static final int bufSize = 64 * 1024 - 20 - 8 - 1;
    private final Map<Long, ImagePacket> frameMap = new HashMap<>();

    private final StreamPanel streamPanel;
    private final int port;

    public StreamServer(StreamPanel streamPanel, int port) {
        super("Stream server thread");
        this.streamPanel = streamPanel;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            byte[] buf = new byte[bufSize];
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            DataInputStream dis = new DataInputStream(bis);
            long start = System.currentTimeMillis();
            while (true) {
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
                    streamPanel.apply(imagePacket.getImage());
                    frameMap.remove(frameIndex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
