package ml.pluto7073.pdapi.internal;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class WrappedTooltipContext implements Item.TooltipContext {

    private final Level level;
    private final Item.TooltipContext wrapped;

    public WrappedTooltipContext(Level level, Item.TooltipContext wrapped) {
        this.level = level;
        this.wrapped = wrapped;
    }

    @Override
    public @Nullable HolderLookup.Provider registries() {
        return wrapped.registries();
    }

    @Override
    public float tickRate() {
        return wrapped.tickRate();
    }

    @Override
    public @Nullable MapItemSavedData mapData(MapId mapId) {
        return wrapped.mapData(mapId);
    }

    @Override
    public DrinkAdditionManager getDrinkAdditionManager() {
        return level.getDrinkAdditionManager();
    }

    @Override
    public SpecialtyDrinkManager getSpecialtyDrinkManager() {
        return level.getSpecialtyDrinkManager();
    }
}
