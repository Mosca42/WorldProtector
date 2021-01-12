package fr.mosca421.worldprotector.item;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.data.RegionSaver;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;
import static fr.mosca421.worldprotector.util.PlayerUtils.*;

public class ItemRegionStick extends Item {

	private boolean regionCacheInitialized;

	public ItemRegionStick() {
		super(new Properties()
				.maxStackSize(1)
				.group(WorldProtector.WORLD_PROTECTOR_TAB));
		this.cachedRegions = new ArrayList<>();
		this.regionCount = 0;
		this.regionIndex = 0;
		this.regionCacheInitialized = false; // NBT?
	}

	public static final String MODE_KEY = "mode";
	public static final String MODE_ADD = "add";
	public static final String MODE_REMOVE = "remove";
	public static final String REGION_KEY = "region";

	private List<String> cachedRegions;
	private int regionCount;
	private int regionIndex;

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent( TextFormatting.LIGHT_PURPLE +  "Select" + TextFormatting.RESET + " an existing region by " +
				TextFormatting.LIGHT_PURPLE + TextFormatting.ITALIC + "SHIFT" + TextFormatting.RESET + " right clicking."));
			tooltip.add(new TranslationTextComponent(TextFormatting.AQUA +  "Switch" + TextFormatting.RESET + " modes by " +
					TextFormatting.AQUA + TextFormatting.ITALIC + "CTRL" + TextFormatting.RESET + " right clicking."));
			tooltip.add(new TranslationTextComponent("Hit the player you want to add/remove (don't worry it wont hurt)."));
			tooltip.add(new TranslationTextComponent("For the secondary functionality keep the Region Stick in your offhand and read the Flag Stick tooltip."));
		} else {
			tooltip.add(new TranslationTextComponent("Use the Region Stick to simply add/remove player to/from a region."));
			tooltip.add(new StringTextComponent( "Hold " + TextFormatting.DARK_BLUE + TextFormatting.ITALIC + "SHIFT" + TextFormatting.RESET + " for more details."));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		if (!player.world.isRemote) {
			if ((entity instanceof PlayerEntity)) {
				// TODO:
			}
		}
		return true; // false will damage entity
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 25;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (!worldIn.isRemote) {
			// No functionality yet
		}
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote) {
			ItemStack regionStick = playerIn.getHeldItem(handIn);
			if (!playerIn.hasPermissionLevel(4) || !playerIn.isCreative()) {
				sendMessage(playerIn, new TranslationTextComponent("item.usage.permission")
						.mergeStyle(TextFormatting.RED));
				return ActionResult.resultFail(regionStick);
			}
			if (isSneaking() && isMainHand(handIn)) {
				switchMode(regionStick);
				return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
			}
			if (isHoldingCtrl() && isMainHand(handIn)) {
				if (cycleRegion(regionStick)) {
					return new ActionResult<>(ActionResultType.SUCCESS, regionStick);
				}
				sendMessage(playerIn, new StringTextComponent(TextFormatting.RED + "No regions defined yet!"));
				new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
			}
		}
		return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		// init region cache
		if (!regionCacheInitialized) {
			cachedRegions = RegionSaver.getRegions().stream().map(Region::getName).collect(Collectors.toList());
			Collections.sort(cachedRegions);
			regionCount = cachedRegions.size();
			regionCacheInitialized = true;
			regionIndex = 0;
		}
		// check if region data was changed and update cache
		if (regionCount != RegionSaver.getRegions().size()) {
			cachedRegions = RegionSaver.getRegions().stream().map(Region::getName).collect(Collectors.toList());
			regionCount = cachedRegions.size();
			regionIndex = Math.min(regionIndex, regionCount - 1);
		}
		// init nbt tag
		if (!worldIn.isRemote && !stack.hasTag()) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString(MODE_KEY, MODE_ADD);
			if (regionCount > 0) {
				String region = cachedRegions.get(0);
				nbt.putString(REGION_KEY, region);
			}
			stack.setTag(nbt);
		}
	}

	private void setDisplayName(ItemStack regionStick, String region, String mode){
		regionStick.setDisplayName(new StringTextComponent(TextFormatting.AQUA + "Region Stick [" + region + ", " + mode + "]"));
	}

	public String getMode(ItemStack regionStick){
		return regionStick.getTag().getString(MODE_KEY);
	}

	private void setMode(ItemStack regionStick, String mode){
		regionStick.getTag().putString(MODE_KEY, mode);
	}

	public String getRegion(ItemStack regionStick) {
		return regionStick.getTag().getString(REGION_KEY);
	}

	private void setRegion(ItemStack regionStick, String region){
		regionStick.getTag().putString(REGION_KEY, region);
	}

	private boolean cycleRegion(ItemStack regionStick){
		if (regionCount > 0) {
			String selectedRegion = cachedRegions.get(regionIndex);
			String mode = getMode(regionStick);
			setDisplayName(regionStick, selectedRegion, mode);
			setRegion(regionStick, selectedRegion);
			regionIndex = (regionIndex + 1) % (regionCount);
			return true;
		} else {
			return false;
		}
	}

	private void switchMode(ItemStack regionStick) {
		String mode = getMode(regionStick);
		String region = getRegion(regionStick);
		switch(mode){
			case MODE_ADD:
				setMode(regionStick, MODE_REMOVE);
				setDisplayName(regionStick, region, MODE_REMOVE);
				break;
			case MODE_REMOVE:
				setMode(regionStick, MODE_ADD);
				setDisplayName(regionStick, region, MODE_ADD);
				break;
			default:
				/* should not happen */
				break;
		}
	}
}
