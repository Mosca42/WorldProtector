package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventTeleport {

    private EventTeleport() {
    }

    @SubscribeEvent
    public static void onEnderTeleportTo(EnderTeleportEvent event) {
        World world = event.getEntity().world;
        if (!world.isRemote()) {
            BlockPos teleportTarget = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            BlockPos teleportSource = event.getEntity().getPosition();
            List<Region> regionsTo = RegionUtils.getHandlingRegionsFor(teleportTarget, world);
            List<Region> regionsFrom = RegionUtils.getHandlingRegionsFor(teleportSource, world);
            // handle player teleportation using ender pearls
            if (event.getEntityLiving() instanceof PlayerEntity) {
                handlePlayerEnderPearlUse(event, regionsFrom, regionsTo);
            }
            // handle enderman teleportation
            if (event.getEntity() instanceof EndermanEntity) {
                handleEndermanTp(event, regionsFrom, regionsTo);
            }
            // handle shulker teleportation
            if (event.getEntity() instanceof ShulkerEntity) {
                handleShulkerTp(event, regionsFrom, regionsTo);
            }
        }
    }

    private static void handlePlayerEnderPearlUse(EnderTeleportEvent event, List<Region> regionsFrom, List<Region> regionsTo) {
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        boolean playerTpToProhibited = regionsTo.stream()
                .anyMatch(region -> region.containsFlag(RegionFlag.USE_ENDERPEARL_TO_REGION) && region.forbids(player));
        if (playerTpToProhibited) {
            MessageUtils.sendMessage(player, "message.event.teleport.ender_pearl.to_region");
        }
        boolean playerTpFromProhibited = regionsFrom.stream()
                .anyMatch(region -> region.containsFlag(RegionFlag.USE_ENDERPEARL_FROM_REGION) && region.forbids(player));
        if (playerTpFromProhibited) {
            MessageUtils.sendMessage(player, "message.event.teleport.ender_pearl.from_region");
        }

        if (playerTpFromProhibited || playerTpToProhibited) {
            event.setCanceled(true);
            // refund pearl
            int count = player.getHeldItem(player.getActiveHand()).getCount();
            player.getHeldItem(player.getActiveHand()).setCount(count + 1);
        }
    }

    private static void handleShulkerTp(EnderTeleportEvent event, List<Region> regionsFrom, List<Region> regionsTo) {
        boolean shulkerTpFromProhibited = regionsFrom.stream()
                .anyMatch(region -> region.containsFlag(RegionFlag.SHULKER_TELEPORT_FROM_REGION));
        if (shulkerTpFromProhibited) {
            event.setCanceled(true);
        }
        boolean shulkerTpToProhibited = regionsTo.stream()
                .anyMatch(region -> region.containsFlag(RegionFlag.SHULKER_TELEPORT_TO_REGION));
        if (shulkerTpToProhibited) {
            event.setCanceled(true);
        }
    }

    private static void handleEndermanTp(EnderTeleportEvent event, List<Region> regionsFrom, List<Region> regionsTo) {
        boolean endermanTpFromProhibited = regionsFrom.stream()
                .anyMatch(region -> region.containsFlag(RegionFlag.ENDERMAN_TELEPORT_FROM_REGION));
        if (endermanTpFromProhibited) {
            event.setCanceled(true);
        }
        boolean endermanTpToProhibited = regionsTo.stream()
                .anyMatch(region -> region.containsFlag(RegionFlag.ENDERMAN_TELEPORT_TO_REGION));
        if (endermanTpToProhibited) {
            event.setCanceled(true);
        }
    }
}
