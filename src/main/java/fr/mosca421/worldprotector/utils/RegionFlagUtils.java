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

import java.util.UUID;

public class RegionFlagUtils {

	private RegionFlagUtils(){}

	public static void getRegionFlags(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			player.sendMessage(new TranslationTextComponent(TextFormatting.DARK_RED + regionName + " Flags : " + Joiner.on(", ").join(region.getFlags())), player.getUniqueID());
		}
	}

	public static void addFlag(String regionName, ServerPlayerEntity player, String flag) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			if (RegionFlag.contains(flag)) {
				region.addFlag(flag);
				player.sendMessage(new TranslationTextComponent("message.flags.add", flag), player.getUniqueID());
				RegionSaver.save();
			}
		}
	}

	public static void removeFlag(String regionName, ServerPlayerEntity player, String flag) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			if (RegionFlag.contains(flag)) {
				if (region.removeFlag(flag)) {
					player.sendMessage(new TranslationTextComponent("message.flags.remove", flag), player.getUniqueID());
					RegionSaver.save();
				} else {
					player.sendMessage(new TranslationTextComponent("message.flags.unknown", flag), player.getUniqueID());
				}
			}
		}
	}
	
	public static void giveHelpMessage(ServerPlayerEntity player) {
		UUID playerUUID = player.getUniqueID();
		player.sendMessage(new TranslationTextComponent(""), playerUUID);
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.flags.1"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.flags.2"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.flags.3"), playerUUID);
		player.sendMessage(new TranslationTextComponent("help.flags.4"), playerUUID);
		player.sendMessage(new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="), playerUUID);
	}
	
	public static void giveListFlagsOfRegion(ServerPlayerEntity player){
		player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Flags : " + Joiner.on(", ").join(RegionFlag.getFlags())), player.getUniqueID());
	}
	
	public static boolean isOp(PlayerEntity name) {
		OpEntry opPlayerEntry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(name.getGameProfile());
		if (opPlayerEntry != null) {
			return opPlayerEntry.getPermissionLevel() == 4;
		}
		return false;
	}
}
