package fr.mosca421.worldprotector.core;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Saver extends WorldSavedData {
	private static Saver INSTANCE;

	public static final HashMap<String, Region> REGIONS = new HashMap<String, Region>();


	public static void save() {
		if (INSTANCE != null) {
			INSTANCE.markDirty();
		}
	}

	public Saver(String name) {
		super(name);
	}

	public static void addRegion(Region region){
		REGIONS.put(region.getName(), region);
	}
	
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
    	REGIONS.clear();
        NBTTagList regionsList = nbt.getTagList("regions", NBT.TAG_COMPOUND);
        for (int i = 0; i < regionsList.tagCount(); i++) {
            Region area = new Region();
            area.deserializeNBT(regionsList.getCompoundTagAt(i));
            REGIONS.put(area.getName(), area);
        }
    }
 
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList regionsList = new NBTTagList();
        for (Region area : REGIONS.values()) {
            regionsList.appendTag(area.serializeNBT());
        }
        compound.setTag("regions", regionsList);
        return compound;
    }

	public static void onServerStarting(FMLServerStartingEvent event) {
		if (!event.getServer().getEntityWorld().isRemote) {
			MapStorage storage = event.getServer().getEntityWorld().getMapStorage();
			Saver data = (Saver) storage.getOrLoadData(Saver.class, "worldprotector");
			if (data == null) {
				data = new Saver("worldprotector");
				storage.setData("worldprotector", data);
			}
			INSTANCE = data;
		}
	}

}
