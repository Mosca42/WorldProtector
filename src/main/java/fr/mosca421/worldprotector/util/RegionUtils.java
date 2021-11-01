package fr.mosca421.worldprotector.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.mosca421.worldprotector.command.Command;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.data.RegionManager;
import fr.mosca421.worldprotector.item.ItemRegionMarker;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.util.MessageUtils.*;

public final class RegionUtils {

	private RegionUtils() {
	}

	public static void removeRegion(String regionName, PlayerEntity player) {
		if (RegionManager.get().removeRegion(regionName, player) != null) {
			sendMessage(player, new TranslationTextComponent("message.region.remove", regionName));
		} else {
			sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeAllRegions(PlayerEntity player) {
		RegionManager.get().clearRegions();
		sendMessage(player, new TranslationTextComponent("message.region.removeall"));
	}

	public static void createRegion(String regionName, PlayerEntity player, ItemStack item) {
		if (regionName.contains(" ")) { // region contains whitespace
			sendStatusMessage(player, "message.region.define.error");
			return;
		}
		if (item.getItem() instanceof ItemRegionMarker) {
			if (item.getTag() != null) {
				if (item.getTag().getBoolean(ItemRegionMarker.VALID)) {
					AxisAlignedBB regionArea = getAreaFromNBT(item.getTag());
					BlockPos tpPos = getTpTargetFromNBT(item.getTag());
					Region region = new Region(regionName, regionArea, tpPos, player.world.getDimensionKey());
					RegionManager.get().addRegion(region, player);
					item.getTag().putBoolean(ItemRegionMarker.VALID, false); // reset flag for consistent command behaviour
					sendMessage(player, new TranslationTextComponent("message.region.define", regionName));
				} else {
					sendStatusMessage(player, "message.item-hand.choose");
				}
			}
		} else {
			sendMessage(player, "message.item-hand.take");
		}
	}

	public static void createRegion(String regionName, PlayerEntity player, BlockPos startPos, BlockPos endPos) {
		if (regionName.contains(" ")) { // region contains whitespace
			sendStatusMessage(player, "message.region.define.error");
			return;
		}
		Region region = new Region(regionName, new AxisAlignedBB(startPos, endPos), player.world.getDimensionKey());
		RegionManager.get().addRegion(region, player);
		sendMessage(player, new TranslationTextComponent("message.region.define", regionName));
	}

	public static void redefineRegion(String regionName, PlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof ItemRegionMarker) {
			if (item.getTag() != null) {
				if (item.getTag().getBoolean(ItemRegionMarker.VALID)) {
					if (RegionManager.get().containsRegion(regionName)) {
						RegionManager.get().getRegion(regionName).ifPresent(region -> {
							region.setArea(getAreaFromNBT(item.getTag()));
							region.setTpTarget(getTpTargetFromNBT(item.getTag()));
							RegionManager.get().updateRegion(new Region(region), player);
							item.getTag().putBoolean(ItemRegionMarker.VALID, false); // reset flag for consistent command behaviour
							sendMessage(player, new TranslationTextComponent("message.region.redefine", regionName));
						});

					}
					else {
						sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
					}
				} else {
					sendStatusMessage(player, "message.item-hand.choose");
				}
			}
		} else {
			sendStatusMessage(player, "message.item-hand.take");
		}
	}

	public static void setRegionPriority(String regionName, int priority, PlayerEntity player) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> {
				region.setPriority(priority);
				sendMessage(player, new TranslationTextComponent("message.region.setpriority", priority, regionName));
			});
			RegionManager.get().markDirty();
		} else {
			sendStatusMessage(player, "message.region.unknown");
		}
	}

	public static void activate(String regionName, PlayerEntity player) {
		if (RegionManager.get().setActiveState(regionName, true)) {
			sendMessage(player, new TranslationTextComponent( "message.region.activate", regionName));
		} else {
			sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void activateAll(Collection<IRegion> regions, PlayerEntity player) {
		List<IRegion> deactiveRegions = regions.stream()
				.filter(region -> !region.isActive())
				.collect(Collectors.toList());
		deactiveRegions.forEach(region -> region.setIsActive(true));
		RegionManager.get().markDirty();
		List<String> activatedRegions = deactiveRegions.stream()
				.map(IRegion::getName)
				.collect(Collectors.toList());
		String regionString = String.join(", ", activatedRegions);
		if (!activatedRegions.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("message.region.activate.multiple", regionString));
		} else {
			sendStatusMessage(player, "message.region.activate.none");
		}
	}

	public static void deactivate(String regionName, PlayerEntity player) {
		if (RegionManager.get().setActiveState(regionName, false)) {
			sendMessage(player, new TranslationTextComponent( "message.region.deactivate", regionName));
		} else {
			sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void deactivateAll(Collection<IRegion> regions, PlayerEntity player) {
		List<IRegion> activeRegions = regions.stream()
				.filter(IRegion::isActive)
				.collect(Collectors.toList());
		activeRegions.forEach(region -> region.setIsActive(false));
		RegionManager.get().markDirty();
		List<String> deactivatedRegions = activeRegions.stream()
				.map(IRegion::getName)
				.collect(Collectors.toList());
		String regionString = String.join(", ", deactivatedRegions);
		if (!deactivatedRegions.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("message.region.deactivate.multiple", regionString));
		} else {
			sendStatusMessage(player, "message.region.deactivate.none");
		}
	}

	public static void muteRegion(String regionName, PlayerEntity player) {
		if (RegionManager.get().setMutedState(regionName, true)) {
			sendMessage(player, new TranslationTextComponent("message.region.mute", regionName));
		} else {
			sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void unmuteRegion(String regionName, PlayerEntity player) {
		if (RegionManager.get().setMutedState(regionName, false)) {
			sendMessage(player, new TranslationTextComponent("message.region.unmute", regionName));
		} else {
			sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static Collection<String> getRegionsAroundPlayer(PlayerEntity player) {
		BlockPos playerPos = player.getPosition();
		RegistryKey<World> dim = player.world.getDimensionKey();
		return RegionManager.get().getRegions(dim).stream()
				.filter(region -> region.containsPosition(playerPos))
				.map(IRegion::getName)
				.collect(Collectors.toList());
	}

	public static void listRegionsAroundPlayer(PlayerEntity player) {
		Collection<String> regions = getRegionsAroundPlayer(player);
		if (regions.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("You are not standing in any region!"));
			return;
		}
		BlockPos pos = player.getPosition();
		sendMessage(player, new TranslationTextComponent(TextFormatting.BOLD + "== Regions around [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "] =="));
		regions.forEach(regionName -> sendRegionInfoCommand(regionName, player));
	}

	public static void teleportToRegion(String regionName, PlayerEntity player, CommandSource source) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> {
				try {
					BlockPos target = region.getTpTarget();
					String command = "execute in " + region.getDimension().getLocation().toString() + " run tp " + player.getName().getString() + " " + target.getX() + " " + target.getY() + " " + target.getZ();
					sendStatusMessage(player, new TranslationTextComponent("message.region.teleport", region.getName()));
					source.getServer().getCommandManager().getDispatcher().execute(command, source);
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
				}
			});
		} else {
			sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void promptInteractiveRegionInfo(PlayerEntity player, String regionName) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> {

				IFormattableTextComponent regionInfoHeader = new StringTextComponent(TextFormatting.BOLD + "== Region ")
						.appendSibling(buildRegionInfoLink(regionName))
						.appendSibling(new StringTextComponent(TextFormatting.BOLD + " information =="));
				sendMessage(player, regionInfoHeader);

				IFormattableTextComponent regionTeleportMessage = new TranslationTextComponent("message.region.info.teleport")
						.appendSibling(buildDimensionTeleportLink(region));
				sendMessage(player, regionTeleportMessage);

				// Region area: ...
				sendMessage(player, new TranslationTextComponent("message.region.info.area", region.getArea().toString().substring(4)));

				// Region priority: n [#] [+] [-]
				int regionPriority = region.getPriority();
				sendMessage(player, buildRegionPriorityInfoLink(regionName, regionPriority));

				// TODO: Link for clear players
				// Region players: [n player(s)] [+]
				IFormattableTextComponent regionPlayerMessage = new TranslationTextComponent("message.region.info.players",
						region.getPlayers().isEmpty()
						? new TranslationTextComponent("message.region.info.noplayers").getString()
						: buildPlayerListLink(region));
				regionPlayerMessage.appendSibling(buildAddPlayerLink(regionName));
				sendMessage(player, regionPlayerMessage);

				// TODO: Link for clear flags
				// Region flags: [n flag(s)] [+]
				IFormattableTextComponent regionFlagMessage = new TranslationTextComponent("message.region.info.flags",
						region.getFlags().isEmpty()
				? new TranslationTextComponent("message.region.info.noflags").getString()
						: buildFlagListLink(region));
				regionFlagMessage.appendSibling(buildAddFlagLink(regionName));
				sendMessage(player, regionFlagMessage);

				// Region state: [activated], [unmuted]
				sendMessage(player, new TranslationTextComponent("message.region.info.active", buildRegionActiveLink(region), buildRegionMuteLink(region)));
				sendMessage(player, new StringTextComponent(""));
			});
		} else {
			sendStatusMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}

	}

	public static void giveRegionList(PlayerEntity player) {
		Collection<IRegion> regions = RegionManager.get().getAllRegionsSorted();
		if (regions.isEmpty()) {
			sendStatusMessage(player, "message.region.info.no_regions");
			return;
		}
		regions.forEach(region -> {
			IFormattableTextComponent regionTeleportMessage = new TranslationTextComponent("message.region.list.entry", region.getName())
					.appendSibling(buildDimensionTeleportLink(region));
			sendMessage(player, regionTeleportMessage);
		});
	}

	public static void giveRegionListForDim(PlayerEntity player, String dim) {
		List<IRegion> regionsForDim = RegionManager.get().getAllRegions()
				.stream()
				.filter(region -> region.getDimension().getLocation().toString().equals(dim))
				.sorted(Comparator.comparing(IRegion::getName))
				.collect(Collectors.toList());
		if (regionsForDim.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("message.region.info.regions_for_dim", dim));
			return;
		}
		regionsForDim.forEach(region -> {
			IFormattableTextComponent regionTeleportMessage = new TranslationTextComponent("message.region.list.entry", region.getName())
					.appendSibling(buildDimensionTeleportLink(region));
			sendMessage(player, regionTeleportMessage);
		});
	}

	public static Collection<String> getDimensionList() {
		return RegionManager.get().getDimensionList();
	}

	public static List<IRegion> getHandlingRegionsFor(Entity entity, IWorld world) {
		return getHandlingRegionsFor(entity.getPosition(), ((World) world).getDimensionKey());
	}

	public static List<IRegion> getHandlingRegionsFor(BlockPos position, World world) {
		return getHandlingRegionsFor(position, world.getDimensionKey());
	}

	public static List<IRegion> getHandlingRegionsFor(BlockPos position, RegistryKey<World> dimension) {
		int maxPriority = 1;
		List<IRegion> handlingRegions = new ArrayList<>();
		List<IRegion> filteredRegions = RegionManager.get().getRegions(dimension).stream()
				.filter(IRegion::isActive)
				.filter(region -> region.containsPosition(position))
				.collect(Collectors.toList());
		for (IRegion region : filteredRegions) {
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

	public static String getDimensionString(World world) {
		return world.getDimensionKey().getLocation().toString();
	}

	private static AxisAlignedBB getAreaFromNBT(CompoundNBT nbtTag) {
		return new AxisAlignedBB(
				nbtTag.getInt(ItemRegionMarker.X1), nbtTag.getInt(ItemRegionMarker.Y1), nbtTag.getInt(ItemRegionMarker.Z1),
				nbtTag.getInt(ItemRegionMarker.X2), nbtTag.getInt(ItemRegionMarker.Y2), nbtTag.getInt(ItemRegionMarker.Z2));
	}

	private static BlockPos getTpTargetFromNBT(CompoundNBT nbtTag) {
		if (nbtTag.getBoolean(ItemRegionMarker.TP_TARGET_SET)) {
			return new BlockPos(nbtTag.getInt(ItemRegionMarker.TP_X), nbtTag.getInt(ItemRegionMarker.TP_Y), nbtTag.getInt(ItemRegionMarker.TP_Z));
		} else {
			AxisAlignedBB area = getAreaFromNBT(nbtTag);
			int centerX = (int) area.getCenter().getX();
			int centerY = (int) area.getCenter().getY();
			int centerZ = (int) area.getCenter().getZ();
			return new BlockPos(centerX, centerY, centerZ);
		}
	}
}
