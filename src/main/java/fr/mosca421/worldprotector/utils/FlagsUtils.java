package fr.mosca421.worldprotector.utils;

import com.google.common.base.Joiner;

import fr.mosca421.worldprotector.core.FlagsList;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class FlagsUtils {

	public static void getRegionFlags(String regions, EntityPlayerMP player) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			player.sendMessage(new TextComponentTranslation(TextFormatting.DARK_RED + regions + " Flags : " + Joiner.on(", ").join(region.getFlags())));
		}
	}

	public static void addFlag(String regions, EntityPlayerMP player, String flag) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (FlagsList.VALID_FLAGS.contains(flag)) {
				region.addFlag(flag);
				player.sendMessage(new TextComponentTranslation("message.flags.add", flag));
				Saver.save();
			}
		}
	}

	public static void removeFlag(String regions, EntityPlayerMP player, String flag) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (FlagsList.VALID_FLAGS.contains(flag)) {
				if (region.removeFlag(flag)) {
					player.sendMessage(new TextComponentTranslation("message.flags.remove", flag));
					Saver.save();
				} else {
					player.sendMessage(new TextComponentTranslation("message.flags.unknown", flag));
				}
			}
		}
	}
	
	public static void giveHelpMessage(EntityPlayerMP player) {
		player.sendMessage(new TextComponentTranslation(""));
		player.sendMessage(new TextComponentTranslation(TextFormatting.BLUE + "==WorldProtector Help=="));
		player.sendMessage(new TextComponentTranslation("help.flags.1"));
		player.sendMessage(new TextComponentTranslation("help.flags.2"));
		player.sendMessage(new TextComponentTranslation("help.flags.3"));
		player.sendMessage(new TextComponentTranslation("help.flags.4"));
		player.sendMessage(new TextComponentTranslation(TextFormatting.BLUE + "==WorldProtector Help=="));
	}
	
	public static void giveListFlagsOfRegion(EntityPlayerMP player){
		player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Flags : " + Joiner.on(", ").join(FlagsList.VALID_FLAGS)));
	}
}
