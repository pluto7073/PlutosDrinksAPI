package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.client.gui.PDScreens;
import ml.pluto7073.pdapi.entity.effect.PDStatusEffects;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.listeners.DrinkAdditionRegisterer;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PDAPI implements ModInitializer {

    public static final String ID = "pdapi";
    public static final Logger LOGGER = LogManager.getLogger("PDAPI");

    @Override
    public void onInitialize() {
        PDRecipeTypes.init();
        PDBlocks.init();
        PDItems.init();
        PDStatusEffects.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DrinkAdditionRegisterer());

        DrinkUtil.registerOldToNewConverter("Coffee/Additions", (DrinkUtil.Converter<NbtList>) list -> {
            if (list == null) return null;
            if (list.isEmpty()) return null;
            for (int i = 0; i < list.size(); i++) {
                Identifier id = new Identifier(list.getString(i));
                boolean ogCoffee = !DrinkAdditions.containsId(id) && DrinkAdditions.containsId(PDAPI.asId(id.getPath()));
                if (!ogCoffee) continue;
                id = PDAPI.asId(id.getPath());
                list.set(i, DrinkUtil.stringAsNbt(id.toString()));
            }
            return list;
        });

        PDScreens.init();

        LOGGER.info("Pluto's Drinks API ready!");
    }

    public static Identifier asId(String name) {
        return new Identifier(ID, name);
    }

}
