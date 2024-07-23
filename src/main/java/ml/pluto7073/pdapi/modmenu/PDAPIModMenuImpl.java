package ml.pluto7073.pdapi.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PDAPIModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("title.pdapi.config"))
                    .setSavingRunnable(() -> {})
                    .setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/gui/options_background.png"));

            ConfigCategory client = builder.getOrCreateCategory(Component.literal("Client"));
            ConfigCategory server = builder.getOrCreateCategory(Component.literal("Server"));

            return builder.build();
        };
    }
}
