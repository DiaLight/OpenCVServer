package stud.opencv.server.network.properties.protocol.in;

import stud.opencv.server.network.properties.protocol.InPacket;
import stud.opencv.server.network.properties.protocol.structures.Property;
import stud.opencv.server.network.properties.protocol.structures.PropertyType;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dialight on 03.11.16.
 */
public class AllPropertiesPacket extends InPacket {

    public static final int ID = 0x00;

    private final Map<String, Property> props = new HashMap<>();

    public AllPropertiesPacket() {
        super(ID);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        int size = dis.readShort();
        for (int i = 0; i < size; i++) {
            String key = dis.readUTF();
            int id = dis.readByte();
            Property value = PropertyType.fromId(id);
            if(value == null) throw new IOException("Bad property id: " + id);
            value.read(dis);
            props.put(key, value);
        }
    }

    public Map<String, Property> getProperties() {
        return props;
    }

    @Override
    public String toString() {
        return String.format("AllPropertiesPacket{props=%s}",
                props.isEmpty() ? "{}" : props.entrySet().stream()
                        .map(e -> String.format("%s = %s", e.getKey(), e.getValue()))
                        .collect(Collectors.joining("\n   ", "{\n", "\n}"))
        );
    }
}
