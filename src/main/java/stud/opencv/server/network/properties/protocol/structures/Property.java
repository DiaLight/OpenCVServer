package stud.opencv.server.network.properties.protocol.structures;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public abstract class Property {

    public abstract void read(DataInputStream dis) throws IOException;
    public abstract void write(DataOutputStream dos) throws IOException;

    public abstract PropertyType getType();

}
