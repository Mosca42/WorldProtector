package fr.mosca421.worldprotector.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.mosca421.worldprotector.util.ExpandUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.system.CallbackI;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

public class CommandExpand {

	private CommandExpand(){}

	public static final LiteralArgumentBuilder<CommandSource> EXPAND_COMMAND = register();

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal(Command.EXPAND.toString())
				.executes(ctx -> giveHelp(ctx.getSource()))
				.then(Commands.literal(Command.HELP.toString())
						.executes(ctx -> giveHelp(ctx.getSource())))
				.then(Commands.literal(Command.VERT.toString())
						.executes(ctx -> giveHelp(ctx.getSource()))
						.then(Commands.argument("Y1", IntegerArgumentType.integer(0, 255))
								.then(Commands.argument("Y2", IntegerArgumentType.integer(0, 255))
										.executes(ctx -> vert(ctx.getSource(), ctx.getArgument("Y1", Integer.class), ctx.getArgument("Y2", Integer.class))))))
				.then(Commands.literal(Command.DEFAULT_Y.toString())
						.then(Commands.argument("Y1", IntegerArgumentType.integer(0, 255))
								.then(Commands.argument("Y2", IntegerArgumentType.integer(0, 255))
										.executes(ctx -> setDefaultYExpansion(ctx.getSource(), ctx.getArgument("Y1", Integer.class), ctx.getArgument("Y2", Integer.class))))))
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
			int yLow = Integer.min(y1, y2);
			int yHigh = Integer.max(y1, y2);
			ExpandUtils.expandVert(source.asPlayer(), item, yLow, yHigh);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int setDefaultYExpansion(CommandSource source, int y1, int y2) {
		try {
			PlayerEntity player = source.asPlayer();
			int yLow = Integer.min(y1, y2);
			int yHigh = Integer.max(y1, y2);
			ExpandUtils.setDefaultYLevels(player, yLow, yHigh);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

		return 0;
	}

}
