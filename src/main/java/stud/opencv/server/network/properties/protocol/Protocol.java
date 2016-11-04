package stud.opencv.server.network.properties.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by dialight on 03.11.16.
 */
public class Protocol {

    private final Map<Integer, Supplier<InPacket>> registry = new HashMap<>();

    public void register(int id, Supplier<InPacket> constructor) {
        registry.put(id, constructor);
    }

    public InPacket newInPacketInstance(int id) {
        Supplier<InPacket> constructor = registry.get(id);
        if(constructor == null) return null;
        return constructor.get();
    }
}
