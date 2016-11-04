package stud.opencv.server.network.properties.protocol;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public abstract class OutPacket extends Packet {

    public OutPacket(int id) {
        super(id);
    }

    public abstract void write(DataOutputStream dos) throws IOException;

}
