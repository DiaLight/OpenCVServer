package stud.opencv.server.network.properties.protocol.out;

import stud.opencv.server.network.properties.protocol.OutPacket;
import stud.opencv.server.network.properties.protocol.structures.Property;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public class ChangePropertyPacket extends OutPacket {

    public static final int ID = 0x02;

    private final String key;
    private final Property value;

    public ChangePropertyPacket(String key, Property value) {
        super(ID);
        this.key = key;
        this.value = value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(key);
        dos.writeByte(value.getType().ordinal());
        value.write(dos);
    }

    @Override
    public String toString() {
        return String.format("ChangePropertyPacket{key='%s', value=%s}", key, value);
    }
}
