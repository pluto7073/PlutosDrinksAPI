package ml.pluto7073.pdapi.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.world.GameRules;

public class PDGameRules {

    public static final GameRules.Key<GameRules.BooleanRule> DO_CAFFEINE_OVERDOSE =
            GameRuleRegistry.register("doCaffeineOverdose", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> CAFFEINE_VISIBLE_TO_NON_OPS =
            GameRuleRegistry.register("caffeineVisibleToNonOps", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<DoubleRule> CAFFEINE_DAMAGE_MODIFIER =
            GameRuleRegistry.register("caffeineDamageMultiplier", GameRules.Category.PLAYER, GameRuleFactory.createDoubleRule(1.0));
    public static final GameRules.Key<GameRules.IntRule> LETHAL_CAFFEINE_DOSE =
            GameRuleRegistry.register("lethalCaffeineDose", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(3000));

    public static void init() {}

}
