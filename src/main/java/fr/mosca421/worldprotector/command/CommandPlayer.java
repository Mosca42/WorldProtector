package fr.mosca421.worldprotector.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.data.RegionManager;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.PlayerUtils;
import fr.mosca421.worldprotector.util.RegionPlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

public class CommandPlayer {

    public static final LiteralArgumentBuilder<CommandSource> PLAYER_COMMAND = register();

    // TODO: test adding/removing multiple players
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal(Command.PLAYER.toString())
                .executes(ctx -> giveHelp(ctx.getSource()))
                .then(Commands.literal(Command.HELP.toString())
                        .executes(ctx -> giveHelp(ctx.getSource())))
                .then(Commands.literal(Command.ADD.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players())
                                        .executes(ctx -> addPlayers(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.ADD.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.player())
                                        .executes(ctx -> addPlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayer(ctx, Command.PLAYER.toString()))))))
                // add offline player
                .then(Commands.literal(Command.ADD_OFFLINE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), StringArgumentType.word())
                                        .executes(ctx -> addPlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), StringArgumentType.getString(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.REMOVE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players())
                                        .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getRegionPlayers(ctx.getArgument(Command.REGION.toString(), String.class)), builder))
                                        .executes(ctx -> removePlayers(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.REMOVE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.player())
                                        .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getRegionPlayers(ctx.getArgument(Command.REGION.toString(), String.class)), builder))
                                        .executes(ctx -> removePlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayer(ctx, Command.PLAYER.toString()))))))
                // remove offline player
                .then(Commands.literal(Command.REMOVE_OFFLINE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), StringArgumentType.word())
                                        .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getRegionPlayers(ctx.getArgument(Command.REGION.toString(), String.class)), builder))
                                        .executes(ctx -> removePlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), StringArgumentType.getString(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.LIST.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
                                .suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionManager.get().getAllRegionNames(), builder))
                                .executes(ctx -> list(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))));
    }

    private static int removePlayer(CommandSource source, String regionName, ServerPlayerEntity player) {
        try {
            RegionPlayerUtils.removePlayer(regionName, source.asPlayer(), player);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int removePlayer(CommandSource source, String regionName, String playerName) {
        try {
            RegionPlayerUtils.removePlayer(regionName, source.asPlayer(), playerName);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int removePlayers(CommandSource source, String regionName, Collection<ServerPlayerEntity> players) {
        try {
            List<PlayerEntity> playerList = players.stream().map(player -> (PlayerEntity)player).collect(Collectors.toList());
            RegionPlayerUtils.removePlayers(regionName, source.asPlayer(), playerList);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int addPlayer(CommandSource source, String regionName, ServerPlayerEntity player) {
        try {
            RegionPlayerUtils.addPlayer(regionName, source.asPlayer(), player);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int addPlayer(CommandSource source, String regionName, String playerName) {
        try {
            PlayerUtils.MCPlayerInfo playerInfo = PlayerUtils.queryPlayerUUIDByName(playerName);
            if (playerInfo == null) {
                sendMessage(source.asPlayer(), new TranslationTextComponent( "message.region.playernotexisting", playerName));
                return -1;
            }
            RegionPlayerUtils.addPlayer(regionName, source.asPlayer(), playerInfo);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int addPlayers(CommandSource source, String regionName, Collection<ServerPlayerEntity> players) {
        try {
            List<PlayerEntity> playerList = players.stream().map(player -> (PlayerEntity)player).collect(Collectors.toList());
            RegionPlayerUtils.addPlayers(regionName, source.asPlayer(), playerList);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int list(CommandSource source, String regionName) {
        try {
            RegionPlayerUtils.listPlayersInRegion(regionName, source.asPlayer());
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int giveHelp(CommandSource source) {
        try {
            MessageUtils.promptPlayerCommandHelp(source.asPlayer());
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
