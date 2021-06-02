package fr.mosca421.worldprotector.network;

import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.data.RegionManager;
import fr.mosca421.worldprotector.network.message.RegionSyncMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

import static fr.mosca421.worldprotector.core.RegionManager.getPlayerRegionData;

public class RegionSyncManager {

    public static void sendAllRegions(PlayerEntity player) {
        List<IRegion> allRegions = new ArrayList<>(RegionManager.get().getAllRegions());
        NetworkHandler.sendTo(new RegionSyncMessage(allRegions), player);
    }

    public static void sendRegionsToAll() {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : players) {
            sendAllRegions(player);
        }
    }

    public static void deactivateRegion(ServerPlayerEntity player, IRegion region) {
    }

    public static void activateRegion(ServerPlayerEntity player, IRegion region) {

    }

    public static void removeRegion(ServerPlayerEntity player, IRegion region) {
        getPlayerRegionData(player.world).removeRegion(player, region);
    }

    public static void addRegion(ServerPlayerEntity player, IRegion region) {
        getPlayerRegionData(player.world).addRegion(player, region);
    }

    public static void updateRegion(ServerPlayerEntity player, IRegion region) {
        getPlayerRegionData(player.world).updateRegion(player, region);
    }
}
