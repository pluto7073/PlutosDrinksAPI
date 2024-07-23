package ml.pluto7073.pdapi.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

public class PDTrackedData {

    public static final EntityDataAccessor<Float> PLAYER_CAFFEINE_AMOUNT;
    public static final EntityDataAccessor<Float> PLAYER_ORIGINAL_CAFFEINE_AMOUNT;
    public static final EntityDataAccessor<Integer> PLAYER_TICKS_SINCE_LAST_CAFFEINE;

    public static void init() {}

    static {
        PLAYER_CAFFEINE_AMOUNT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
        PLAYER_ORIGINAL_CAFFEINE_AMOUNT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
        PLAYER_TICKS_SINCE_LAST_CAFFEINE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    }

}
