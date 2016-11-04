package stud.opencv.server.network.properties;

import stud.opencv.server.network.properties.protocol.InPacket;
import stud.opencv.server.network.properties.protocol.OutPacket;
import stud.opencv.server.network.properties.protocol.Protocol;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Created by dialight on 04.11.16.
 */
public abstract class TCPPacketServer extends TCPSocketServer {

    protected final Protocol protocol = new Protocol();

    private final Lock lock = new ReentrantLock();
    private Consumer<InPacket> inHandler = p -> {};
    private Consumer<OutPacket> outHandler = p -> {};

    public TCPPacketServer(int port) {
        super(port);
    }

    protected final void registerInPacketHandler(Consumer<InPacket> inHandler) {
        this.inHandler = inHandler;
    }
    protected final void registerOutPacketHandler(Consumer<OutPacket> outHandler) {
        this.outHandler = outHandler;
    }

    protected final void processPacket() throws IOException {
        int id = in.readUnsignedByte();
        InPacket inPacket = protocol.newInPacketInstance(id);
        if(inPacket == null) throw new IOException("Unregistered packet id: " + id);
        inPacket.read(in);
        inHandler.accept(inPacket);
    }

    public final void trySendPacket(OutPacket packet) {
        if(closed) return;
        try {
            sendPacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    protected void sendPacket(OutPacket packet) throws IOException {
        lock.lock();
        try {
            outHandler.accept(packet);
            out.writeByte(packet.getId());
            packet.write(this.out);
        } finally {
            lock.unlock();
        }
    }

}
