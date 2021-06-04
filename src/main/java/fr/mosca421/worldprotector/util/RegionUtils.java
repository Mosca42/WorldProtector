package fr.mosca421.worldprotector.util;

import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.data.RegionManager;
import fr.mosca421.worldprotector.item.ItemRegionMarker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;
import static fr.mosca421.worldprotector.util.MessageUtils.sendTeleportLink;

public class RegionUtils {

	private RegionUtils(){}

	public static void removeRegion(String regionName, PlayerEntity player) {
		if (RegionManager.get().removeRegion(regionName, player) != null) {
			sendMessage(player, new TranslationTextComponent("message.region.remove", regionName));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void removeAllRegions(PlayerEntity player) {
		RegionManager.get().clearRegions();
		sendMessage(player, new TranslationTextComponent("message.region.removeall"));
	}

	public static void createRegion(String regionName, PlayerEntity player, ItemStack item) {
		if (regionName.contains(" ")){ // region contains whitespace
			sendMessage(player,   "message.region.define.error");
			return;
		}
		if (item.getItem() instanceof ItemRegionMarker) {
			if (item.getTag() != null) {
				if (item.getTag().getBoolean(ItemRegionMarker.VALID)) {
					AxisAlignedBB regionArea = getAreaFromNBT(item.getTag());
					Region region = new Region(regionName, regionArea, player.world.getDimensionKey());
					RegionManager.get().addRegion(region, player);
					item.getTag().putBoolean(ItemRegionMarker.VALID, false); // reset flag for consistent command behaviour
					sendMessage(player, new TranslationTextComponent("message.region.define", regionName));
				} else {
					sendMessage(player, "message.itemhand.choose");
				}
			}
		} else {
			sendMessage(player,"message.itemhand.take");
		}
	}

	public static void redefineRegion(String regionName, PlayerEntity player, ItemStack item) {
		if (item.getItem() instanceof ItemRegionMarker) {
			if (item.getTag() != null) {
				if (item.getTag().getBoolean(ItemRegionMarker.VALID)) {
					if (RegionManager.get().containsRegion(regionName)) {
						RegionManager.get().getRegion(regionName).ifPresent(region -> {
							region.setArea(getAreaFromNBT(item.getTag())); // TODO: maybe better in Manager/DRC
							RegionManager.get().updateRegion(new Region(region), player);
							item.getTag().putBoolean(ItemRegionMarker.VALID, false); // reset flag for consistent command behaviour
							sendMessage(player, new TranslationTextComponent("message.region.redefine", regionName));
						});

					}
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

	public static void setRegionPriority(String regionName, int priority, PlayerEntity player) {
		if (RegionManager.get().containsRegion(regionName)) {
			// TODO: Move to Manager/DimensionalRegionCache - no markdirty is done here
			RegionManager.get().getRegion(regionName).ifPresent(region -> {
				region.setPriority(priority);
				sendMessage(player, new TranslationTextComponent("message.region.setpriority", priority, regionName));
			});
			RegionManager.get().markDirty();
		} else {
			sendMessage(player, "message.region.unknown");
		}
	}

	public static void getPriority(String regionName, PlayerEntity player) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> {
				sendMessage(player, new TranslationTextComponent("message.region.infopriority", regionName,  region.getPriority()));
			});
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void activate(String regionName, PlayerEntity player) {
		if (RegionManager.get().setActiveState(regionName, true)) {
			sendMessage(player, new TranslationTextComponent( "message.region.activate", regionName));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void activateAll(Collection<IRegion> regions, PlayerEntity player) {
		List<IRegion> deactiveRegions = regions.stream()
				.filter(region -> !region.isActive())
				.collect(Collectors.toList());
		deactiveRegions.forEach(region -> region.setIsActive(true));
		// TODO: move to region manager
		List<String> activatedRegions = deactiveRegions.stream()
				.map(IRegion::getName)
				.collect(Collectors.toList());
		String regionString = String.join(", ", activatedRegions);
		if (!activatedRegions.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("message.region.activate.multiple", regionString));
		} else {
			sendMessage(player, "message.region.activate.none");
		}
	}

	public static void deactivate(String regionName, PlayerEntity player) {
		if (RegionManager.get().setActiveState(regionName, false)) {
			sendMessage(player, new TranslationTextComponent( "message.region.deactivate", regionName));
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void deactivateAll(Collection<IRegion> regions, PlayerEntity player) {
		List<IRegion> activeRegions = regions.stream()
				.filter(IRegion::isActive)
				.collect(Collectors.toList());
		activeRegions.forEach(region -> region.setIsActive(false));
		// TODO: move to region manager
		List<String> deactivatedRegions = activeRegions.stream()
				.map(IRegion::getName)
				.collect(Collectors.toList());
		String regionString = String.join(", ", deactivatedRegions);
		if (!deactivatedRegions.isEmpty()) {
			sendMessage(player, new TranslationTextComponent("message.region.deactivate.multiple", regionString));
		} else {
			sendMessage(player, "message.region.deactivate.none");
		}
	}

	public static void teleportRegion(String regionName, PlayerEntity player) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> {
				sendMessage(player, new TranslationTextComponent("message.region.teleport", regionName));
				BlockPos tpPos = region.getTpPos(player.world);
				player.setPositionAndUpdate(tpPos.getX(), tpPos.getY(), tpPos.getZ());
			});
		} else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}
	}

	public static void giveHelpMessage(PlayerEntity player) {
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
		sendMessage(player, new TranslationTextComponent(TextFormatting.BLUE + "==WorldProtector Help=="));
	}

	public static void giveRegionInfo(PlayerEntity player, String regionName) {
		if (RegionManager.get().containsRegion(regionName)) {
			RegionManager.get().getRegion(regionName).ifPresent(region -> {
				String noFlagsText = new TranslationTextComponent("message.region.info.noflags").getString();
				String noPlayersText = new TranslationTextComponent("message.region.info.noplayers").getString();
				String regionFlags = region.getFlags().isEmpty() ? noFlagsText : String.join(", ", region.getFlags());
				String regionPlayers = region.getPlayers().isEmpty() ? noPlayersText : String.join(",\n", region.getPlayers().values());
				sendMessage(player, new StringTextComponent(TextFormatting.BLUE + "==Region '" + regionName + "' information=="));
				BlockPos tpPos = region.getTpPos(player.world);
				sendTeleportLink(player, tpPos, new TranslationTextComponent("message.region.info.tpcenter"));
				sendMessage(player, new TranslationTextComponent("message.region.info.area", region.getArea().toString().substring(4)));
				sendMessage(player, new TranslationTextComponent("message.region.info.priority", region.getPriority()));
				sendMessage(player, new TranslationTextComponent("message.region.info.flags", regionFlags));
				sendMessage(player, new TranslationTextComponent("message.region.info.players", regionPlayers));
				sendMessage(player, new TranslationTextComponent("message.region.info.active", region.isActive()));
				sendMessage(player, new StringTextComponent(TextFormatting.BLUE + "==Region '" + regionName + "' information=="));
			});
		}
		else {
			sendMessage(player, new TranslationTextComponent("message.region.unknown", regionName));
		}

	}

	// TODO: region list by dimension
	public static void giveRegionList(PlayerEntity player) {
		if (RegionManager.get().getAllRegions().isEmpty()) {
			sendMessage(player, "message.region.info.no_regions");
			return;
		}
		RegionManager.get().getAllRegions().forEach(region -> {
			BlockPos tpPos = region.getTpPos(player.world);
			sendTeleportLink(player, tpPos, new TranslationTextComponent("message.region.list.entry", region.getName()));
		});
	}

	public static List<IRegion> getHandlingRegionsFor(Entity entity, IWorld world){
		return getHandlingRegionsFor(entity.getPosition(), ((World)world).getDimensionKey());
	}

	public static List<IRegion> getHandlingRegionsFor(BlockPos position, World world){
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

	public static List<IRegion> filterHandlingRegions(BlockPos position, World world, RegionFlag flagFilter){
		return getHandlingRegionsFor(position, world)
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.collect(Collectors.toList());
	}

	public static List<IRegion> filterHandlingRegions(BlockPos position, World world, RegionFlag flagFilter, PlayerEntity player){
		return getHandlingRegionsFor(position, world)
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.filter(region -> !region.permits(player))
				.collect(Collectors.toList());
	}

	public static void cancelEventsInRegions(BlockPos eventPos, World worldIn, RegionFlag flagFilter, PlayerEntity player, Runnable cancelAction){
		getHandlingRegionsFor(eventPos, worldIn)
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.filter(region -> !region.permits(player))
				.forEach(region -> cancelAction.run());
	}

	public static void cancelEventsInRegions(BlockPos eventPos, World worldIn, RegionFlag flagFilter, Predicate<IRegion> isPermittedInRegion, Runnable cancelAction){
		getHandlingRegionsFor(eventPos, worldIn)
				.stream()
				.filter(region -> region.containsFlag(flagFilter.toString()))
				.filter(isPermittedInRegion)
				.forEach(region -> cancelAction.run());
	}

	public static boolean isActionProhibited(BlockPos position, IWorld world, RegionFlag flag) {
		return RegionUtils.getHandlingRegionsFor(position, (World) world).stream()
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

	public static String getDimension(World world){
		return world.getDimensionKey().getLocation().toString();
	}

	private static AxisAlignedBB getAreaFromNBT(CompoundNBT nbtTag){
		return new AxisAlignedBB(
				nbtTag.getInt(ItemRegionMarker.X1), nbtTag.getInt(ItemRegionMarker.Y1), nbtTag.getInt(ItemRegionMarker.Z1),
				nbtTag.getInt(ItemRegionMarker.X2), nbtTag.getInt(ItemRegionMarker.Y2), nbtTag.getInt(ItemRegionMarker.Z2))
				.grow(1);
	}

}
