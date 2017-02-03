package stud.opencv.server.network.properties;

import stud.opencv.server.network.properties.protocol.structs.Property;

/**
 * Created by DiaLight on 01.02.2017.
 */
public interface PropertiesHandle {
    void clear();

    void setConnection(String connection);

    void pong(long time);

    void put(String key, Property value);

    static PropertiesHandle dummy() {
        return new PropertiesHandle() {
            public void clear() {}
            public void setConnection(String connection) {}
            public void pong(long time) {}
            public void put(String key, Property value) {}
        };
    }
}
