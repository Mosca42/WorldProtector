package fr.mosca421.worldprotector.utils;

import fr.mosca421.worldprotector.items.RegionStick;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ExpandUtils {
	
	public static void giveHelpMessage(EntityPlayerMP player) {
		player.sendMessage(new TextComponentString(""));
		player.sendMessage(new TextComponentTranslation(TextFormatting.BLUE + "==WorldProtector Help=="));
		player.sendMessage(new TextComponentTranslation("help.expand.1"));
		player.sendMessage(new TextComponentTranslation(TextFormatting.BLUE + "==WorldProtector Help=="));
	}

	public static void expandVert(EntityPlayerMP player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.hasTagCompound()) {
				if (item.getTagCompound().getBoolean("valide")) {
					item.getTagCompound().setDouble("y1", 0);
					item.getTagCompound().setDouble("y2", 255);
					player.sendStatusMessage(new TextComponentTranslation("message.itemhand.expand"), true);
				} else {
					player.sendMessage(new TextComponentTranslation("message.itemhand.choose"));
				}
			} else {
				player.sendMessage(new TextComponentTranslation("message.itemhand.choose"));
			}
		} else {
			player.sendMessage(new TextComponentTranslation("message.itemhand.take"));
		}
	}
}
