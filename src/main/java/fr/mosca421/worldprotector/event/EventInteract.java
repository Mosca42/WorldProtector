package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventInteract {

	private EventInteract(){}

	@SubscribeEvent
	public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		List<IRegion> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), event.getWorld());
		if (!event.getWorld().isRemote) {
			for (IRegion region : regions) {
				PlayerEntity player = event.getPlayer();
				TileEntity targetEntity = event.getWorld().getTileEntity(event.getPos());
				boolean isLockableTileEntity = targetEntity instanceof LockableTileEntity;
				boolean isEnderChest = targetEntity instanceof EnderChestTileEntity;
				boolean isContainer = targetEntity instanceof LecternTileEntity || isLockableTileEntity;
				boolean isPlayerProhibited = region.forbids(player);

				// check for trapped chest
				if (region.containsFlag(RegionFlag.USE) && !isLockableTileEntity && isPlayerProhibited) {
					if (!player.isSneaking()) {
						event.setCanceled(true);
						MessageUtils.sendMessage(player, "message.event.interact.use");
						return;
					}
				}
				// check for ender chest access
				if (region.containsFlag(RegionFlag.ENDER_CHEST_ACCESS) && isEnderChest && isPlayerProhibited) {
					if (!player.isSneaking()) {
						event.setCanceled(true);
						MessageUtils.sendMessage(player, "message.event.interact.access_ender_chest");
						return;
					}
				}
				// check for container access
				if (region.containsFlag(RegionFlag.CONTAINER_ACCESS) && isContainer && isPlayerProhibited) {
					if (!player.isSneaking()) {
						event.setCanceled(true);
						MessageUtils.sendMessage(player, "message.event.interact.access_container");
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
		List<IRegion> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), event.getWorld());
		if (!event.getWorld().isRemote) {
			for (IRegion region : regions) {
				PlayerEntity player = event.getPlayer();
				boolean containsChestAccess = region.containsFlag(RegionFlag.CONTAINER_ACCESS);
				boolean playerHasPermission = region.permits(player);
				boolean isMinecartContainer = event.getTarget() instanceof ContainerMinecartEntity;

				if (containsChestAccess && !playerHasPermission && isMinecartContainer) {
					event.setCanceled(true);
					MessageUtils.sendMessage(player, "message.event.interact.access_container");
					return;
				}
			}
		}
	}
}
