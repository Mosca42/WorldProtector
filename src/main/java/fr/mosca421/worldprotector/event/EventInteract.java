package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventInteract {

	private EventInteract(){}

	@SubscribeEvent
	public static void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event) {
		List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), RegionUtils.getDimension(event.getWorld()));
		for (Region region : regions) {
			PlayerEntity player = event.getPlayer();
			boolean containsUse = region.getFlags().contains(RegionFlag.USE.toString());
			boolean containsChestAccess = region.getFlags().contains(RegionFlag.CHEST_ACCESS.toString());
			boolean isLockableTileEntity = (event.getWorld().getTileEntity(event.getPos()) instanceof LockableTileEntity);
			boolean playerHasPermission = region.permits(player);

			if (containsUse && !isLockableTileEntity && !playerHasPermission) {
				event.setCanceled(true);
				MessageUtils.sendMessage(player, "world.interact.use");
				return;
			}

			if (containsChestAccess && !playerHasPermission) {
				event.setCanceled(true);
				if (event.getHand() == Hand.MAIN_HAND) {
					// FIXME: message is send twice for unknown reason
					MessageUtils.sendMessage(player, "world.interact.use");
				}
				return;

			}
		}
	}

}
