package fr.mosca421.worldprotector.event;

import java.util.List;
import java.util.stream.Collectors;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static fr.mosca421.worldprotector.util.MessageUtils.*;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventProtection {

	private EventProtection(){}

	@SubscribeEvent
	public static void onPlayerBreakBlock(BreakEvent event) {
		if (!event.getWorld().isRemote()) {
			PlayerEntity player = event.getPlayer();
			List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), RegionUtils.getDimension(player.world));
			for (Region region : regions) {
				if (region.containsFlag(RegionFlag.BREAK.toString()) && !region.permits(player)) {
					player.sendMessage(new TranslationTextComponent("world.protection.break"), player.getUniqueID());
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerPlaceBlock(EntityPlaceEvent event) {
		if (!event.getWorld().isRemote()) {
			List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), RegionUtils.getDimension((World) event.getWorld()));
			for (Region region : regions) {
				if (region.containsFlag(RegionFlag.PLACE.toString())) {
					if (event.getEntity() instanceof PlayerEntity) {
						if (!region.permits(((PlayerEntity) event.getEntity()))) {
							event.getEntity().sendMessage(new TranslationTextComponent("world.protection.place"), event.getEntity().getUniqueID());
							event.setCanceled(true);
							return;
						}
					} else {
						event.setCanceled(true);
						return;
					}
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
	private static boolean anyRegionContainsFlag(List<Region> regions, String flag){
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
						RegionUtils.getHandlingRegionsFor(blockPos, RegionUtils.getDimension(event.getWorld())),
						flag))
				.collect(Collectors.toList());
	}

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		event.getAffectedBlocks().removeAll(filterExplosionAffectedBlocks(event, RegionFlag.EXPLOSION.toString()));

		boolean explosionTriggeredByCreeper = (event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity);
		if (!explosionTriggeredByCreeper) {
			event.getAffectedBlocks().removeAll(filterExplosionAffectedBlocks(event, RegionFlag.EXPLOSION_OTHER.toString()));
		}
		if (explosionTriggeredByCreeper) {
			event.getAffectedBlocks().removeAll(filterExplosionAffectedBlocks(event, RegionFlag.EXPLOSION_CREEPER.toString()));
		}
	}

	@SubscribeEvent
	// Note: Does not prevent from fluids generate additional blocks (cobble generator). Use BlockEvent.FluidPlaceBlockEvent for this
	public static void onBucketFill(FillBucketEvent event) {
		PlayerEntity player = event.getPlayer();
		if (!event.getWorld().isRemote && event.getTarget() != null) {
			List<Region> regions = RegionUtils.getHandlingRegionsFor(new BlockPos(event.getTarget().getHitVec()), RegionUtils.getDimension(event.getWorld()));
			for (Region region : regions) {
				int bucketItemMaxStackCount = event.getEmptyBucket().getMaxStackSize();
				// MaxStackSize: 1 -> full bucket so only placeable; >1 -> empty bucket, only fillable
				if (bucketItemMaxStackCount == 1 && region.containsFlag(RegionFlag.PLACE.toString()) && !region.permits(player)) {
					sendMessage(player, new TranslationTextComponent("world.protection.placefluid"));
					event.setCanceled(true);
					return;
				}
				if (bucketItemMaxStackCount > 1 && region.containsFlag(RegionFlag.BREAK.toString()) && !region.permits(player)) {
					sendMessage(player, new TranslationTextComponent("world.protection.scoopfluid"));
					event.setCanceled(true);
					return;
				}
			}
		}
	}
}
