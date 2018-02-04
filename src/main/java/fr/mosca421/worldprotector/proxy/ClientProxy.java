package fr.mosca421.worldprotector.proxy;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.items.ItemsRegister;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRender() {
		ModelLoader.setCustomModelResourceLocation(ItemsRegister.REGION_STICK, 0, new ModelResourceLocation(WorldProtector.MODID + ":" + "regionstick", "inventory"));
	}
}
