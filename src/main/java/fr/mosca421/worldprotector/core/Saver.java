package fr.mosca421.worldprotector.core;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class Saver extends WorldSavedData {
	private static Saver INSTANCE;

	public static final HashMap<String, Region> REGIONS = new HashMap<String, Region>();

	public Saver() {
		super("worldprotector");
	}

	public static void save() {
		if (INSTANCE != null) {
			INSTANCE.markDirty();
		}
	}


	public static void addRegion(Region region) {
		REGIONS.put(region.getName(), region);
	}

	@Override
	public void read(CompoundNBT nbt) {
		REGIONS.clear();
		ListNBT regionsList = nbt.getList("regions", NBT.TAG_COMPOUND);
		for (int i = 0; i < regionsList.size(); i++) {
			Region area = new Region();
			area.deserializeNBT(regionsList.getCompound(i));
			REGIONS.put(area.getName(), area);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT regionsList = new ListNBT();
		for (Region area : REGIONS.values()) {
			regionsList.add(area.serializeNBT());
		}
		compound.put("regions", regionsList);
		return compound;
	}

	public static void onServerStarting(FMLServerStartingEvent event) {
		if (!event.getServer().getWorld(DimensionType.OVERWORLD).isRemote) {
			DimensionSavedDataManager storage = event.getServer().getWorld(DimensionType.OVERWORLD).getSavedData();
			Saver data = storage.get(Saver::new, "worldprotector");
			if (data == null) {
				data = new Saver();
				storage.set(data);
			}
			INSTANCE = data;
		}
	}

}
