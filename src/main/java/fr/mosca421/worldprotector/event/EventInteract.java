package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.*;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventInteract {

	private EventInteract(){}

	@SubscribeEvent
	public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), RegionUtils.getDimension(event.getWorld()));
		if (!event.getWorld().isRemote) {
			for (Region region : regions) {
				PlayerEntity player = event.getPlayer();
				TileEntity target = event.getWorld().getTileEntity(event.getPos());
				boolean containsUse = region.containsFlag(RegionFlag.USE.toString());
				boolean containsChestAccess = region.containsFlag(RegionFlag.CHEST_ACCESS.toString());
				boolean isLockableTileEntity = target instanceof LockableTileEntity;
				boolean isEnderChest = target instanceof EnderChestTileEntity;
				boolean isLectern = target instanceof LecternTileEntity;
				boolean isTrappedChest = target instanceof TrappedChestTileEntity;
				boolean isContainer = isLectern || isEnderChest || isLockableTileEntity;
				boolean playerHasPermission = region.permits(player);
				// FIXME: Prevents block placement
				// TODO: use flag not covering tripwire and pressure plate
				if (containsUse && (!isLockableTileEntity || isTrappedChest) && !playerHasPermission) {
					event.setCanceled(true);
					MessageUtils.sendMessage(player, "world.interact.use");
					return;
				}

				if (containsChestAccess && !playerHasPermission && isContainer) {
					event.setCanceled(true);
					// Shift click block placement on containers not possible
					if (event.getHand() == Hand.MAIN_HAND) {
						MessageUtils.sendMessage(player, "world.interact.container");
					}
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
		List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), RegionUtils.getDimension(event.getWorld()));
		if (!event.getWorld().isRemote) {
			for (Region region : regions) {
				PlayerEntity player = event.getPlayer();
				boolean containsChestAccess = region.containsFlag(RegionFlag.CHEST_ACCESS.toString());
				boolean playerHasPermission = region.permits(player);
				boolean isMinecartContainer = event.getTarget() instanceof ContainerMinecartEntity;

				if (containsChestAccess && !playerHasPermission && isMinecartContainer) {
					event.setCanceled(true);
					if (event.getHand() == Hand.MAIN_HAND) {
						MessageUtils.sendMessage(player, "world.interact.container");
					}
					return;
				}
			}
		}
	}

}
