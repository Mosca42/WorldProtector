package fr.mosca421.worldprotector.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.data.RegionManager;
import fr.mosca421.worldprotector.util.RegionPlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Collection;

public class CommandPlayer {

    public static final LiteralArgumentBuilder<CommandSource> PLAYER_COMMAND = register();

    // TODO:
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal(Command.PLAYER.toString())
                .executes(ctx -> giveHelp(ctx.getSource()))
                .then(Commands.literal(Command.HELP.toString())
                        .executes(ctx -> giveHelp(ctx.getSource())))
                .then(Commands.literal(Command.ADD.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players()) // TODO: suggest all players - players already existing in the region
                                        .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionFlag.getFlags(), builder))
                                        .executes(ctx -> add(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.REMOVE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players()) // TODO: suggest only players already existing in the region
                                        .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getRegionFlags(ctx.getArgument(Command.REGION.toString(), String.class), ctx.getSource().getWorld().getDimensionKey()), builder))
                                        .executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.INFO.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .executes(ctx -> info(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))));
    }

    private static int remove(CommandSource source, String regionName, Collection<ServerPlayerEntity> players) {
        return 0;
    }

    private static int add(CommandSource source, String regionName, Collection<ServerPlayerEntity> players) {

        return 0;
    }

    private static int info(CommandSource source, String string) {
        try {
            RegionPlayerUtils.listPlayersInRegion("", source.asPlayer());
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int giveHelp(CommandSource source) {
        try {
            RegionPlayerUtils.giveHelpMessage(source.asPlayer());
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
