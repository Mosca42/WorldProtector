package fr.mosca421.worldprotector.event;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.RegionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static fr.mosca421.worldprotector.event.EventMobs.isMonster;
import static fr.mosca421.worldprotector.util.RegionUtils.getHandlingRegionsFor;

@Mod.EventBusSubscriber(modid = WorldProtector.MODID)
public class EventWorld {

    private EventWorld() {
    }

    @SubscribeEvent
    public static void onFarmLandTrampled(BlockEvent.FarmlandTrampleEvent event) {
        if (!event.getWorld().isRemote()) {
            List<IRegion> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), (World) event.getWorld());
            for (IRegion r : regions) {
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
                        if (!r.isMuted()) {
                            MessageUtils.sendStatusMessage(player, "message.event.world.trample_farmland");
                        }
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

    @SubscribeEvent
    public static void onLightningStrikeOccur(EntityStruckByLightningEvent event){
        Entity poorBastard = event.getEntity();
        if (!poorBastard.world.isRemote) {
            boolean isLightningProhibited = RegionUtils.getHandlingRegionsFor(poorBastard.getPosition(), poorBastard.world).stream()
                    .anyMatch(region -> region.containsFlag(RegionFlag.LIGHTNING_PROT));
            if (isLightningProhibited) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBonemealUse(BonemealEvent event){
        if (!event.getWorld().isRemote) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            List<IRegion> regions = RegionUtils.getHandlingRegionsFor(event.getPos(), player.world);
            for (IRegion region : regions) {
                if (region.containsFlag(RegionFlag.USE_BONEMEAL) && region.forbids(player)) {
                    event.setCanceled(true);
                    if (!region.isMuted()) {
                        MessageUtils.sendStatusMessage(player, "message.event.world.use_bone_meal");
                    }
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityXpDrop(LivingExperienceDropEvent event){
        if (!event.getEntityLiving().world.isRemote) {
            PlayerEntity player = event.getAttackingPlayer();
            Entity entity = event.getEntity();
            List<IRegion> regions = RegionUtils.getHandlingRegionsFor(entity, entity.world);
            boolean entityDroppingXpIsPlayer = event.getEntityLiving() instanceof PlayerEntity;
            for (IRegion region : regions) {
                // prevent all xp drops
                if (region.containsFlag(RegionFlag.XP_DROP_ALL)) {
                    if (entityDroppingXpIsPlayer) {
                        event.setCanceled(true);
                        return;
                    }
                    if (region.forbids(player)) {
                        event.setCanceled(true);
                        if (!region.isMuted()) {
                            MessageUtils.sendStatusMessage(player, "message.event.world.exp_drop.all");
                        }
                        return;
                    }
                }
                // prevent monster xp drop
                if (region.containsFlag(RegionFlag.XP_DROP_MONSTER) && isMonster(entity) && region.forbids(player)) {
                    event.setCanceled(true);
                    if (!region.isMuted()) {
                        MessageUtils.sendStatusMessage(player, "message.event.world.exp_drop.monsters");
                    }
                    return;
                }
                // prevent other entity xp drop (villagers, animals, ..)
                if (region.containsFlag(RegionFlag.XP_DROP_OTHER) && !isMonster(entity) && !entityDroppingXpIsPlayer) {
                    if (region.forbids(player)) {
                        event.setCanceled(true);
                        if (!region.isMuted()) {
                            MessageUtils.sendStatusMessage(player, "message.event.world.exp_drop.non_hostile");
                        }
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDropLoot(LivingDropsEvent event){
        LivingEntity lootEntity = event.getEntityLiving();
        if (!lootEntity.world.isRemote) {
            boolean isLootDropProhibited = RegionUtils.getHandlingRegionsFor(lootEntity.getPosition(), lootEntity.world).stream()
                    .anyMatch(region -> region.containsFlag(RegionFlag.LOOT_DROP));
            if (isLootDropProhibited) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDestroyBlock(LivingDestroyBlockEvent event){
        if (!event.getEntityLiving().world.isRemote) {
            LivingEntity destroyer = event.getEntityLiving();
            List<IRegion> regions = RegionUtils.getHandlingRegionsFor(destroyer, destroyer.world);
            for (IRegion region : regions) {
                if (region.containsFlag(RegionFlag.DRAGON_BLOCK_PROT) && destroyer instanceof EnderDragonEntity) {
                    event.setCanceled(true);
                    return;
                }

                if (region.containsFlag(RegionFlag.WITHER_BLOCK_PROT) && destroyer instanceof WitherEntity) {
                    event.setCanceled(true);
                    return;
                }
                if (region.containsFlag(RegionFlag.ZOMBIE_DOOR_PROT) && destroyer instanceof ZombieEntity) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onNetherPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        List<IRegion> regions = getHandlingRegionsFor(event.getPos(), (World) event.getWorld());
        for (IRegion region : regions) {
            if (region.containsFlag(RegionFlag.SPAWN_PORTAL)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    // This event is only fired for PlayerEntities
    @SubscribeEvent
    public static void onChangeDimension(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        List<IRegion> regions = RegionUtils.getHandlingRegionsFor(entity.getPosition(), entity.world);
        for (IRegion region : regions) {
            if (region.containsFlag(RegionFlag.USE_PORTAL.toString())) {

                if (entity instanceof PlayerEntity) {
                    event.setCanceled(true);
                    if (!region.isMuted()) {
                        MessageUtils.sendStatusMessage((PlayerEntity) entity, "message.event.player.change_dim");
                    }
                    return;
                }
            }
        }
    }
}
