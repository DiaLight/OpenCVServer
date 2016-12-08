package stud.opencv.server.network.properties.protocol.structures;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public class IntProperty implements Property {

    private int value;

    public IntProperty() {
    }

    public IntProperty(int value) {
        this.value = value;
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        value = dis.readInt();
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(value);
    }

    @Override
    public PropertyType getType() {
        return PropertyType.INT;
    }

    public int get() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%d", value);
    }
}
