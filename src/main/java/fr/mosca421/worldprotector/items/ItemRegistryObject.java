package fr.mosca421.worldprotector.items;

import fr.mosca421.worldprotector.utils.WrappedRegistryObject;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

public class ItemRegistryObject<ITEM extends Item> extends WrappedRegistryObject<ITEM> implements IItemProvider {

    public ItemRegistryObject(RegistryObject<ITEM> registryObject) {
        super(registryObject);
    }

	@Override
	public Item asItem() {
		return get();
	}
}