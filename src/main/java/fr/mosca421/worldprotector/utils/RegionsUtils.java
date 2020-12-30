package fr.mosca421.worldprotector.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionSaver;
import fr.mosca421.worldprotector.items.RegionStick;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import static fr.mosca421.worldprotector.utils.PlayerMessageUtils.*;

public class RegionsUtils {

	private RegionsUtils(){}

	public static void teleportRegion(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			sendInfoMessage(player, new TranslationTextComponent("message.region.teleport", regionName));
			player.setPositionAndUpdate(region.getArea().minX, 200, region.getArea().minZ);
		} else {
			sendInfoMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void addPlayer(String regionName, ServerPlayerEntity sourcePlayer, ServerPlayerEntity playerToAdd) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String playerToAddName = playerToAdd.getName().getString();
			if (!region.addPlayer(playerToAdd.getUniqueID().toString())) {
				// Player already defined in this region -> Message needed or silent acknowledgement?
				sendInfoMessage(sourcePlayer, new TranslationTextComponent("message.region.errorplayer", regionName, playerToAddName));
			}
			sendInfoMessage(sourcePlayer, new TranslationTextComponent("message.region.addplayer", regionName, playerToAddName));
			sendInfoMessage(playerToAdd, new TranslationTextComponent("message.player.regionadded", regionName));
			RegionSaver.save();
		} else {
			sendInfoMessage(sourcePlayer,  new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removePlayer(String regionName, ServerPlayerEntity sourcePlayer, ServerPlayerEntity playerToRemove) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String playerToRemoveName = playerToRemove.getName().getString();
			if (!region.removePlayer(playerToRemove.getUniqueID().toString())) {
				// Player was not present in this region -> Message needed or silent acknowledgement?
				sendInfoMessage(sourcePlayer, new TranslationTextComponent("message.region.unknownplayer", regionName, playerToRemoveName));
			}
			sendInfoMessage(sourcePlayer, new TranslationTextComponent("message.region.removeplayer", regionName, playerToRemoveName));
			sendInfoMessage(playerToRemove, new TranslationTextComponent("message.player.regionremoved", regionName));
			RegionSaver.save();
		} else {
			sendInfoMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeRegion(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.removeRegion(regionName) != null) {
			sendInfoMessage(player, new TranslationTextComponent("message.region.remove", regionName));
			RegionSaver.save();
		} else {
			sendInfoMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
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
				CompoundNBT regionTag = item.getTag();
				if (item.hasTag() && regionTag.getBoolean("valide")) {
					AxisAlignedBB regions = getRegionFromNBT(regionTag);
					Region region = new Region(regionName, regions, getDimension(player.world));
					RegionSaver.addRegion(region);
					RegionSaver.save();
					sendInfoMessage(player, "message.region.define");
				} else {
					sendInfoMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendInfoMessage(player,"message.itemhand.take");
		}
	}

	public static void giveHelpMessage(ServerPlayerEntity player) {
		sendInfoMessage(player, "");
		sendInfoMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
		sendInfoMessage(player,"help.region.1");
		sendInfoMessage(player,"help.region.2");
		sendInfoMessage(player,"help.region.3");
		sendInfoMessage(player,"help.region.4");
		sendInfoMessage(player,"help.region.5");
		sendInfoMessage(player,"help.region.6");
		sendInfoMessage(player,"help.region.7");
		sendInfoMessage(player,"help.region.8");
		sendInfoMessage(player,"help.region.9");
		sendInfoMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));

	}

	public static void giveRegionList(ServerPlayerEntity player) {
		player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Region : " + Joiner.on(", ").join(RegionSaver.getRegionNames())), player.getUniqueID());
	}

	public static void redefineRegion(String regionName, ServerPlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof RegionStick) {
			if (item.getTag() != null) {
				CompoundNBT regionTag = item.getTag();
				if (item.hasTag() && regionTag.getBoolean("valide")) {
					if (RegionSaver.containsRegion(regionName)) {
						AxisAlignedBB regions = getRegionFromNBT(regionTag);
						Region region = new Region(regionName, regions, getDimension(player.world));
						RegionSaver.replaceRegion(region);
						RegionSaver.save();
						sendInfoMessage(player, "message.region.redefine");}
					else {
						sendInfoMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
					}
				} else {
					sendInfoMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendInfoMessage(player,"message.itemhand.take");
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
				sendInfoMessage(player, new TranslationTextComponent("message.region.setpriority", priority, regionName));
			} else {
				sendInfoMessage(player, "message.region.priority");
			}
		} else {
			sendInfoMessage(player, "message.region.unknown");
		}
	}

	public static void getPriority(String regionName, ServerPlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String priority = "" + region.getPriority();
			sendInfoMessage(player, new TranslationTextComponent("message.region.infopriority", regionName, priority));
		} else {
			sendInfoMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
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
