package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventInteract {

	@SubscribeEvent
	public static void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event) {
		int dim = 1; // event.getWorld().getDimension().getType().getId();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPos(), dim)) {
			if (region.getFlags().contains("use")) {
				if (!(event.getWorld().getTileEntity(event.getPos()) instanceof LockableTileEntity))
					if (!region.isInPlayerList(event.getPlayer())) {
						event.setCanceled(true);
						// Added ", event.getPlayer().getUniqueID()"
						event.getPlayer().sendMessage(new TranslationTextComponent("world.interact.use"), event.getPlayer().getUniqueID());
						return;
					}
			}
			if (region.getFlags().contains("chest-access")) {
				if (!region.isInPlayerList(event.getPlayer())) {
					event.setCanceled(true);
					if (event.getHand() == Hand.MAIN_HAND)
						// Added ", event.getPlayer().getUniqueID()"
						event.getPlayer().sendMessage(new TranslationTextComponent("world.interact.use"), event.getPlayer().getUniqueID());
					return;
				}
			}
		}
	}

}
