package fr.mosca421.worldprotector;

import fr.mosca421.worldprotector.commands.CommandsRegister;
import fr.mosca421.worldprotector.core.Saver;
import fr.mosca421.worldprotector.items.ItemsRegister;
import fr.mosca421.worldprotector.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = WorldProtector.MODID, version = WorldProtector.VERSION)
public class WorldProtector {

	@SidedProxy(clientSide = "fr.mosca421.worldprotector.proxy.ClientProxy", serverSide = "fr.mosca421.worldprotector.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static final String MODID = "worldprotector";
	public static final String VERSION = "1.0";
	@Instance(MODID)
	public static WorldProtector instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.registerRender();
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		CommandsRegister.init(event);
        Saver.onServerStarting(event);
	}
}
