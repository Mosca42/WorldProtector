package fr.mosca421.worldprotector.item;

import java.util.List;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.util.MessageUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class RegionStick extends Item {

	public RegionStick() {
		super(new Item.Properties()
				.maxStackSize(1)
				.group(WorldProtector.WORLD_PROTECTOR_TAB));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent("help.regionstick.detail.1"));
			tooltip.add(new TranslationTextComponent("help.regionstick.detail.2"));
			tooltip.add(new TranslationTextComponent("help.regionstick.optional").mergeStyle(TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent("help.regionstick.detail.3"));
			tooltip.add(new TranslationTextComponent("help.regionstick.detail.4").mergeStyle(TextFormatting.RED));
		} else {
			tooltip.add(new TranslationTextComponent("help.regionstick.simple.1"));
			tooltip.add(new TranslationTextComponent("help.regionstick.simple.2"));
			tooltip.add(new StringTextComponent( "Hold " + TextFormatting.DARK_BLUE + TextFormatting.ITALIC + "SHIFT" + TextFormatting.RESET + " for more details."));
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		BlockPos pos = context.getPos();
		if (!world.isRemote) {
			ItemStack playerHeldItem = player.getHeldItem(hand);
			if (playerHeldItem.hasTag()) {
				CompoundNBT playerItemTag = playerHeldItem.getTag();
				switch (playerItemTag.getInt("id")) {
					case 0:
						playerItemTag.putInt("x1", pos.getX());
						playerItemTag.putInt("y1", pos.getY());
						playerItemTag.putInt("z1", pos.getZ());
						playerItemTag.putInt("id", 1);
						playerItemTag.putBoolean("valide", false);
						MessageUtils.sendMessage(player, new StringTextComponent(TextFormatting.DARK_RED + "Position 1 : x=" + player.getHeldItem(hand).getTag().getInt("x1") + ", y=" + player.getHeldItem(hand).getTag().getInt("y1") + ", z=" + player.getHeldItem(hand).getTag().getInt("z1")));
						break;
					case 1:
						playerItemTag.putInt("x2", pos.getX());
						playerItemTag.putInt("y2", pos.getY());
						playerItemTag.putInt("z2", pos.getZ());
						playerItemTag.putInt("id", 0);
						playerItemTag.putBoolean("valide", true);
						MessageUtils.sendMessage(player, new StringTextComponent(TextFormatting.DARK_RED + "Position 2 : x=" + player.getHeldItem(hand).getTag().getInt("x2") + ", y=" + player.getHeldItem(hand).getTag().getInt("y2") + ", z=" + player.getHeldItem(hand).getTag().getInt("z2")));
						break;
					default:
						// Never reached
						break;
				}
			}
		}
		return ActionResultType.SUCCESS;
	}



	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote && !stack.hasTag()) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("id", 0);
			nbt.putBoolean("valide", false);
			stack.setTag(nbt);
		}
	}
}
