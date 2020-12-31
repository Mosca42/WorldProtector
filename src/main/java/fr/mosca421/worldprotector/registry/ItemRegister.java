package fr.mosca421.worldprotector.registry;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.item.Emblem;
import fr.mosca421.worldprotector.item.RegionStick;

public class ItemRegister {

    private ItemRegister() {}
	
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(WorldProtector.MODID);

    public static final ItemRegistryObject<RegionStick> REGION_STICK = ITEMS.register("region_stick", RegionStick::new);
    public static final ItemRegistryObject<Emblem> EMBLEM = ITEMS.register("emblem", Emblem::new);

}
