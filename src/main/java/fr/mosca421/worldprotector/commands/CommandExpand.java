package fr.mosca421.worldprotector.commands;

import java.util.List;

import com.google.common.collect.Lists;

import fr.mosca421.worldprotector.items.RegionStick;
import fr.mosca421.worldprotector.utils.ExpandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandExpand implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getName() {
		return "expand";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/expand help";
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			ItemStack item = player.getHeldItemMainhand();
			if (args.length <= 0)
				ExpandUtils.giveHelpMessage(player);

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help"))
					ExpandUtils.giveHelpMessage(player);

				if (args[0].equalsIgnoreCase("vert"))
					ExpandUtils.expandVert(player, item);
			}
			if (args.length > 1)
				ExpandUtils.giveHelpMessage(player);
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return Lists.newArrayList("help", "vert");
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

}
