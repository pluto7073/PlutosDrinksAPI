package ml.pluto7073.pdapi.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.gamerule.PDGameRules;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PDCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<CommandSourceStack> drink = literal("drink");
            ConsumableChemicalRegistry.forEach(handler -> {
                LiteralArgumentBuilder<CommandSourceStack> subCommand = handler.getDrinkSubcommand();
                if (subCommand == null) return;
                drink.then(subCommand);
            });
            dispatcher.register(drink);
        });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> caffeine() {
        return literal("caffeine").then(caffeineGet()).then(caffeineSet());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> caffeineGet() {
        return literal("get")
                .requires(source -> source.hasPermission(2) || source.getLevel().getGameRules().getBoolean(PDGameRules.CAFFEINE_VISIBLE_TO_NON_OPS))
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    if (!source.isPlayer()) {
                        source.sendFailure(Component.literal("Must be executed by a player"));
                        return -1;
                    }
                    float caffeine = DrinkUtil.getPlayerCaffeine(source.getPlayerOrException());
                    source.sendSuccess(() -> Component.translatable("command.getMyCaffeine.response", (int) caffeine), true);
                    return 1;
                }).then(argument("target", EntityArgument.player()).requires(source -> source.hasPermission(2))
                        .executes(ctx -> {
                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                            ctx.getSource().sendSuccess(() -> Component.translatable("command.getPlayerCaffeine.response", target.getName(), DrinkUtil.getPlayerCaffeine(target)), true);
                            return 1;
                        }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> caffeineSet() {
        return literal("set")
                .requires(source -> source.hasPermission(2))
                .then(argument("target", EntityArgument.player())
                        .then(argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    ConsumableChemicalRegistry.CAFFEINE.set(target, amount);
                                    return 1;
                                })));
    }

}
