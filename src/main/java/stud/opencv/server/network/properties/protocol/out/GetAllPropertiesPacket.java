package stud.opencv.server.network.properties.protocol.out;

import stud.opencv.server.network.properties.protocol.OutPacket;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dialight on 03.11.16.
 */
public class GetAllPropertiesPacket extends OutPacket {

    public static final int ID = 0x00;

    public GetAllPropertiesPacket() {
        super(ID);
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public String toString() {
        return "GetAllPropertiesPacket{}";
    }
}
