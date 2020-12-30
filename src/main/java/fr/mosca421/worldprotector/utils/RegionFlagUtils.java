package fr.mosca421.worldprotector.utils;

import com.google.common.base.Joiner;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.core.RegionSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraft.util.text.TextFormatting;

public class RegionFlagUtils {

	private RegionFlagUtils(){}

	public static void getRegionFlags(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String regionFlags = Joiner.on(", ").join(region.getFlags());
			PlayerMessageUtils.sendInfoMessage(player, new TranslationTextComponent(TextFormatting.DARK_RED + regionName + " Flags: " + regionFlags));
		}
	}

	public static void addFlag(String regionName, ServerPlayerEntity player, String flag) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			if (RegionFlag.contains(flag)) {
				region.addFlag(flag);
				PlayerMessageUtils.sendInfoMessage(player, new TranslationTextComponent("message.flags.add", flag, regionName));
				RegionSaver.save();
			}
		}
	}

	public static void removeFlag(String regionName, ServerPlayerEntity player, String flag) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			if (RegionFlag.contains(flag)) {
				if (region.removeFlag(flag)) {
					PlayerMessageUtils.sendInfoMessage(player, new TranslationTextComponent("message.flags.remove", flag, regionName));
					RegionSaver.save();
				} else {
					PlayerMessageUtils.sendInfoMessage(player, new TranslationTextComponent("message.flags.unknown", flag, regionName));
				}
			}
		}
	}
	
	public static void giveHelpMessage(ServerPlayerEntity player) {
		PlayerMessageUtils.sendInfoMessage(player, "");
		PlayerMessageUtils.sendInfoMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		PlayerMessageUtils.sendInfoMessage(player, "help.flags.1");
		PlayerMessageUtils.sendInfoMessage(player, "help.flags.2");
		PlayerMessageUtils.sendInfoMessage(player, "help.flags.3");
		PlayerMessageUtils.sendInfoMessage(player, "help.flags.4");
		PlayerMessageUtils.sendInfoMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}
	
	public static void listAvailableFlags(ServerPlayerEntity player){
		String flags = Joiner.on(", ").join(RegionFlag.getFlags());
		PlayerMessageUtils.sendInfoMessage(player, new StringTextComponent(TextFormatting.DARK_RED + "Flags: " + flags));
	}
	
	public static boolean isOp(PlayerEntity name) {
		OpEntry opPlayerEntry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(name.getGameProfile());
		if (opPlayerEntry != null) {
			return opPlayerEntry.getPermissionLevel() == 4;
		}
		return false;
	}
}
