package fr.mosca421.worldprotector.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.FMLServerHandler;

public class RegionStick extends Item {

	public RegionStick(String name) {
		this.setUnlocalizedName(name);
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setRegistryName(name);
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("help.regionstick.1"));
		tooltip.add(I18n.format("help.regionstick.2"));
	}
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		if (!worldIn.isRemote) {
			if (player.getHeldItem(hand).hasTagCompound()) {
				switch (player.getHeldItem(hand).getTagCompound().getInteger("id")) {
				case 0:
					player.getHeldItem(hand).getTagCompound().setInteger("x1", pos.getX());
					player.getHeldItem(hand).getTagCompound().setInteger("y1", pos.getY());
					player.getHeldItem(hand).getTagCompound().setInteger("z1", pos.getZ());
					player.getHeldItem(hand).getTagCompound().setInteger("id", 1);
					player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Position 1 : x=" + player.getHeldItem(hand).getTagCompound().getInteger("x1") + ", y=" + player.getHeldItem(hand).getTagCompound().getInteger("y1") + ", z=" + player.getHeldItem(hand).getTagCompound().getInteger("z1")));
					break;
				case 1:
					player.getHeldItem(hand).getTagCompound().setInteger("x2", pos.getX());
					player.getHeldItem(hand).getTagCompound().setInteger("y2", pos.getY());
					player.getHeldItem(hand).getTagCompound().setInteger("z2", pos.getZ());
					player.getHeldItem(hand).getTagCompound().setInteger("id", 0);
					player.getHeldItem(hand).getTagCompound().setBoolean("valide", true);
					player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Position 2 : x=" + player.getHeldItem(hand).getTagCompound().getInteger("x2") + ", y=" + player.getHeldItem(hand).getTagCompound().getInteger("y2") + ", z=" + player.getHeldItem(hand).getTagCompound().getInteger("z2")));
					break;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote) {
			if (!stack.hasTagCompound()) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("id", 0);
				nbt.setBoolean("valide", false);
				stack.setTagCompound(nbt);
			}
		}
	}
}
