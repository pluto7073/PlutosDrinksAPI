package ml.pluto7073.pdapi.addition.action;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import net.minecraft.core.Registry;

public final class OnDrinkSerializers {

    public static final OnDrinkSerializer<ApplyStatusEffectAction> APPLY_STATUS_EFFECT =
            register("apply_status_effect", new ApplyStatusEffectAction.Serializer());
    public static final OnDrinkSerializer<ApplyEffectRadiusAction> APPLY_EFFECT_RADIUS =
            register("apply_effect_radius", new ApplyEffectRadiusAction.Serializer());
    public static final OnDrinkSerializer<DealDamageAction> DEAL_DAMAGE =
            register("deal_damage", new DealDamageAction.Serializer());
    public static final OnDrinkSerializer<RestoreHungerAction> RESTORE_HUNGER =
            register("restore_hunger", new RestoreHungerAction.Serializer());
    public static final OnDrinkSerializer<ClearHarmfulEffectsAction> CLEAR_HARMFUL_EFFECTS =
            register("clear_harmful_effects", new ClearHarmfulEffectsAction.Serializer());
    public static final OnDrinkSerializer<ChorusTeleportAction> CHORUS_TELEPORT =
            register("chorus_teleport", new ChorusTeleportAction.Serializer());

    private static <T extends OnDrinkAction> OnDrinkSerializer<T> register(String id, OnDrinkSerializer<T> serializer) {
        return Registry.register(PDRegistries.ON_DRINK_SERIALIZER, PDAPI.asId(id), serializer);
    }

    public static void init() {}

}
