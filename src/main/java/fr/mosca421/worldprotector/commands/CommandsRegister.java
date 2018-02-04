package fr.mosca421.worldprotector.commands;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommandsRegister {

	public static void init(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandRegion());
		event.registerServerCommand(new CommandExpand());
		event.registerServerCommand(new CommandFlag());
	}

}
