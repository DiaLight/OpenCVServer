package stud.opencv.server.network.properties.protocol.structs;

import java.util.function.Supplier;

/**
 * Created by dialight on 03.11.16.
 */
public enum PropertyType {

    INT(IntProperty::new),
    DOUBLE(DoubleProperty::new),
    SELECT(SelectProperty::new);

    private final Supplier<Property> constructor;

    PropertyType(Supplier<Property> constructor) {
        this.constructor = constructor;
    }

    public Property newInstance() {
        return constructor.get();
    }

    public static Property fromId(int id) {
        PropertyType[] values = PropertyType.values();
        if(id < 0) return null;
        if(id >= values.length) return null;
        return values[id].newInstance();
    }
}
