package fr.mosca421.worldprotector.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fr.mosca421.worldprotector.core.FlagsList;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.utils.FlagsUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandFlag extends CommandBase implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getName() {
		return "flag";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/flag help";
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			if (args.length <= 0)
				FlagsUtils.giveHelpMessage(player);

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help"))
					FlagsUtils.giveHelpMessage(player);

				if (args[0].equalsIgnoreCase("list"))
					FlagsUtils.giveListFlagsOfRegion(player);
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("info"))
					FlagsUtils.getRegionFlags(args[1], player);
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("add"))
					FlagsUtils.addFlag(args[1], player, args[2]);

				if (args[0].equalsIgnoreCase("remove"))
					FlagsUtils.removeFlag(args[1], player, args[2]);
			}
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 3) {
			return getListOfStringsMatchingLastWord(args, FlagsList.VALID_FLAGS);
		}
		if (args.length == 1) {
			return Lists.newArrayList("help", "list", "info", "add", "remove");
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("add")) {
				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
			}
			if (args[0].equalsIgnoreCase("remove")) {
				return getListOfStringsMatchingLastWord(args, Saver.REGIONS.keySet());
			}
			if (args[0].equalsIgnoreCase("info")) {
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
