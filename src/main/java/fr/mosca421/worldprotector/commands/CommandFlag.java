package fr.mosca421.worldprotector.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.mosca421.worldprotector.core.FlagsList;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.utils.FlagsUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandFlag {

//	@Override
//	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//		if (args.length == 3) {
//			return getListOfStringsMatchingLastWord(args, FlagsList.VALID_FLAGS);
//		}
//		if (args.length == 1) {
//			return Lists.newArrayList("help", "list", "info", "add", "remove");
//		}
//		if (args.length == 2) {
//			if (args[0].equalsIgnoreCase("add")) {
//				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//			}
//			if (args[0].equalsIgnoreCase("remove")) {
//				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//			}
//			if (args[0].equalsIgnoreCase("info")) {
//				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
//			}
//		}
//		return Lists.newArrayList();
//
//	}

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("flag").requires(cs -> cs.hasPermissionLevel(4)).executes(ctx -> giveHelp(ctx.getSource()))

				.then(Commands.literal("help").executes(ctx -> giveHelp(ctx.getSource())))

				.then(Commands.literal("list").executes(ctx -> giveList(ctx.getSource())))

				.then(Commands.literal("add").then(Commands.argument("region", StringArgumentType.word()).then(Commands.argument("flag", StringArgumentType.string()).suggests((ctx, builder) -> ISuggestionProvider.suggest(FlagsList.VALID_FLAGS, builder)).then(Commands.argument("name", StringArgumentType.greedyString()).executes(ctx -> add(ctx.getSource(), StringArgumentType.getString(ctx, "region"), StringArgumentType.getString(ctx, "flag"), StringArgumentType.getString(ctx, "name")))))))

				.then(Commands.literal("add").then(Commands.argument("region", StringArgumentType.word()).then(Commands.argument("flag", StringArgumentType.string()).suggests((ctx, builder) -> ISuggestionProvider.suggest(FlagsList.VALID_FLAGS, builder)).executes(ctx -> add(ctx.getSource(), StringArgumentType.getString(ctx, "region"), StringArgumentType.getString(ctx, "flag"), "")))))

				.then(Commands.literal("remove").then(Commands.argument("region", StringArgumentType.word()).then(Commands.argument("flag", StringArgumentType.string()).suggests((ctx, builder) -> ISuggestionProvider.suggest(FlagsList.VALID_FLAGS, builder)).executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, "region"), StringArgumentType.getString(ctx, "flag"))))))

				.then(Commands.literal("info").then(Commands.argument("region", StringArgumentType.word()).executes(ctx -> info(ctx.getSource(), StringArgumentType.getString(ctx, "region")))));
	}

	private static int giveHelp(CommandSource source) {
		try {
			FlagsUtils.giveHelpMessage(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int giveList(CommandSource source) {
		try {
			FlagsUtils.giveListFlagsOfRegion(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int add(CommandSource source, String region, String flag, String name) {
		try {
			if (flag.equalsIgnoreCase("enter-message")) {
				if (Saver.REGIONS.containsKey(region)) {
					Region regions = Saver.REGIONS.get(region);
					regions.setEnterMessage(name);
				}
			}
			if (flag.equalsIgnoreCase("exit-message")) {
				if (Saver.REGIONS.containsKey(region)) {
					Region regions = Saver.REGIONS.get(region);
					regions.setExitMessage(name);
				}
			}
			if (flag.equalsIgnoreCase("enter-message-small")) {
				if (Saver.REGIONS.containsKey(region)) {
					Region regions = Saver.REGIONS.get(region);
					regions.setEnterMessageSmall(name);
				}
			}
			if (flag.equalsIgnoreCase("exit-message-small")) {
				if (Saver.REGIONS.containsKey(region)) {
					Region regions = Saver.REGIONS.get(region);
					regions.setExitMessageSmall(name);
				}
			}
			FlagsUtils.addFlag(region, source.asPlayer(), flag);

		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int remove(CommandSource source, String region, String flag) {
		try {
			FlagsUtils.removeFlag(region, source.asPlayer(), flag);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int info(CommandSource source, String region) {
		try {
			FlagsUtils.getRegionFlags(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
