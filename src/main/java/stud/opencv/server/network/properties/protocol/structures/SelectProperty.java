package stud.opencv.server.network.properties.protocol.structures;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dialight on 03.11.16.
 */
public class SelectProperty implements Property {

    private HashMap<Integer, String> selections;
    private int selected;

    public SelectProperty() {
    }

    public SelectProperty(int selected) {
        this.selected = selected;
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        int size = dis.readByte();
        selections = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            int key = dis.readByte();
            String var = dis.readUTF();
            selections.put(key, var);
        }
        selected = dis.readByte();
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeByte(selected);
    }

    @Override
    public PropertyType getType() {
        return PropertyType.SELECT;
    }

    public HashMap<Integer, String> getSelections() {
        return selections;
    }

    public int getSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return String.format("selected=%d, %s", selected, selections);
    }
}
