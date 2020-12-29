package fr.mosca421.worldprotector.commands;

import java.util.Collection;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CommandRegion {

	private CommandRegion(){}

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal(Command.REGION.toString())
                .requires(cs -> cs.hasPermissionLevel(4))
                .executes(ctx -> giveHelp(ctx.getSource()))
                .then(Commands.literal(Command.HELP.toString())
                        .executes(ctx -> giveHelp(ctx.getSource())))
                .then(Commands.literal(Command.LIST.toString())
                        .executes(ctx -> giveList(ctx.getSource())))
                .then(Commands.literal(Command.DEFINE.toString())
                        .executes(ctx -> giveHelp(ctx.getSource()))
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .executes(ctx -> define(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.REDEFINE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .executes(ctx -> redefine(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.REMOVE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.TELEPORT.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal("tp")
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.PRIORITY_GET.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .executes(ctx -> getpriority(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))))
                .then(Commands.literal(Command.PLAYER_ADD.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players())
                                        .executes(ctx -> addPlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.PLAYER_REMOVE.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .then(Commands.argument(Command.PLAYER.toString(), EntityArgument.players())
                                        .executes(ctx -> removePlayer(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), EntityArgument.getPlayers(ctx, Command.PLAYER.toString()))))))
                .then(Commands.literal(Command.PRIORITY_SET.toString())
                        .then(Commands.argument(Command.REGION.toString(), StringArgumentType.string())
                                .then(Commands.argument(Command.PRIORITY.toString(), IntegerArgumentType.integer())
                                        .executes(ctx -> setpriority(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), IntegerArgumentType.getInteger(ctx, Command.PRIORITY.toString()))))));
    }

	private static int addPlayer(CommandSource source, String region, Collection<ServerPlayerEntity> players) {
		try {
			for (ServerPlayerEntity serverplayerentity : players) {
				RegionsUtils.addPlayer(region, source.asPlayer(), serverplayerentity);
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int removePlayer(CommandSource source, String region, Collection<ServerPlayerEntity> players) {
		try {
			for (ServerPlayerEntity playerToRemove : players) {
				RegionsUtils.removePlayer(region, source.asPlayer(), playerToRemove);
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int giveHelp(CommandSource source) {
		try {
			RegionsUtils.giveHelpMessage(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int giveList(CommandSource source) {
		try {
			RegionsUtils.giveRegionList(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int define(CommandSource source, String region) {

		try {
			RegionsUtils.createRegion(region, source.asPlayer(), source.asPlayer().getHeldItemMainhand());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int redefine(CommandSource source, String region) {
		try {
			RegionsUtils.redefineRegion(region, source.asPlayer(), source.asPlayer().getHeldItemMainhand());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int remove(CommandSource source, String region) {
		try {
			RegionsUtils.removeRegion(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int teleport(CommandSource source, String region) {
		try {
			RegionsUtils.teleportRegion(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int getpriority(CommandSource source, String region) {
		try {
			RegionsUtils.getPriority(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int setpriority(CommandSource source, String region, int priority) {
		try {
			RegionsUtils.setPriorityRegion(region, priority, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
