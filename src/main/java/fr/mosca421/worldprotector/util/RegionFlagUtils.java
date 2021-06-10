package fr.mosca421.worldprotector.util;

import com.google.common.base.Joiner;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.data.RegionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

public final class RegionFlagUtils {

	private RegionFlagUtils() {
	}

	public static void addFlag(String regionName, PlayerEntity player, String flag) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> addFlag(region, player, flag));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void addAllFlags(String regionName, PlayerEntity player) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().addFlags(regionName, RegionFlag.getFlags());
			sendMessage(player, new TranslationTextComponent("message.flags.add.all", regionName));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void addFlags(String regionName, PlayerEntity player, List<String> flags) {
		if (RegionManager.get().containsRegion(regionName)) {
			List<String> addedFlags = RegionManager.get().addFlags(regionName, flags);
			String flagString = String.join(", ", addedFlags);
			if (!addedFlags.isEmpty()) {
				sendMessage(player, new TranslationTextComponent("message.flags.add.multiple", flagString, regionName));
			} else {
				sendMessage(player, new TranslationTextComponent( "message.flags.add.none", flagString));
			}
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeAllFlags(String regionName, PlayerEntity player) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().removeFlags(regionName, RegionFlag.getFlags());
			sendMessage(player, new TranslationTextComponent("message.flags.remove.all", regionName));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeFlags(String regionName, PlayerEntity player, List<String> flags) {
		if (RegionManager.get().containsRegion(regionName)) {
			List<String> removedFlags = RegionManager.get().removeFlags(regionName, flags);
			String flagString = String.join(", ", removedFlags);
			if (!removedFlags.isEmpty()) {
				sendMessage(player, new TranslationTextComponent("message.flags.remove.multiple", flagString, regionName));
			} else {
				sendMessage(player, new TranslationTextComponent( "message.flags.remove.none", flagString));
			}
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void addFlag(IRegion region, PlayerEntity player, String flag) {
		if (RegionManager.get().addFlag(region, flag)) {
			sendMessage(player, new TranslationTextComponent("message.flags.add", flag, region.getName()));
		} else {
			sendMessage(player, new StringTextComponent("Flag already defined in region."));
		}
	}

	public static void removeFlag(IRegion region, PlayerEntity player, String flag){
			if (RegionManager.get().removeFlag(region, flag)) {
				sendMessage(player, new TranslationTextComponent("message.flags.remove", flag, region.getName()));
			} else {
				sendMessage(player, new StringTextComponent("Flag not defined in region."));
			}
	}

	public static void removeFlag(String regionName, PlayerEntity player, String flag) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> removeFlag(region, player, flag));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void listAvailableFlags(PlayerEntity player){
		String flags = Joiner.on(", ").join(RegionFlag.getFlags());
		sendMessage(player, new StringTextComponent(TextFormatting.DARK_RED + "Flags: " + flags));
	}

	public static void giveHelpMessage(PlayerEntity player) {
		sendMessage(player, new TranslationTextComponent(TextFormatting.AQUA + "== WorldProtector Help =="));
		sendMessage(player, "help.flags.1");
		sendMessage(player, "help.flags.2");
		sendMessage(player, "help.flags.3");
		sendMessage(player, "help.flags.4");
		sendMessage(player, new TranslationTextComponent(TextFormatting.AQUA + "== WorldProtector Help =="));
	}

	public static String getFlagString(IRegion region) {
		return Joiner.on(", ").join(region.getFlags());
	}

}
