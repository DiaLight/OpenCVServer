package stud.opencv.server.network.properties.protocol.structs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public interface Property {

    void read(DataInputStream dis) throws IOException;
    void write(DataOutputStream dos) throws IOException;

    PropertyType getType();

}
