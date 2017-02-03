package stud.opencv.server.fx;

import javafx.application.Application;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by DiaLight on 01.02.2017.
 */
public class Arguments {

    private int port = 2017;

    private Arguments() {}

    public static Arguments parse(Application.Parameters parameters) {
        Map<String, String> named = parameters.getNamed(); // --key=value
        List<String> unnamed = parameters.getUnnamed();
//        System.out.println(named);
//        System.out.println(unnamed);
//        System.out.println(parameters.getRaw());
        Arguments arguments = new Arguments();

        computeIfExist(unnamed, 0, arg -> arguments.port = Integer.parseInt(arg));

        return arguments;
    }

    private static void computeIfExist(List<String> list, int index, Consumer<String> compute) {
        if(index >= 0 && index < list.size()) {
            compute.accept(list.get(index));
        }
    }

    public int getPort() {
        return port;
    }
}
