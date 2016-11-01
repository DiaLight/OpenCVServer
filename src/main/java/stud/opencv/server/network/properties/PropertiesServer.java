package stud.opencv.server.network.properties;

import stud.opencv.server.gui.PropertiesPanel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by DiaLight on 27.10.2016.
 */
public class PropertiesServer extends Thread {

    private final PropertiesPanel propertiesPanel;
    private final int port;

    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;

    private final Lock lock = new ReentrantLock();

    private boolean closed = false;

    public PropertiesServer(PropertiesPanel propertiesPanel, int port) {
        super("Config server thread");
        this.propertiesPanel = propertiesPanel;
        this.port = port;
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            if(closed) return;
            try {
                lock.lock();
                try {
                    out.writeByte(0xFF); //ping-pong
                    long v = System.currentTimeMillis();
                    out.writeLong(v);
                } finally {
                    lock.unlock();
                }
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            while(true) {
                receiveConnection(ss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveConnection(ServerSocket ss) {
        try {
            propertiesPanel.clear();
            propertiesPanel.setConnection("waiting for connection...");
            propertiesPanel.pong(-1);
            socket = ss.accept();
            socket.setSoTimeout(5000); //5 second timeout(ping-pong every 3 seconds)
            propertiesPanel.setConnection(socket.getInetAddress().toString());
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            lock.lock();
            try {
                out.writeByte(0); //get All
            } finally {
                lock.unlock();
            }
            closed = false;
            while (!closed) {
                byte op = in.readByte();
                switch (op) {
                    case 0:
                        propertiesPanel.clear();
                        short size = in.readShort();
                        for(int i = 0; i < size; i++) {
                            String key = in.readUTF();
                            int value = in.readInt();
                            propertiesPanel.put(key, value);
                        }
                        break;
                    case 1: //write
                        String key = in.readUTF();
                        int value = in.readInt();
                        propertiesPanel.put(key, value);
                        break;
                    case 3: //remove

                        break;
                    case (byte) 0xFF:
                        long time = in.readLong();
                        propertiesPanel.pong(time);
                        break;
                    default:
                        System.out.println("ERROR");
                        close();
                }
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
            closed = true;
        }
    }

    public void close() {
        if(closed) return;
        closed = true;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProperty(String key, int newVal) {
        try {
            lock.lock();
            try {
                out.writeByte(2);
                out.writeUTF(key);
                out.writeInt(newVal);
            } finally {
                lock.unlock();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closed = true;
        }
    }
}
