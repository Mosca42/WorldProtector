package fr.mosca421.worldprotector.items;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.utils.ItemDeferredRegister;

public class ItemsRegister {

	
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(WorldProtector.MODID);

    public static final ItemRegistryObject<RegionStick> REGION_STICK = ITEMS.register("region_stick", RegionStick::new);

}
