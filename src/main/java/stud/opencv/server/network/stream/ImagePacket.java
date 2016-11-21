package stud.opencv.server.network.stream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static stud.opencv.server.network.stream.StreamServer.bufSize;

/**
 * Created by DiaLight on 23.10.2016.
 */
public class ImagePacket {

    private final Map<Integer, byte[]> buffer = new HashMap<>();
    private int size = -1;
    private int parts = -1;

    public ImagePacket() {
    }

    public boolean complete() {
        return parts == buffer.size();
    }

    public void read(DataInputStream dis) throws IOException {
        int i = dis.read();
        if(i == 0) {
            size = dis.readInt();
            int left = (size + 4) % (bufSize - 8 - 1);
            parts = (size + 4) / (bufSize - 8 - 1);
            if(left != 0) parts++;
        }
        byte[] part = new byte[dis.available()];
        dis.read(part, 0, part.length);
        buffer.put(i, part);
    }

    public BufferedImage getImage() {
        byte[] buf = new byte[size];
        int bufPos = 0;
        for (int i = 0; i < parts; i++) {
            byte[] part = buffer.remove(i);
            if(part == null) return null;
            int partSize = part.length;
            if(bufPos + part.length > size) partSize = size - bufPos;
            System.arraycopy(part, 0, buf, bufPos, partSize);
            bufPos += partSize;
        }

        try {
            return ImageIO.read(new ByteArrayInputStream(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
