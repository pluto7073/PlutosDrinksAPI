package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.client.gui.PDScreens;
import ml.pluto7073.pdapi.command.PDCommands;
import ml.pluto7073.pdapi.entity.effect.PDMobEffects;
import ml.pluto7073.pdapi.gamerule.PDGameRules;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.listeners.DrinkAdditionRegisterer;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
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
        PDMobEffects.init();
        PDGameRules.init();
        PDCommands.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new DrinkAdditionRegisterer());

        DrinkUtil.registerOldToNewConverter("Coffee/Additions", (DrinkUtil.Converter<ListTag>) list -> {
            if (list == null) return null;
            if (list.isEmpty()) return null;
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation id = new ResourceLocation(list.getString(i));
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

    public static ResourceLocation asId(String name) {
        return new ResourceLocation(ID, name);
    }

}
