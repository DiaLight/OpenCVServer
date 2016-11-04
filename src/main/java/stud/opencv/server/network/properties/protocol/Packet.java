package stud.opencv.server.network.properties.protocol;

/**
 * Created by dialight on 03.11.16.
 */
public abstract class Packet {

    private final int id;

    public Packet(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
