package ml.pluto7073.pdapi.addition;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DrinkAdditions {

    public static final Map<Identifier, DrinkAddition> REGISTRY = new HashMap<>();
    public static final String ADDITIONS_NBT_KEY = "Additions";

    public static final DrinkAddition EMPTY = register(PDAPI.asId("empty"), new DrinkAddition(new OnDrink[0], false, 0, 0, 0));

    public static DrinkAddition register(Identifier id, DrinkAddition addition) {
        REGISTRY.put(id, addition);
        return addition;
    }

    public static Identifier getId(DrinkAddition addition) {
        for (Map.Entry<Identifier, DrinkAddition> entry : REGISTRY.entrySet()) {
            if (addition.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return PDAPI.asId("empty");
    }

    public static DrinkAddition get(Identifier id) {
        if (!REGISTRY.containsKey(id)) return EMPTY;
        return REGISTRY.get(id);
    }

    public static boolean containsId(Identifier id) {
        return REGISTRY.containsKey(id);
    }

}
