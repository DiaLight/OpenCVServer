package stud.opencv.server.network.properties;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dialight on 04.11.16.
 */
public abstract class TCPSocketServer {

    private final int port;
    private ServerSocket ss;
    protected DataOutputStream out;
    protected DataInputStream in;
    protected Socket socket;

    protected boolean closed = true;

    public TCPSocketServer(int port) {
        this.port = port;
    }

    protected final void bind() throws IOException {
        ss = new ServerSocket(port);
//        ss.setSoTimeout(2000);
    }
    protected final void closeServer() throws IOException {
        ss.close();
    }

    protected final void receiveConnection() throws IOException {
        socket = ss.accept();
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        closed = false;
    }

    public void closeConnection() {
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

}
