package stud.opencv.server.network.properties.protocol.out;

import stud.opencv.server.network.properties.protocol.OutPacket;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public class PingPacket extends OutPacket {

    public static final int ID = 0xFF;
    private final long time;

    public PingPacket(long time) {
        super(ID);
        this.time = time;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeLong(time);
    }

    @Override
    public String toString() {
        return String.format("PingPacket{time=%d}", time);
    }
}
