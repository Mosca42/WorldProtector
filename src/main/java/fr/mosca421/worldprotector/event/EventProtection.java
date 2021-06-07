package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.util.MessageUtils.sendStatusMessage;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventProtection {

	private EventProtection(){}

	@SubscribeEvent
	public static void onPlayerBreakBlock(BreakEvent event) {
		if (!event.getWorld().isRemote()) {
			PlayerEntity player = event.getPlayer();
			RegionUtils.cancelEventsInRegions(
					event.getPos(), (World) event.getWorld(), RegionFlag.BREAK,
					region -> !region.permits(player),
					() -> {
						event.setCanceled(true);
						sendStatusMessage(player, new TranslationTextComponent("message.event.protection.break_block"));
					});
		}
	}

	@SubscribeEvent
	public static void onPlayerPlaceBlock(EntityPlaceEvent event) {
		if (!event.getWorld().isRemote()) {
			List<IRegion> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), (World) event.getWorld());
			if (event.getEntity() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getEntity();
				boolean isPlayerPlacementProhibited = regions.stream()
						.anyMatch(region -> region.containsFlag(RegionFlag.PLACE) && region.forbids(player));
				if (isPlayerPlacementProhibited) {
					event.setCanceled(true);
					sendStatusMessage(player, new TranslationTextComponent("message.event.protection.place_block"));
				}
			}
			// TODO: Test
			if (event.getEntity() instanceof EndermanEntity) {
				boolean endermanPlacingProhibited = regions.stream()
						.anyMatch(region -> region.containsFlag(RegionFlag.ENTITY_PLACE));
				if (endermanPlacingProhibited) {
					event.setCanceled(true);
					WorldProtector.LOGGER.debug("Block placed by enderman denied!");
				}
			}
		}
	}


	@SubscribeEvent
	public static void onExplosionStarted(ExplosionEvent.Start event) {
		if (!event.getWorld().isRemote) {
			List<IRegion> regions = RegionUtils.getHandlingRegionsFor(new BlockPos(event.getExplosion().getPosition()), event.getWorld());
			if (event.getExplosion().getExplosivePlacedBy() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getExplosion().getExplosivePlacedBy();
				for (IRegion region : regions) {
					boolean cancelEvent = region.containsFlag(RegionFlag.IGNITE_EXPLOSIVES) && !region.permits(player);
					event.setCanceled(cancelEvent);
					if (cancelEvent) {
						sendStatusMessage(player, "message.event.protection.ignite_tnt");
					}
				}
			} else {
				// TODO: Explosion triggered by projectile or other TNT, or [.?.]
			}
		}
	}


	/**
	 * Removes affected entities and/or blocks from the event list to protect them
	 * @param event -
	 */
	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		if (!event.getWorld().isRemote) {
			event.getAffectedBlocks().removeAll(filterExplosionAffectedBlocks(event, RegionFlag.EXPLOSION_BLOCK.toString()));
			event.getAffectedEntities().removeAll(filterAffectedEntities(event.getAffectedEntities(), RegionFlag.EXPLOSION_ENTITY.toString()));

			boolean explosionTriggeredByCreeper = (event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity);
			if (!explosionTriggeredByCreeper) {
				event.getAffectedBlocks().removeAll(filterExplosionAffectedBlocks(event, RegionFlag.EXPLOSION_OTHER_BLOCKS.toString()));
				event.getAffectedEntities().removeAll(filterAffectedEntities(event.getAffectedEntities(), RegionFlag.EXPLOSION_OTHER_ENTITY.toString()));
			}
			if (explosionTriggeredByCreeper) {
				event.getAffectedBlocks().removeAll(filterExplosionAffectedBlocks(event, RegionFlag.EXPLOSION_CREEPER_BLOCK.toString()));
				event.getAffectedEntities().removeAll(filterAffectedEntities(event.getAffectedEntities(), RegionFlag.EXPLOSION_OTHER_ENTITY.toString()));
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerUseToolSecondary(BlockEvent.BlockToolInteractEvent event) {
		if (!event.getWorld().isRemote()) {
			PlayerEntity player = event.getPlayer();
			List<IRegion> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), player.getEntityWorld());
			for (IRegion region : regions){ // iterate through regions, if a region contains a specified flag, cancel event
				boolean playerNotPermitted = !region.permits(player);
				if (region.containsFlag(RegionFlag.TOOL_SECONDARY_USE) && playerNotPermitted) {
					event.setCanceled(true);
					sendStatusMessage(player, "message.event.protection.tool_secondary_use");
					return;
				}
				if (event.getToolType() == ToolType.AXE && region.containsFlag(RegionFlag.AXE_STRIP) && playerNotPermitted) {
					event.setCanceled(true);
					sendStatusMessage(player, "message.event.protection.strip_wood");
					return;
				}
				if (event.getToolType() == ToolType.HOE && region.containsFlag(RegionFlag.HOE_TILL) && playerNotPermitted) {
					event.setCanceled(true);
					sendStatusMessage(player, "message.event.protection.till_farmland");
					return;
				}
				if (event.getToolType() == ToolType.SHOVEL && region.containsFlag(RegionFlag.SHOVEL_PATH) && playerNotPermitted) {
					event.setCanceled(true);
					sendStatusMessage(player, "message.event.protection.shovel_path");
					return;
				}
			}
		}
	}

	@SubscribeEvent
	// Note: Does not prevent from fluids generate additional blocks (cobble generator). Use BlockEvent.FluidPlaceBlockEvent for this
	public static void onBucketFill(FillBucketEvent event) {
		PlayerEntity player = event.getPlayer();
		if (!event.getWorld().isRemote && event.getTarget() != null) {
			List<IRegion> regions = RegionUtils.getHandlingRegionsFor(new BlockPos(event.getTarget().getHitVec()), event.getWorld());
			for (IRegion region : regions) {
				int bucketItemMaxStackCount = event.getEmptyBucket().getMaxStackSize();
				// MaxStackSize: 1 -> full bucket so only placeable; >1 -> empty bucket, only fillable
				if (bucketItemMaxStackCount == 1 && region.containsFlag(RegionFlag.PLACE.toString()) && !region.permits(player)) {
					sendStatusMessage(player, new TranslationTextComponent("message.event.protection.place_fluid"));
					event.setCanceled(true);
					return;
				}
				// FIXME: Message is send if target raycast hits a non fluid. Check if event.getTarget hits a fluid
				if (bucketItemMaxStackCount > 1 && region.containsFlag(RegionFlag.BREAK.toString()) && !region.permits(player)) {
					sendStatusMessage(player, new TranslationTextComponent("message.event.protection.scoop_fluid"));
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	/**
	 * Checks is any region contains the specified flag
	 * @param regions regions to check for
	 * @param flag flag to be checked for
	 * @return true if any region contains the specified flag, false otherwise
	 */
	private static boolean anyRegionContainsFlag(List<IRegion> regions, String flag){
		return regions.stream()
				.anyMatch(region -> region.containsFlag(flag));
	}

	/**
	 * Filters affected blocks from explosion event which are in a region with the specified flag.
	 * @param event detonation event
	 * @param flag flag to be filtered for
	 * @return list of block positions which are in a region with the specified flag
	 */
	private static List<BlockPos> filterExplosionAffectedBlocks(ExplosionEvent.Detonate event, String flag){
		return event.getAffectedBlocks().stream()
				.filter(blockPos -> anyRegionContainsFlag(
						RegionUtils.getHandlingRegionsFor(blockPos, event.getWorld()),
						flag))
				.collect(Collectors.toList());
	}

	private static List<Entity> filterAffectedEntities(List<Entity> entities, String flag){
		return entities.stream()
				.filter(entity -> anyRegionContainsFlag(
						RegionUtils.getHandlingRegionsFor(entity.getPosition(), entity.world), flag))
				.collect(Collectors.toList());
	}
}
