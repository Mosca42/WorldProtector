package fr.mosca421.worldprotector.commands;

import java.util.Collection;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;

import fr.mosca421.worldprotector.utils.FlagsUtils;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CommandRegion {

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("region")
                .requires(cs -> cs.hasPermissionLevel(4))
                .executes(ctx -> giveHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> giveHelp(ctx.getSource())))
                .then(Commands.literal("list")
                        .executes(ctx -> giveList(ctx.getSource())))
                .then(Commands.literal("define")
                        .executes(ctx -> giveHelp(ctx.getSource()))
                        .then(Commands.argument("region", StringArgumentType.string())
                                .executes(ctx -> define(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))
                .then(Commands.literal("redefine")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .executes(ctx -> redefine(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))
                .then(Commands.literal("teleport")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))
                .then(Commands.literal("tp")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))
                .then(Commands.literal("getpriority")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .executes(ctx -> getpriority(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))
                .then(Commands.literal("addplayer")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> addPlayer(ctx.getSource(), StringArgumentType.getString(ctx, "region"), EntityArgument.getPlayers(ctx, "player"))))))
                .then(Commands.literal("removeplayer")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .then(Commands.argument("player", EntityArgument.players())
                                        .executes(ctx -> removePlayer(ctx.getSource(), StringArgumentType.getString(ctx, "region"), EntityArgument.getPlayers(ctx, "player"))))))
                .then(Commands.literal("setpriority")
                        .then(Commands.argument("region", StringArgumentType.string())
                                .then(Commands.argument("priority", IntegerArgumentType.integer())
                                        .executes(ctx -> setpriority(ctx.getSource(), StringArgumentType.getString(ctx, "region"), IntegerArgumentType.getInteger(ctx, "priority"))))));
    }

	private static int addPlayer(CommandSource source, String region, Collection<ServerPlayerEntity> players) {
		try {
			for (ServerPlayerEntity serverplayerentity : players) {
				RegionsUtils.addPlayer(region, source.asPlayer(), serverplayerentity.getUniqueID().toString());
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int removePlayer(CommandSource source, String region, Collection<ServerPlayerEntity> players) {
		try {
			for (ServerPlayerEntity serverplayerentity : players) {
				RegionsUtils.removePlayer(region, source.asPlayer(), serverplayerentity.getUniqueID().toString());
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
