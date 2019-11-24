package fr.mosca421.worldprotector.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;

public class CommandsRegister {

	public static void init(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register(CommandExpand.register());
		commandDispatcher.register(CommandFlag.register());
		commandDispatcher.register(CommandRegion.register());
	}

}
