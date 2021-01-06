package fr.mosca421.worldprotector.util;

import com.google.common.base.Joiner;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.data.RegionSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraft.util.text.TextFormatting;

import static fr.mosca421.worldprotector.util.MessageUtils.*;

public class RegionFlagUtils {

	private RegionFlagUtils(){}

	public static void getRegionFlags(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String regionFlags = Joiner.on(", ").join(region.getFlags());
			sendMessage(player, new TranslationTextComponent(TextFormatting.DARK_RED + regionName + " Flags: " + regionFlags));
		}
	}

	public static void addFlag(String regionName, PlayerEntity player, String flag) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			addFlag(region, player, flag);
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void addFlag(Region region, PlayerEntity player, String flag) {
		if (region.addFlag(flag)) {
			sendMessage(player, new TranslationTextComponent("message.flags.add", flag, region.getName()));
			RegionSaver.save();
		} else {
			sendMessage(player, new StringTextComponent("Flag already defined in region."));
		}


	}

	public static void removeFlag(String regionName, PlayerEntity player, String flag) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			if (RegionFlag.contains(flag)) {
				if (region.removeFlag(flag)) {
					sendMessage(player, new TranslationTextComponent("message.flags.remove", flag, regionName));
					RegionSaver.save();
				} else {
					sendMessage(player, new StringTextComponent("Flag not defined in region."));
				}
			}
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}
	
	public static void giveHelpMessage(ServerPlayerEntity player) {
		sendMessage(player, "");
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		sendMessage(player, "help.flags.1");
		sendMessage(player, "help.flags.2");
		sendMessage(player, "help.flags.3");
		sendMessage(player, "help.flags.4");
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}
	
	public static void listAvailableFlags(ServerPlayerEntity player){
		String flags = Joiner.on(", ").join(RegionFlag.getFlags());
		sendMessage(player, new StringTextComponent(TextFormatting.DARK_RED + "Flags: " + flags));
	}
	
	public static boolean isOp(PlayerEntity name) {
		OpEntry opPlayerEntry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(name.getGameProfile());
		if (opPlayerEntry != null) {
			return opPlayerEntry.getPermissionLevel() == 4;
		}
		return false;
	}
}
