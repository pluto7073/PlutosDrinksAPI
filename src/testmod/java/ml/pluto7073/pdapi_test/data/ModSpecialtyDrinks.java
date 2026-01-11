package ml.pluto7073.pdapi_test.data;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.action.ApplyEffectRadiusAction;
import ml.pluto7073.pdapi.addition.action.ChorusTeleportAction;
import ml.pluto7073.pdapi.addition.action.RestoreHungerAction;
import ml.pluto7073.pdapi.datagen.provider.SpecialtyDrinkProvider;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi_test.PDAPITestMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;

import java.util.function.BiConsumer;

public class ModSpecialtyDrinks extends SpecialtyDrinkProvider {

    public ModSpecialtyDrinks(FabricDataOutput out) {
        super(out);
    }

    @Override
    public void buildDrinks(BiConsumer<ResourceLocation, SpecialtyDrink> output) {
        staticBaseBuilder(PDAPITestMod.TEST_ITEM)
                .step(PDAPI.asId("milk"))
                .step(PDAPI.asId("sugar"))
                .step(PDAPI.asId("chorus_fruit"))
                .color(0xFF0000)
                .chemical(PDAPI.asId("caffeine"), 1000)
                .action(new ApplyEffectRadiusAction(10, true, MobEffects.HUNGER, 20 * 600, 2))
                .action(new ChorusTeleportAction(8))
                .action(new RestoreHungerAction(4, 2))
                .name("Generated Drink!!")
                .save(PDAPITestMod.id("generated_drink"), output);
    }

}
