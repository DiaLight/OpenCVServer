package stud.opencv.server.network.tcplib;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by DiaLight on 23.01.2017.
 */
public abstract class Connection<Packet extends IPacket> {

    private final int id;
    private final Socket socket;
    private final PacketGenerator<Packet> packetGenerator;
    private DataOutputStream dos;
    private boolean alive = false;
    private DataInputStream dis;

    public Connection(int id, Socket socket, PacketGenerator<Packet> packetGenerator) {
        this.id = id;
        this.socket = socket;
        this.packetGenerator = packetGenerator;
    }

    public void open() throws IOException {
        if(alive) return;
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(socket.getOutputStream());
        alive = true;
    }

    public void loop() {
        try {
            while (alive) {
                int id = dis.read();
                if(id == -1) throw new IOException("End of stream");
                Packet packet = packetGenerator.newPacket(id);
                packet.read(dis);
                onPacketReceived(packet);
                if(closePacketHandler(packet)) break;
            }
            this.dis.close();
            this.dos.close();
            this.socket.close();
            alive = false;
        } catch (Exception e) {
            forceClose(e);
        }
    }

    protected abstract void onPacketReceived(Packet packet) throws IOException;
    protected abstract void onPacketSent(Packet packet) throws IOException;

    protected abstract boolean closePacketHandler(Packet packet) throws IOException;
    protected abstract void sendClosePacket() throws IOException;

    protected abstract void onException(Exception e);

    public void close() {
        if(!alive) return;
        alive = false;
        try {
            sendClosePacket();
        } catch (IOException e) {
            forceClose(e);
        }
    }

    private void forceClose(Exception e) {
//        logger.log("*** FORCE CLOSE ***");
        onException(e);
        try {this.dis.close();} catch (IOException ignored) {}
        try {this.dos.close();} catch (IOException ignored) {}
        try {this.socket.close();} catch (IOException ignored) {}
    }

    public void sendPacket(Packet packet) throws IOException {
        if(alive) sendPacketImpl(packet);
        else onException(new IOException("Packet doesn't sent, cause connection is closing. " + packet.toString()));
    }

    protected final void sendPacketImpl(Packet packet) throws IOException {
        dos.write(packet.getId());
        packet.write(dos);
        onPacketSent(packet);
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("connection: id=%d, address=%s", id, socket.getInetAddress());
    }

}
