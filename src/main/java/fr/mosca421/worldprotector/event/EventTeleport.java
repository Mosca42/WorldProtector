package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
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
        if (!event.getEntity().world.isRemote) {
            BlockPos teleportTarget = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            BlockPos teleportSource = event.getEntity().getPosition();
            List<Region> regionsTo = RegionUtils.getHandlingRegionsFor(teleportTarget, RegionUtils.getDimension(event.getEntity().world));
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                for (Region region : regionsTo) {
                    if (region.containsFlag(RegionFlag.USE_ENDERPEARL_TO.toString()) && !region.permits(player)) {
                        event.setCanceled(true);
                        // refund enderpearl
                        int count = player.getHeldItem(player.getActiveHand()).getCount();
                        player.getHeldItem(player.getActiveHand()).setCount(count + 1);
                        player.sendMessage(new TranslationTextComponent("world.ender.player.to"), player.getUniqueID());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEnderTeleportFrom(EnderTeleportEvent event) {
        if (!event.getEntity().world.isRemote) {
            BlockPos teleportSource = event.getEntity().getPosition();
            List<Region> regionsFrom = RegionUtils.getHandlingRegionsFor(teleportSource, RegionUtils.getDimension(event.getEntity().world));
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                for (Region region : regionsFrom) {
                    if (region.containsFlag(RegionFlag.USE_ENDERPEARL_FROM.toString()) && !region.permits(player)) {
                        event.setCanceled(true);
                        // refund enderpearl
                        int count = player.getHeldItem(player.getActiveHand()).getCount();
                        player.getHeldItem(player.getActiveHand()).setCount(count + 1);
                        player.sendMessage(new TranslationTextComponent("world.ender.player.from"), player.getUniqueID());
                    }
                }
            }
        }
    }
}
