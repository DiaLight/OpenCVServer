package stud.opencv.server.network.tcplib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by DiaLight on 01.02.2017.
 */
public interface IPacket {
    int getId();
    void read(DataInputStream dis) throws IOException;
    void write(DataOutputStream dos) throws IOException;
}
