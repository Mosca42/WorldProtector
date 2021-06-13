package fr.mosca421.worldprotector.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class CommandsRegister {

	private CommandsRegister(){}

	public static void init(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register(CommandWorldProtector.register());
		commandDispatcher.register(CommandWorldProtector.registerAlternate1());
		commandDispatcher.register(CommandWorldProtector.registerAlternate2());
	}

}
