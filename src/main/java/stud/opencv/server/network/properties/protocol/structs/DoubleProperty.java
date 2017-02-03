package stud.opencv.server.network.properties.protocol.structs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public class DoubleProperty implements Property {

    private double value;

    public DoubleProperty() {
    }

    public DoubleProperty(double value) {
        this.value = value;
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        value = dis.readDouble();
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeDouble(value);
    }

    @Override
    public PropertyType getType() {
        return PropertyType.DOUBLE;
    }

    public double get() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s", value);
    }

}
