package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.items.ItemsRegister;
import fr.mosca421.worldprotector.utils.RegionsUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventInteract {

	@SubscribeEvent
	public static void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event) {
		int dim = event.getWorld().getDimension().getType().getId();
		for (Region region : RegionsUtils.getHandlingRegionsFor(event.getPos(), dim)) {
			if (region.getFlags().contains("use")) {
				if (!(event.getWorld().getTileEntity(event.getPos()) instanceof LockableTileEntity))
					if (!region.isInPlayerList(event.getPlayer())) {
						event.setCanceled(true);
						event.getPlayer().sendMessage(new TranslationTextComponent("world.interact.use"));
						return;
					}
			}
			if (region.getFlags().contains("chest-access")) {
				if (!region.isInPlayerList(event.getPlayer())) {
					event.setCanceled(true);
					if (event.getHand() == Hand.MAIN_HAND)
						event.getPlayer().sendMessage(new TranslationTextComponent("world.interact.use"));
					return;
				}
			}
		}
	}

}
