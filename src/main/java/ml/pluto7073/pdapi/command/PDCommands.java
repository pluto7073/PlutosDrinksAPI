package ml.pluto7073.pdapi.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import ml.pluto7073.pdapi.DrinkUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PDCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("drink")
                .then(literal("getMyCaffeine").executes(context -> {
                    ServerCommandSource source = context.getSource();
                    if (!source.isExecutedByPlayer()) {
                        source.sendError(Text.literal("Must be executed by a player"));
                        return -1;
                    }
                    float caffeine = DrinkUtil.getPlayerCaffeine(source.getPlayerOrThrow());
                    source.sendFeedback(() -> Text.translatable("command.getMyCaffeine.response", (int) caffeine), true);
                    return 1;
                }))
                .then(literal("getPlayerCaffeine")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("target", EntityArgumentType.player()).executes(context -> {
                            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                            context.getSource().sendFeedback(() -> Text.translatable("command.getPlayerCaffeine.response", target.getName(), DrinkUtil.getPlayerCaffeine(target)), true);
                            return 1;
                        }))
                )
                .then(literal("setCaffeine")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("target", EntityArgumentType.player())
                            .then(argument("amount", IntegerArgumentType.integer(0))
                            .executes(context -> {
                                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                                int amount = IntegerArgumentType.getInteger(context, "amount");
                                DrinkUtil.setPlayerCaffeine(target, amount);
                                return 1;
                            }))))
        ));
    }

}
