package fr.mosca421.worldprotector.utils;

import com.google.common.base.Joiner;

import fr.mosca421.worldprotector.core.FlagsList;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraft.util.text.TextFormatting;

public class FlagsUtils {

	public static void getRegionFlags(String regions, ServerPlayerEntity player) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			player.sendMessage(new TranslationTextComponent(TextFormatting.DARK_RED + regions + " Flags : " + Joiner.on(", ").join(region.getFlags())));
		}
	}

	public static void addFlag(String regions, ServerPlayerEntity player, String flag) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (FlagsList.VALID_FLAGS.contains(flag)) {
				region.addFlag(flag);
				player.sendMessage(new TranslationTextComponent("message.flags.add", flag));
				Saver.save();
			}
		}
	}

	public static void removeFlag(String regions, ServerPlayerEntity player, String flag) {
		if (Saver.REGIONS.containsKey(regions)) {
			Region region = Saver.REGIONS.get(regions);
			if (FlagsList.VALID_FLAGS.contains(flag)) {
				if (region.removeFlag(flag)) {
					player.sendMessage(new TranslationTextComponent("message.flags.remove", flag));
					Saver.save();
				} else {
					player.sendMessage(new TranslationTextComponent("message.flags.unknown", flag));
				}
			}
		}
	}
	
	public static void giveHelpMessage(ServerPlayerEntity player) {
		player.sendMessage(new TranslationTextComponent(""));
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		player.sendMessage(new TranslationTextComponent("help.flags.1"));
		player.sendMessage(new TranslationTextComponent("help.flags.2"));
		player.sendMessage(new TranslationTextComponent("help.flags.3"));
		player.sendMessage(new TranslationTextComponent("help.flags.4"));
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}
	
	public static void giveListFlagsOfRegion(ServerPlayerEntity player){
		player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Flags : " + Joiner.on(", ").join(FlagsList.VALID_FLAGS)));
	}
	
	public static boolean isOp(PlayerEntity name) {
		if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(name.getGameProfile()) != null)
			if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(name.getGameProfile()).getPermissionLevel() == 4)
				return true;

		return false;
	}
}
