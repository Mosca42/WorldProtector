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

//	@Override
//	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//			if (args.length <= 0)
//				RegionsUtils.giveHelpMessage(player);
//			if (args.length == 1) {
//				if (args[0].equalsIgnoreCase("help"))
//					RegionsUtils.giveHelpMessage(player);
//
//				if (args[0].equalsIgnoreCase("list"))
//					RegionsUtils.giveRegionList(player);
//			}
//
//			if (args.length == 2) {
//				if (args[0].equalsIgnoreCase("define"))
//					RegionsUtils.createRegion(args[1], player, item);
//
//				if (args[0].equalsIgnoreCase("redefine"))
//					RegionsUtils.redefineRegion(args[1], player, item);
//
//				if (args[0].equalsIgnoreCase("remove"))
//					RegionsUtils.removeRegion(args[1], player);
//
//				if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) 
//					RegionsUtils.teleportRegion(args[1], player);
//				
//				if (args[0].equalsIgnoreCase("getpriority"))
//					RegionsUtils.getPriority(args[1], player);
//			}
//			if (args.length == 3) {
//				if (args[0].equalsIgnoreCase("setpriority"))
//					RegionsUtils.setPriorityRegion(args[1], Integer.parseInt(args[2]), player);
//			}
//	}

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("region").requires(cs -> cs.hasPermissionLevel(4)).executes(ctx -> giveHelp(ctx.getSource()))

				.then(Commands.literal("help").executes(ctx -> giveHelp(ctx.getSource())))

				.then(Commands.literal("list").executes(ctx -> giveList(ctx.getSource())))

				.then(Commands.literal("define").executes(ctx -> giveHelp(ctx.getSource())).then(Commands.argument("region", StringArgumentType.string()).executes(ctx -> define(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))

				.then(Commands.literal("redefine").then(Commands.argument("region", StringArgumentType.string()).executes(ctx -> redefine(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))

				.then(Commands.literal("remove").then(Commands.argument("region", StringArgumentType.string()).executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))

				.then(Commands.literal("teleport").then(Commands.argument("region", StringArgumentType.string()).executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))

				.then(Commands.literal("tp").then(Commands.argument("region", StringArgumentType.string()).executes(ctx -> teleport(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))

				.then(Commands.literal("getpriority").then(Commands.argument("region", StringArgumentType.string()).executes(ctx -> getpriority(ctx.getSource(), StringArgumentType.getString(ctx, "region")))))

				.then(Commands.literal("addplayer").then(Commands.argument("region", StringArgumentType.string()).then(Commands.argument("player", EntityArgument.players()).executes(ctx -> addPlayer(ctx.getSource(), StringArgumentType.getString(ctx, "region"), EntityArgument.getPlayers(ctx, "player"))))))
				
				.then(Commands.literal("removeplayer").then(Commands.argument("region", StringArgumentType.string()).then(Commands.argument("player", EntityArgument.players()).executes(ctx -> removePlayer(ctx.getSource(), StringArgumentType.getString(ctx, "region"), EntityArgument.getPlayers(ctx, "player"))))))

				.then(Commands.literal("setpriority").then(Commands.argument("region", StringArgumentType.string()).then(Commands.argument("priority", IntegerArgumentType.integer()).executes(ctx -> setpriority(ctx.getSource(), StringArgumentType.getString(ctx, "region"), IntegerArgumentType.getInteger(ctx, "priority"))))));

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
				System.out.println(serverplayerentity);
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
		System.out.println("fgggg");

		try {
			System.out.println("fgggg");
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

	//
	//
//			@Override
//			public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
//				if (args.length == 3) {
//					return getListOfStringsMatchingLastWord(args, FlagsList.VALID_FLAGS);
//				}
//				if (args.length == 1) {
//					return Lists.newArrayList("define", "redefine", "remove", "teleport", "getpriority", "setpriority", "help", "list");
//				}
//				if (args.length == 2) {
//					if (args[0].equalsIgnoreCase("redefine")) {
//						return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//					}
//					if (args[0].equalsIgnoreCase("remove")) {
//						return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//					}
//					if (args[0].equalsIgnoreCase("teleport")) {
//						return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//					}
//					if (args[0].equalsIgnoreCase("getpriority")) {
//						return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//					}
//					if (args[0].equalsIgnoreCase("setpriority")) {
//						return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//					}
//				}
//				return Lists.newArrayList();
	//
//			}

}
