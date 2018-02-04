package fr.mosca421.worldprotector.events;

import fr.mosca421.worldprotector.core.AxisRegions;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.Saver;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EventMobs {

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		for (Region region : Saver.REGIONS.values()) {
			if (region.getArea().isVecInside(new Vec3d(event.getEntity().getPosition()))) {
				if (region.getFlags().contains("mob-spawning-all")) {
					if (event.getEntity() instanceof EntityMob || event.getEntity() instanceof EntityAnimal || event.getEntity() instanceof EntityWaterMob || event.getEntity() instanceof EntitySlime)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("mob-spawning-animals")) {
					if (event.getEntity() instanceof EntityAnimal || event.getEntity() instanceof EntityWaterMob)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("mob-spawning-monsters")) {
					if (event.getEntity() instanceof EntityMob || event.getEntity() instanceof EntitySlime)
						event.setCanceled(true);
				}
				if (region.getFlags().contains("exp-drop")) {
					if (event.getEntity() instanceof EntityXPOrb) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onAttackEntityAnimal(AttackEntityEvent event) {
		if (!event.getTarget().world.isRemote) {
			if (event.getTarget() instanceof EntityAnimal) {
				EntityAnimal animal = (EntityAnimal) event.getTarget();
				for (Region region : Saver.REGIONS.values()) {
					if (region.getArea().isVecInside(new Vec3d(animal.getPosition()))) {
						if (region.getFlags().contains("damage-animals")) {
							event.getEntityPlayer().sendMessage(new TextComponentTranslation("world.hurt.mob"));
							event.setCanceled(true);
						}
					}
				}
			}
			
			if (event.getTarget() instanceof EntityMob) {
				EntityMob animal = (EntityMob) event.getTarget();
				for (Region region : Saver.REGIONS.values()) {
					if (region.getArea().isVecInside(new Vec3d(animal.getPosition()))) {
						if (region.getFlags().contains("damage-monsters")) {
							event.getEntityPlayer().sendMessage(new TextComponentTranslation("world.hurt.mob"));
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

}
