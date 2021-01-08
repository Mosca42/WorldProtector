package fr.mosca421.worldprotector.data;

import java.util.*;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class RegionSaver extends WorldSavedData {

	private static RegionSaver instance;
	private static final Map<String, Region> Regions = new HashMap<>();

	public RegionSaver() {
		super("worldprotector");
	}

	public static void save() {
		if (instance != null) {
			instance.markDirty();
		}
	}

	public static Set<String> getRegionFlags(String regionName){
		return Regions.get(regionName).getFlags();
	}

	public static Collection<Region> getRegions(){
		return Regions.values();
	}

	public static Collection<String> getRegionNames(){
		return Regions.keySet();
	}

	public static Region removeRegion(String regionName) {
		return Regions.remove(regionName);
	}

	public static void clearRegions(){
		Regions.clear();
	}

	public static void replaceRegion(Region newRegion){
		Region oldRegion = Regions.get(newRegion.getName());
		oldRegion.getFlags().forEach(newRegion::addFlag);
		removeRegion(oldRegion.getName());
		addRegion(newRegion);
	}

	public static Region getRegion(String regionName){
		return Regions.get(regionName);
	}

	public static boolean containsRegion(String regionName){
		return Regions.containsKey(regionName);
	}

	public static void addRegion(Region region) {
		Regions.put(region.getName(), region);
	}

	@Override
	public void read(CompoundNBT nbt) {
		Regions.clear();
		ListNBT regionsList = nbt.getList("regions", NBT.TAG_COMPOUND);
		for (int i = 0; i < regionsList.size(); i++) {
			Region area = new Region(regionsList.getCompound(i));
			Regions.put(area.getName(), area);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT regionsList = new ListNBT();
		for (Region area : Regions.values()) {
			regionsList.add(area.serializeNBT());
		}
		compound.put("regions", regionsList);
		return compound;
	}

	public static void onServerStarting(FMLServerStartingEvent event) {
		try {
			ServerWorld world = Objects.requireNonNull(event.getServer().getWorld(World.OVERWORLD));
			if (!(world).isRemote) {
				DimensionSavedDataManager storage = world.getSavedData();
				RegionSaver data = storage.get(RegionSaver::new, "worldprotector");
				if (data == null) {
					data = new RegionSaver();
					storage.set(data);
					WorldProtector.LOGGER.debug("Loaded dimension regions successfully");
				}
				instance = data;
			}
		} catch (NullPointerException npe) {
			WorldProtector.LOGGER.error("Loading dimension regions failed");
		}
	}

}
