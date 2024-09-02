package ml.pluto7073.pdapi.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class BasicSingleStorage extends SingleFluidStorage {

    public final FluidVariant allowedVariant;
    public final long capacity;
    protected final @Nullable Runnable onChanged;

    public BasicSingleStorage(FluidVariant allowedVariant, long capacity, @Nullable Runnable onSave) {
        this.allowedVariant = allowedVariant;
        this.capacity = capacity;
        this.onChanged = onSave;
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return capacity;
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return variant.equals(allowedVariant);
    }

    @Override
    protected void onFinalCommit() {
        if (onChanged != null) onChanged.run();
    }
}
