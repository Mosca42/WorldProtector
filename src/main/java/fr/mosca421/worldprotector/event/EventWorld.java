package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventWorld {

    private EventWorld() {
    }

    @SubscribeEvent
    public static void onFarmLandTrampled(BlockEvent.FarmlandTrampleEvent event) {
        if (!event.getWorld().isRemote()) {
            List<Region> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), RegionUtils.getDimension((World) event.getWorld()));
            for (Region r : regions) {
                // cancel all trampling
                if (r.containsFlag(RegionFlag.TRAMPLE_FARMLAND.toString())) {
                    event.setCanceled(true);
                    return;
                }
                // cancel only player trampling
                if (event.getEntity() instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) event.getEntity();
                    if (r.containsFlag(RegionFlag.TRAMPLE_FARMLAND_PLAYER.toString())) {
                        event.setCanceled(true);
                        MessageUtils.sendMessage(player, "world.protection.trampfarmland");
                    }
                } else {
                    // cancel trampling by other entities
                    if (r.containsFlag(RegionFlag.TRAMPLE_FARMLAND_OTHER.toString())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

}
