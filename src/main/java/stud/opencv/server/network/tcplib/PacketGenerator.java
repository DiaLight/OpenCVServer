package stud.opencv.server.network.tcplib;

import java.io.IOException;

/**
 * Created by DiaLight on 24.01.2017.
 */
public interface PacketGenerator<Packet extends IPacket> {

    Packet newPacket(int id) throws IOException;

}
