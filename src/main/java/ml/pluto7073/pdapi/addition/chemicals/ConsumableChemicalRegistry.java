package ml.pluto7073.pdapi.addition.chemicals;

import java.util.*;
import java.util.function.Consumer;

public final class ConsumableChemicalRegistry {

    private static final HashMap<String, ConsumableChemicalHandler> REGISTRY = new HashMap<>();

    public static final ConsumableChemicalHandler CAFFEINE = register(new CaffeineHandler());

    public static ConsumableChemicalHandler register(ConsumableChemicalHandler handler) {
        REGISTRY.put(handler.getName(), handler);
        return handler;
    }

    public static ConsumableChemicalHandler get(String name) {
        return REGISTRY.get(name);
    }

    public static void forEach(Consumer<ConsumableChemicalHandler> handlerConsumer) {
        REGISTRY.values().forEach(handlerConsumer);
    }

    public static Set<String> ids() {
        return REGISTRY.keySet();
    }

    public static void fillChemicalMap(Map<String, Integer> chemicals) {
        forEach(handler -> {
            if (!chemicals.containsKey(handler.getName()))
                chemicals.put(handler.getName(), 0);
        });
    }

}
