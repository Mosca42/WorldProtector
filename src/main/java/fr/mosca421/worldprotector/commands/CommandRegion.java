package fr.mosca421.worldprotector.commands;

import java.util.List;

import com.google.common.collect.Lists;

import fr.mosca421.worldprotector.core.FlagsList;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandRegion extends CommandBase implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getName() {
		return "region";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/region help";
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("rg");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			ItemStack item = player.getHeldItemMainhand();
			if (args.length <= 0)
				RegionsUtils.giveHelpMessage(player);
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help"))
					RegionsUtils.giveHelpMessage(player);

				if (args[0].equalsIgnoreCase("list"))
					RegionsUtils.giveRegionList(player);
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("define"))
					RegionsUtils.createRegion(args[1], player, item);

				if (args[0].equalsIgnoreCase("redefine"))
					RegionsUtils.redefineRegion(args[1], player, item);

				if (args[0].equalsIgnoreCase("remove"))
					RegionsUtils.removeRegion(args[1], player);

				if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) 
					RegionsUtils.teleportRegion(args[1], player);
				
				if (args[0].equalsIgnoreCase("getpriority"))
					RegionsUtils.getPriority(args[1], player);
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("setpriority"))
					RegionsUtils.setPriorityRegion(args[1], Integer.parseInt(args[2]), player);
			}
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		if (args.length == 3) {
			return getListOfStringsMatchingLastWord(args, FlagsList.VALID_FLAGS);
		}
		if (args.length == 1) {
			return Lists.newArrayList("define", "redefine", "remove", "teleport", "getpriority", "setpriority", "help", "list");
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("redefine")) {
				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
			}
			if (args[0].equalsIgnoreCase("remove")) {
				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
			}
			if (args[0].equalsIgnoreCase("teleport")) {
				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
			}
			if (args[0].equalsIgnoreCase("getpriority")) {
				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
			}
			if (args[0].equalsIgnoreCase("setpriority")) {
				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
			}
		}
		return Lists.newArrayList();

	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

}
