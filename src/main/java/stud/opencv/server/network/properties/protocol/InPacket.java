package stud.opencv.server.network.properties.protocol;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public abstract class InPacket extends Packet {

    public InPacket(int id) {
        super(id);
    }

    public abstract void read(DataInputStream dis) throws IOException;

}
