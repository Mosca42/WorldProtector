package fr.mosca421.worldprotector.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.core.RegionSaver;
import fr.mosca421.worldprotector.utils.RegionFlagUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

public class CommandFlag {

	private CommandFlag(){}

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal(Command.FLAG.toString())
				.requires(cs -> cs.hasPermissionLevel(4))
				.executes(ctx -> giveHelp(ctx.getSource()))
				.then(Commands.literal(Command.HELP.toString())
						.executes(ctx -> giveHelp(ctx.getSource())))
				.then(Commands.literal(Command.LIST.toString())
						.executes(ctx -> giveList(ctx.getSource())))
				.then(Commands.literal(Command.ADD.toString())
						.then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
								.then(Commands.argument(Command.FLAG.toString(), StringArgumentType.string())
										.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionFlag.getFlags(), builder))
										.then(Commands.argument(Command.NAME.toString(), StringArgumentType.greedyString())
												.executes(ctx -> add(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), StringArgumentType.getString(ctx, Command.FLAG.toString()), StringArgumentType.getString(ctx, Command.NAME.toString())))))))
				.then(Commands.literal(Command.ADD.toString())
						.then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
								.then(Commands.argument(Command.FLAG.toString(), StringArgumentType.string())
										.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionFlag.getFlags(), builder))
										.executes(ctx -> add(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), StringArgumentType.getString(ctx, Command.FLAG.toString()), "")))))
				.then(Commands.literal(Command.REMOVE.toString())
						.then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
								.then(Commands.argument(Command.FLAG.toString(), StringArgumentType.string())
										.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionFlags(ctx.getArgument(Command.REGION.toString(), String.class)), builder))
										.executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString()), StringArgumentType.getString(ctx, Command.FLAG.toString()))))))
				.then(Commands.literal(Command.INFO.toString())
						.then(Commands.argument(Command.REGION.toString(), StringArgumentType.word())
								.suggests((ctx, builder) -> ISuggestionProvider.suggest(RegionSaver.getRegionNames(), builder))
								.executes(ctx -> info(ctx.getSource(), StringArgumentType.getString(ctx, Command.REGION.toString())))));
	}

	private static int giveHelp(CommandSource source) {
		try {
			RegionFlagUtils.giveHelpMessage(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int giveList(CommandSource source) {
		try {
			RegionFlagUtils.listAvailableFlags(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int add(CommandSource source, String region, String flag, String name) {
		try {
			boolean regionsContain = RegionSaver.containsRegion(region);
			if (flag.equalsIgnoreCase(RegionFlag.ENTER_MESSAGE_TITLE.toString()) && regionsContain) {
				Region regions = RegionSaver.getRegion(region);
				regions.setEnterMessage(name);
			}
			if (flag.equalsIgnoreCase(RegionFlag.EXIT_MESSAGE_TITLE.toString()) && regionsContain) {
				Region regions = RegionSaver.getRegion(region);
				regions.setExitMessage(name);
			}
			if (flag.equalsIgnoreCase(RegionFlag.ENTER_MESSAGE_SUBTITLE.toString()) && regionsContain) {
				Region regions = RegionSaver.getRegion(region);
				regions.setEnterMessageSmall(name);
			}
			if (flag.equalsIgnoreCase(RegionFlag.EXIT_MESSAGE_SUBTITLE.toString()) && regionsContain) {
				Region regions = RegionSaver.getRegion(region);
				regions.setExitMessageSmall(name);
			}
			RegionFlagUtils.addFlag(region, source.asPlayer(), flag);

		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int remove(CommandSource source, String region, String flag) {
		try {
			RegionFlagUtils.removeFlag(region, source.asPlayer(), flag);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int info(CommandSource source, String region) {
		try {
			RegionFlagUtils.getRegionFlags(region, source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
