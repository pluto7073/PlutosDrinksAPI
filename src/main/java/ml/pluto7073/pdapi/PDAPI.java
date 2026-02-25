package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializers;
import ml.pluto7073.pdapi.addition.chemicals.CaffeineHandler;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.client.gui.PDScreens;
import ml.pluto7073.pdapi.config.PDCommonConfig;
import ml.pluto7073.pdapi.entity.effect.PDMobEffects;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBaseSerializer;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PDAPI implements ModInitializer {

    public static final String ID = "pdapi";
    public static final Logger LOGGER = LogManager.getLogger("PDAPI");
    public static final ResourceKey<CreativeModeTab> SPECIALTY_DRINKS_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, asId("specialty_drinks"));
    public static final ServerConfigType<PDCommonConfig> CONFIG_TYPE =
            Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, asId("common"), new ServerConfigType<>(PDCommonConfig.INSTANCE, PDCommonConfig::new));
    public static final DrinkAdditionManager SERVER_ADDITION_MANAGER = new DrinkAdditionManager();
    public static final SpecialtyDrinkManager SERVER_SPECIALITY_DRINK_MANAGER = new SpecialtyDrinkManager();


    @Override
    public void onInitialize() {
        OnDrinkSerializers.init();
        SpecialtyDrinkBaseSerializer.init();
        PDRecipeTypes.init();
        CaffeineHandler.init();
        PDBlocks.init();
        PDItems.init();
        PDMobEffects.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(SERVER_ADDITION_MANAGER);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(SERVER_SPECIALITY_DRINK_MANAGER);

        PDScreens.init();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SPECIALTY_DRINKS_TAB, FabricItemGroup.builder().icon(() -> new ItemStack(PDItems.ICON))
                .title(Component.translatable("creative_tab.pdapi.specialty_drinks")).build());
        ItemGroupEvents.modifyEntriesEvent(SPECIALTY_DRINKS_TAB).register(stacks -> {
            if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
            if (Minecraft.getInstance().level == null) return;
            for (SpecialtyDrink d : Minecraft.getInstance().level.getSpecialtyDrinkManager().values()
                    .stream().sorted(DrinkUtil.alphabetizer(drink -> drink.languageKey(Minecraft.getInstance().level))).toList()) {
                stacks.accept(d.getAsItem(Minecraft.getInstance().level));
            }
        });

        LOGGER.info("Pluto's Drinks API ready!");
    }

    public static ResourceLocation asId(String name) {
        return new ResourceLocation(ID, name);
    }

}
