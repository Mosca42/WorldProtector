package fr.mosca421.worldprotector.command;

import java.util.Collection;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.mosca421.worldprotector.core.RegionSaver;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CommandRegion {

	private CommandRegion(){}

	public static final LiteralArgumentBuilder<CommandSource> REGION_COMMAND = register();

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal(Command.REGION.toString())
                .executes(ctx -> giveHelp(ctx.getSource()))
                .then(Commands.literal(Command.HELP.toString())
                        .executes(ctx -> giveHelp(ctx.getSource())))
                .then(Commands.literal(Command.LIST.toString())
                        .executes(ctx -> giveList(ctx.getSource())))
				.then(Commands.literal(Command.INFO.toString())
						.executes(ctx -> giveHelp(ctx.getSource()))
						.then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
								.executes(ctx -> info(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
				.then(Commands.literal(Command.DEFINE.toString())
                        .executes(ctx -> giveHelp(ctx.getSource()))
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .executes(ctx -> define(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.REDEFINE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .executes(ctx -> redefine(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.REMOVE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()))))
						.then(Commands.literal("all").executes(ctx -> removeAll(ctx.getSource()))))
                .then(Commands.literal(Command.TELEPORT.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal("tp")
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.PRIORITY_GET.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .executes(ctx -> getpriority(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.PLAYER_ADD.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players())
                                        .executes(ctx -> addPlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.PLAYER_REMOVE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players())
                                        .executes(ctx -> removePlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.PRIORITY_SET.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
                                .then(Commands.argument(Command.PRIORITY.toString(), IntegerArgumentType.integer())
                                        .executes(ctx -> setpriority(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), IntegerArgumentType.getInteger(ctx, Command.PRIORITY.toString()))))));
    }

	private static int info(CommandSource source, String regionName) {
		try {
			RegionUtils.giveRegionInfo(source.asPlayer(), regionName);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int addPlayer(CommandSource source, String region, Collection<ServerPlayerEntity> players) {
		try {
			for (ServerPlayerEntity serverplayerentity : players) {
				RegionUtils.addPlayer(region, source.asPlayer(), serverplayerentity);
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int removePlayer(CommandSource source, String region, Collection<ServerPlayerEntity> players) {
		try {
			for (ServerPlayerEntity playerToRemove : players) {
				RegionUtils.removePlayer(region, source.asPlayer(), playerToRemove);
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int giveHelp(CommandSource source) {
		try {
			RegionUtils.giveHelpMessage(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int giveList(CommandSource source) {
		try {
			RegionUtils.giveRegionList(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int define(CommandSource source, String region) {

		try {
			RegionUtils.createRegion(region, source.asPlayer(), source.asPlayer().getHeldItemMainhand());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int redefine(CommandSource source, String region) {
		try {
			RegionUtils.redefineRegion(region, source.asPlayer(), source.asPlayer().getHeldItemMainhand());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int remove(CommandSource source, String region) {
		try {
			RegionUtils.removeRegion(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}


	private static int removeAll(CommandSource source) {
		try {
			RegionUtils.removeAllRegions(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int teleport(CommandSource source, String region) {
		try {
			RegionUtils.teleportRegion(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int getpriority(CommandSource source, String region) {
		try {
			RegionUtils.getPriority(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int setpriority(CommandSource source, String region, int priority) {
		try {
			RegionUtils.setPriorityRegion(region, priority, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
