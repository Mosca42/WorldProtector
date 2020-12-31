package fr.mosca421.worldprotector.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.mosca421.worldprotector.util.ExpandUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;

public class CommandExpand {

	private CommandExpand(){}

	public static final LiteralArgumentBuilder<CommandSource> EXPAND_COMMAND = register();

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal(Command.EXPAND.toString())
				.executes(ctx -> giveHelp(ctx.getSource()))
				.then(Commands.literal(Command.HELP.toString())
						.executes(ctx -> giveHelp(ctx.getSource())))
				.then(Commands.literal(Command.VERT.toString())
						.then(Commands.argument("Y1", IntegerArgumentType.integer(0, 255))
								.then(Commands.argument("Y2", IntegerArgumentType.integer(0, 255))
										.executes(ctx -> vert(ctx.getSource(), ctx.getArgument("Y1", Integer.class), ctx.getArgument("Y2", Integer.class))))))
				.then(Commands.literal(Command.VERT.toString())
						.executes(ctx -> vert(ctx.getSource(), 0, 255)));
	}

	public static int giveHelp(CommandSource source) {
		try {
			ExpandUtils.giveHelpMessage(source.asPlayer());
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int vert(CommandSource source, int y1, int y2) {
		try {
			ItemStack item = source.asPlayer().getHeldItemMainhand();
			ExpandUtils.expandVert(source.asPlayer(), item, y1, y2);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
