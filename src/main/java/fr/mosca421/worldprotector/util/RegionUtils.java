package fr.mosca421.worldprotector.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.item.ItemRegionMarker;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.data.RegionSaver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import static fr.mosca421.worldprotector.util.MessageUtils.*;

public class RegionUtils {

	private RegionUtils(){}

	public static void teleportRegion(String regionName, PlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			sendMessage(player, new TranslationTextComponent("message.region.teleport", regionName));
			player.setPositionAndUpdate(region.getArea().minX, 200, region.getArea().minZ);
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void addPlayer(String regionName, PlayerEntity sourcePlayer, PlayerEntity playerToAdd) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String playerToAddName = playerToAdd.getName().getString();
			if (!region.addPlayer(playerToAdd.getUniqueID().toString())) {
				// Player already defined in this region -> Message needed or silent acknowledgement?
				sendMessage(sourcePlayer, new TranslationTextComponent("message.region.errorplayer", regionName, playerToAddName));
			} else {
				sendMessage(sourcePlayer, new TranslationTextComponent("message.region.addplayer", playerToAddName, regionName));
				sendMessage(playerToAdd, new TranslationTextComponent("message.player.regionadded", regionName));
				RegionSaver.save();
			}
		} else {
			sendMessage(sourcePlayer,  new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removePlayer(String regionName, PlayerEntity sourcePlayer, PlayerEntity playerToRemove) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String playerToRemoveName = playerToRemove.getName().getString();
			if (!region.removePlayer(playerToRemove.getUniqueID().toString())) {
				// Player was not present in this region -> Message needed or silent acknowledgement?
				sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknownplayer", regionName, playerToRemoveName));
			} else {
				sendMessage(sourcePlayer, new TranslationTextComponent("message.region.removeplayer", playerToRemoveName, regionName));
				sendMessage(playerToRemove, new TranslationTextComponent("message.player.regionremoved", regionName));
				RegionSaver.save();
			}
		} else {
			sendMessage(sourcePlayer, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeRegion(String regionName, PlayerEntity player) {
		if (RegionSaver.removeRegion(regionName) != null) {
			sendMessage(player, new TranslationTextComponent("message.region.remove", regionName));
			RegionSaver.save();
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeAllRegions(PlayerEntity player) {
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

	public static void createRegion(String regionName, PlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof ItemRegionMarker) {
			if (item.getTag() != null) {
				if (item.getTag().getBoolean("valid")) {
					AxisAlignedBB regions = getRegionFromNBT(item.getTag());
					Region region = new Region(regionName, regions, getDimension(player.world));
					RegionSaver.addRegion(region);
					item.getTag().putBoolean("valid", false); // reset flag for consistent command behaviour
					RegionSaver.save();
					sendMessage(player, new TranslationTextComponent("message.region.define", regionName));
				} else {
					sendMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendMessage(player,"message.itemhand.take");
		}
	}

	public static void giveHelpMessage(PlayerEntity player) {
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
		sendMessage(player,"help.region.10");
		sendMessage(player,"help.region.11");
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}

	public static void giveRegionInfo(PlayerEntity player, String regionName) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String noFlagsText = new TranslationTextComponent("message.region.info.noflags").getString();
			String noPlayersText = new TranslationTextComponent("message.region.info.noplayers").getString();
			String regionFlags = region.getFlags().isEmpty() ? noFlagsText : String.join(", ", region.getFlags());
			String regionPlayers = region.getPlayers().isEmpty() ? noPlayersText : String.join(",\n", region.getPlayers());
			sendMessage(player, new StringTextComponent(TextFormatting.BLUE + "==Region '" + regionName + "' information=="));
			BlockPos tpPos = region.getCenterPos();
			sendTeleportLink(player, tpPos, new TranslationTextComponent("message.region.info.tpcenter"));
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

	public static void giveRegionList(PlayerEntity player) {
		RegionSaver.getRegions().forEach(region -> {
			BlockPos tpPos = region.getCenterPos();
			sendTeleportLink(player, tpPos, new TranslationTextComponent("message.region.list.entry", region.getName()));
		});
	}

	public static void redefineRegion(String regionName, PlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof ItemRegionMarker) {
			if (item.getTag() != null) {
				if (item.getTag().getBoolean("valid")) {
					if (RegionSaver.containsRegion(regionName)) {
						AxisAlignedBB regions = getRegionFromNBT(item.getTag());
						Region region = new Region(regionName, regions, getDimension(player.world));
						RegionSaver.replaceRegion(region);
						item.getTag().putBoolean("valid", false); // reset flag for consistent command behaviour
						RegionSaver.save();
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

	public static List<Region> getHandlingRegionsFor(Entity entity, IWorld world){
		return getHandlingRegionsFor(entity.getPosition(), RegionUtils.getDimension((World) world));
	}

	public static List<Region> getHandlingRegionsFor(BlockPos position, IWorld world){
		return getHandlingRegionsFor(position, RegionUtils.getDimension((World) world));
	}

	public static List<Region> getHandlingRegionsFor(BlockPos position, String dimension) {
		int maxPriority = 1;
		List<Region> handlingRegions = new ArrayList<>();
		List<Region> filteredRegions = RegionSaver.getRegions().stream()
				.filter(Region::isActive)
				.filter(region -> region.getDimension().equals(dimension))
				.filter(region -> region.containsPosition(position))
				.collect(Collectors.toList());
		for (Region region : filteredRegions) {
			if (region.getPriority() == maxPriority) {
				handlingRegions.add(region);
			} else if (region.getPriority() > maxPriority) {
				handlingRegions.clear();
				maxPriority = region.getPriority();
				handlingRegions.add(region);
			}
		}
		return handlingRegions;
	}

	public static List<Region> filterHandlingRegions(BlockPos position, World world, RegionFlag flagFilter){
		return getHandlingRegionsFor(position, getDimension(world))
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.collect(Collectors.toList());
	}

	public static List<Region> filterHandlingRegions(BlockPos position, World world, RegionFlag flagFilter, PlayerEntity player){
		return getHandlingRegionsFor(position, getDimension(world))
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.filter(region -> !region.permits(player))
				.collect(Collectors.toList());
	}

	public static void cancelEventsInRegions(BlockPos eventPos, World worldIn, RegionFlag flagFilter, PlayerEntity player, Runnable cancelAction){
		getHandlingRegionsFor(eventPos, getDimension(worldIn))
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.filter(region -> !region.permits(player))
				.forEach(region -> cancelAction.run());
	}

	public static void cancelEventsInRegions(BlockPos eventPos, World worldIn, RegionFlag flagFilter, Predicate<Region> isPermittedInRegion, Runnable cancelAction){
		getHandlingRegionsFor(eventPos, getDimension(worldIn))
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.filter(isPermittedInRegion)
				.forEach(region -> cancelAction.run());
	}


	public static boolean isActionProhibited(BlockPos position, IWorld world, RegionFlag flag) {
		return RegionUtils.getHandlingRegionsFor(position, world).stream()
				.anyMatch(region -> region.containsFlag(flag));
	}

	public static boolean isActionProhibited(BlockPos position, Entity entity, RegionFlag flag) {
		return RegionUtils.getHandlingRegionsFor(position, entity.world).stream()
				.anyMatch(region -> region.containsFlag(flag));
	}

	public static boolean isPlayerActionProhibited(BlockPos position, PlayerEntity player, RegionFlag flag) {
		return RegionUtils.getHandlingRegionsFor(position, player.world).stream()
				.anyMatch(region -> region.containsFlag(flag) && region.forbids(player));
	}

	public static void setPriorityRegion(String regionName, int priority, PlayerEntity player) {
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

	public static void getPriority(String regionName, PlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			String priority = "" + region.getPriority();
			sendMessage(player, new TranslationTextComponent("message.region.infopriority", regionName, priority));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static boolean isInRegion(String regionName, PlayerEntity player) {
		return RegionSaver.containsRegion(regionName)
				&& RegionSaver.getRegion(regionName).permits(player);
	}

	public boolean isActive(String regionName) {
		return RegionSaver.containsRegion(regionName)
				&& RegionSaver.getRegion(regionName).isActive();
	}

	public static String getDimension(World world){
		return world.getDimensionKey().getLocation().toString();
	}

	public static void activate(String regionName, PlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			region.setIsActive(true);
			RegionSaver.save();
			sendMessage(player, new TranslationTextComponent( "message.region.activate", regionName));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void activateAll(List<Region> regions, PlayerEntity player) {
		List<Region> deactiveRegions = regions.stream()
				.filter(region -> !region.isActive())
				.collect(Collectors.toList());
		deactiveRegions.forEach(region -> region.setIsActive(true));
		RegionSaver.save();
		List<String> activatedRegions = deactiveRegions.stream()
				.map(Region::getName)
				.collect(Collectors.toList());
		String regionString = String.join(", ", activatedRegions);
		if (!activatedRegions.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("message.region.activate.multiple", regionString));
		} else {
			sendMessage(player, "message.region.activate.none");
		}
	}

	public static void deactivate(String regionName, PlayerEntity player) {
		if (RegionSaver.containsRegion(regionName)) {
			Region region = RegionSaver.getRegion(regionName);
			region.setIsActive(false);
			RegionSaver.save();
			sendMessage(player, new TranslationTextComponent( "message.region.deactivate", regionName));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void deactivateAll(List<Region> regions, PlayerEntity player) {
		List<Region> activeRegions = regions.stream()
				.filter(Region::isActive)
				.collect(Collectors.toList());
		activeRegions.forEach(region -> region.setIsActive(false));
		RegionSaver.save();
		List<String> deactivatedRegions = activeRegions.stream()
				.map(Region::getName)
				.collect(Collectors.toList());
		String regionString = String.join(", ", deactivatedRegions);
		if (!deactivatedRegions.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("message.region.deactivate.multiple", regionString));
		} else {
			sendMessage(player, "message.region.deactivate.none");
		}
	}
}
