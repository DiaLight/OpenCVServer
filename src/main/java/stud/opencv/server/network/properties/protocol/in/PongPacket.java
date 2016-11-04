package stud.opencv.server.network.properties.protocol.in;

import stud.opencv.server.network.properties.protocol.InPacket;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public class PongPacket extends InPacket {

    public static final int ID = 0xFF;
    private long time;

    public PongPacket() {
        super(ID);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        this.time = dis.readLong();
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return String.format("PongPacket{time=%d}", time);
    }

}
