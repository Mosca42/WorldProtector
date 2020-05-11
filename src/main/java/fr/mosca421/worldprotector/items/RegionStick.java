package fr.mosca421.worldprotector.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("help.regionstick.1"));
		tooltip.add(new TranslationTextComponent("help.regionstick.2"));
	}
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		BlockPos pos = context.getPos();
		if (!world.isRemote) {
			if (player.getHeldItem(hand).hasTag()) {
				switch (player.getHeldItem(hand).getTag().getInt("id")) {
				case 0:
					player.getHeldItem(hand).getTag().putInt("x1", pos.getX());
					player.getHeldItem(hand).getTag().putInt("y1", pos.getY());
					player.getHeldItem(hand).getTag().putInt("z1", pos.getZ());
					player.getHeldItem(hand).getTag().putInt("id", 1);
					player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Position 1 : x=" + player.getHeldItem(hand).getTag().getInt("x1") + ", y=" + player.getHeldItem(hand).getTag().getInt("y1") + ", z=" + player.getHeldItem(hand).getTag().getInt("z1")));
					break;
				case 1:
					player.getHeldItem(hand).getTag().putInt("x2", pos.getX());
					player.getHeldItem(hand).getTag().putInt("y2", pos.getY());
					player.getHeldItem(hand).getTag().putInt("z2", pos.getZ());
					player.getHeldItem(hand).getTag().putInt("id", 0);
					player.getHeldItem(hand).getTag().putBoolean("valide", true);
					player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Position 2 : x=" + player.getHeldItem(hand).getTag().getInt("x2") + ", y=" + player.getHeldItem(hand).getTag().getInt("y2") + ", z=" + player.getHeldItem(hand).getTag().getInt("z2")));
					break;
				}
			}
		}
		return ActionResultType.SUCCESS;
		}


	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote) {
			if (!stack.hasTag()) {
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("id", 0);
				nbt.putBoolean("valide", false);
				stack.setTag(nbt);
			}
		}
	}
}
