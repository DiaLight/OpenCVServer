package stud.opencv.server.network.properties.protocol.in;

import stud.opencv.server.network.properties.protocol.InPacket;
import stud.opencv.server.network.properties.protocol.structs.Property;
import stud.opencv.server.network.properties.protocol.structs.PropertyType;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public class AddPropertyPacket extends InPacket {

    public static final int ID = 0x01;

    private String key;
    private Property value;

    public AddPropertyPacket() {
        super(ID);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        key = dis.readUTF();
        int id = dis.readByte();
        value = PropertyType.fromId(id);
        if(value == null) throw new IOException("Bad property id: " + id);
        value.read(dis);
    }

    public String getKey() {
        return key;
    }

    public Property getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("AddPropertyPacket{key='%s', value=%s}", key, value);
    }
}
