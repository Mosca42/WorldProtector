package fr.mosca421.worldprotector.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import com.mojang.authlib.GameProfile;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionSaver;
import fr.mosca421.worldprotector.item.RegionStick;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import static fr.mosca421.worldprotector.util.MessageUtils.*;

public class RegionUtils {

	private RegionUtils(){}

	public static void teleportRegion(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			sendMessage(player, new TranslationTextComponent("message.region.teleport", regionName));
			player.setPositionAndUpdate(region.getArea().minX, 200, region.getArea().minZ);
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void addPlayer(String regionName, ServerPlayerEntity sourcePlayer, ServerPlayerEntity playerToAdd) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String playerToAddName = playerToAdd.getName().getString();
			if (!region.addPlayer(playerToAdd.getUniqueID().toString())) {
				// Player already defined in this region -> Message needed or silent acknowledgement?
				sendMessage(sourcePlayer, new TranslationTextComponent("message.region.errorplayer", regionName, playerToAddName));
			}
			sendMessage(sourcePlayer, new TranslationTextComponent("message.region.addplayer", playerToAddName, regionName));
			sendMessage(playerToAdd, new TranslationTextComponent("message.player.regionadded", regionName));
			RegionSaver.save();
		} else {
			sendMessage(sourcePlayer,  new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removePlayer(String regionName, ServerPlayerEntity sourcePlayer, ServerPlayerEntity playerToRemove) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String playerToRemoveName = playerToRemove.getName().getString();
			if (!region.removePlayer(playerToRemove.getUniqueID().toString())) {
				// Player was not present in this region -> Message needed or silent acknowledgement?
				sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknownplayer", regionName, playerToRemoveName));
			}
			sendMessage(sourcePlayer, new TranslationTextComponent("message.region.removeplayer", playerToRemoveName, regionName));
			sendMessage(playerToRemove, new TranslationTextComponent("message.player.regionremoved", regionName));
			RegionSaver.save();
		} else {
			sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeRegion(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.removeRegion(regionName) != null) {
			sendMessage(player, new TranslationTextComponent("message.region.remove", regionName));
			RegionSaver.save();
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeAllRegions(ServerPlayerEntity player) {
		RegionSaver.clearRegions();
		sendMessage(player, new TranslationTextComponent("message.region.removeall"));
		RegionSaver.save();
	}

	private static AxisAlignedBB getRegionFromNBT(CompoundNBT nbtTag){
		return new AxisAlignedBB(
				nbtTag.getInt("x1"), nbtTag.getInt("y1"), nbtTag.getInt("z1"),
				nbtTag.getInt("x2"), nbtTag.getInt("y2"), nbtTag.getInt("z2"))
				.grow(1);
	}

	public static void createRegion(String regionName, ServerPlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.getTag() != null) {
				CompoundNBT regionValidTag = item.getTag();
				if (item.hasTag() && regionValidTag.getBoolean("valide")) {
					AxisAlignedBB regions = getRegionFromNBT(regionValidTag);
					Region region = new Region(regionName, regions, getDimension(player.world));
					RegionSaver.addRegion(region);
					RegionSaver.save();
					regionValidTag.putBoolean("valide", false); // reset flag for consistent command behaviour
					sendMessage(player, new TranslationTextComponent("message.region.define", regionName));
				} else {
					sendMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendMessage(player,"message.itemhand.take");
		}
	}

	public static void giveHelpMessage(ServerPlayerEntity player) {
		sendMessage(player, "");
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		sendMessage(player,"help.region.1");
		sendMessage(player,"help.region.2");
		sendMessage(player,"help.region.3");
		sendMessage(player,"help.region.4");
		sendMessage(player,"help.region.5");
		sendMessage(player,"help.region.6");
		sendMessage(player,"help.region.7");
		sendMessage(player,"help.region.8");
		sendMessage(player,"help.region.9");
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}

	public static void giveRegionInfo(ServerPlayerEntity player, String regionName) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String noFlagsText = new TranslationTextComponent("message.region.info.noflags").getString();
			String noPlayersText = new TranslationTextComponent("message.region.info.noplayers").getString();
			String regionFlags = region.getFlags().isEmpty() ? noFlagsText : String.join(", ", region.getFlags());
			String regionPlayers = region.getPlayers().isEmpty() ? noPlayersText : String.join(",\n", region.getPlayers());
			sendMessage(player, new StringTextComponent(TextFormatting.BLUE + "==Region '" + regionName + "' information=="));
			sendMessage(player, new TranslationTextComponent("message.region.info.area", region.getArea().toString().substring(4)));
			sendMessage(player, new TranslationTextComponent("message.region.info.priority", region.getPriority()));
			sendMessage(player, new TranslationTextComponent("message.region.info.flags", regionFlags));
			sendMessage(player, new TranslationTextComponent("message.region.info.players", regionPlayers));
			sendMessage(player, new StringTextComponent(TextFormatting.BLUE + "==Region '" + regionName + "' information=="));
		}
		else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}

	}

	public static void giveRegionList(ServerPlayerEntity player) {
		player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Region : " + Joiner.on(", ").join(RegionSaver.getRegionNames())), player.getUniqueID());
	}

	public static void redefineRegion(String regionName, ServerPlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.getTag() != null) {
				CompoundNBT regionValidTag = item.getTag();
				if (item.hasTag() && regionValidTag.getBoolean("valide")) {
					if (RegionSaver.containsRegion(regionName)) {
						AxisAlignedBB regions = getRegionFromNBT(regionValidTag);
						Region region = new Region(regionName, regions, getDimension(player.world));
						RegionSaver.replaceRegion(region);
						RegionSaver.save();
						regionValidTag.putBoolean("valide", false); // reset flag for consistent command behaviour
						sendMessage(player, new TranslationTextComponent("message.region.redefine", regionName));}
					else {
						sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
					}
				} else {
					sendMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendMessage(player,"message.itemhand.take");
		}
	}

	public static List<Region> getHandlingRegionsFor(BlockPos position, String dimension) {
		int maxPriority = 1;
		ArrayList<Region> handlers = new ArrayList<>();
		for (Region region : RegionSaver.getRegions()) {
			boolean regionDimensionMatches = region.getDimension().equals(dimension);
			boolean positionIsInRegion = region.getArea().contains(new Vector3d(position.getX(), position.getY(), position.getZ()));
			if (regionDimensionMatches && positionIsInRegion) {
				if (region.getPriority() == maxPriority) {
					handlers.add(region);
				} else if (region.getPriority() > maxPriority) {
					handlers.clear();
					maxPriority = region.getPriority();
					handlers.add(region);
				}
			}
		}
		return handlers;
	}

	public static void setPriorityRegion(String regionName, int priority, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			if (priority >= 1) {
				region.setPriority(priority);
				sendMessage(player, new TranslationTextComponent("message.region.setpriority", priority, regionName));
			} else {
				sendMessage(player, "message.region.priority");
			}
		} else {
			sendMessage(player, "message.region.unknown");
		}
	}

	public static void getPriority(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String priority = "" + region.getPriority();
			sendMessage(player, new TranslationTextComponent("message.region.infopriority", regionName, priority));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static boolean isInRegion(String regionName, PlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			return region.permits(player);
		}
		return false;
	}

	public static String getDimension(World world){
		return world.getDimensionKey().getLocation().toString();
	}

}
