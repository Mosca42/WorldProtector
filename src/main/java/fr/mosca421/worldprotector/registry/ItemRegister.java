package fr.mosca421.worldprotector.registry;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.item.ItemEmblem;
import fr.mosca421.worldprotector.item.ItemFlagStick;
import fr.mosca421.worldprotector.item.ItemRegionMarker;
import fr.mosca421.worldprotector.item.ItemRegionStick;

public class ItemRegister {

    private ItemRegister() {}
	
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(WorldProtector.MODID);

    public static final ItemRegistryObject<ItemRegionStick> REGION_STICK = ITEMS.register("region_stick", ItemRegionStick::new);
    public static final ItemRegistryObject<ItemRegionMarker> REGION_MARKER = ITEMS.register("region_marker", ItemRegionMarker::new);
    public static final ItemRegistryObject<ItemFlagStick> FLAG_STICK = ITEMS.register("flag_stick", ItemFlagStick::new);
    public static final ItemRegistryObject<ItemEmblem> EMBLEM = ITEMS.register("emblem", ItemEmblem::new);

}
