package fr.mosca421.worldprotector.items;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.utils.ItemDeferredRegister;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class ItemsRegister {

    private ItemsRegister() {}
	
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(WorldProtector.MODID);

    public static final ItemRegistryObject<RegionStick> REGION_STICK = ITEMS.register("region_stick", RegionStick::new);
    public static final ItemRegistryObject<Emblem> EMBLEM = ITEMS.register("emblem", Emblem::new);

}
