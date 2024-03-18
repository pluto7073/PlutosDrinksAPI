package ml.pluto7073.pdapi.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;

public class PDTrackedData {

    public static final TrackedData<Float> PLAYER_CAFFEINE_AMOUNT;
    public static final TrackedData<Float> PLAYER_ORIGINAL_CAFFEINE_AMOUNT;
    public static final TrackedData<Integer> PLAYER_TICKS_SINCE_LAST_CAFFEINE;

    public static void init() {}

    static {
        PLAYER_CAFFEINE_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
        PLAYER_ORIGINAL_CAFFEINE_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
        PLAYER_TICKS_SINCE_LAST_CAFFEINE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

}
