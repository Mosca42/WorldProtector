package fr.mosca421.worldprotector.network;

import fr.mosca421.worldprotector.WorldProtector;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class LoginHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        RegionSyncManager.sendAllRegions(event.getPlayer());
        WorldProtector.LOGGER.debug("Sent region infos to player: " + event.getPlayer());
        // RegionConfig.syncServerConfigs(event.getPlayer());
    }



}
