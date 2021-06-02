package fr.mosca421.worldprotector.item;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.IRegion;
import fr.mosca421.worldprotector.data.RegionManager;
import fr.mosca421.worldprotector.util.RegionPlayerUtils;
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

public class ItemRegionStick extends Item {


	public ItemRegionStick() {
		super(new Properties()
				.maxStackSize(1)
				.group(WorldProtector.WORLD_PROTECTOR_TAB));
	}

	// nbt keys
	public static final String REGION_IDX = "region_idx";
	public static final String MODE = "mode";
	public static final String REGION = "region";

	public static final String MODE_ADD = "add";
	public static final String MODE_REMOVE = "remove";

	private static List<String> cachedRegions;
	private static int regionCount;

	// TODO: rework with sync in place
	static {
		// init region cache
		WorldProtector.LOGGER.debug("Region Stick cache initialized");
		cachedRegions = RegionManager.get().getAllRegions().stream()
				.map(IRegion::getName)
				.collect(Collectors.toList());
		Collections.sort(cachedRegions);
		regionCount = cachedRegions.size();
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent( "Select an existing region by right clicking."));
			tooltip.add(new TranslationTextComponent(TextFormatting.AQUA +  "Switch" + TextFormatting.RESET + " modes by " +
					TextFormatting.AQUA + TextFormatting.ITALIC + "SHIFT" + TextFormatting.RESET + " right clicking."));
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
				String mode = stack.getTag().getString(MODE);
				PlayerEntity hitPlayer = (PlayerEntity) entity;
				String regionName = stack.getTag().getString(REGION);
				switch (mode) {
					case MODE_ADD:
						RegionPlayerUtils.addPlayer(regionName, player, hitPlayer);
						break;
					case MODE_REMOVE:
						RegionPlayerUtils.removePlayer(regionName, player, hitPlayer);
						break;
					default:
						/* should not happen */
						break;
				}
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
			if (playerIn.getActiveHand() == Hand.MAIN_HAND){
				if (playerIn.isSneaking()) {
					switchMode(regionStick);
					return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				} else {
					if (cycleRegion(regionStick)) {
						return new ActionResult<>(ActionResultType.SUCCESS, regionStick);
					}
					sendMessage(playerIn, new StringTextComponent(TextFormatting.RED + "No regions defined yet!"));
					new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
				}
			}
		}
		return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote) {
			// init nbt tag
			if (!stack.hasTag()) {
				WorldProtector.LOGGER.info("Region Stick nbt initialized");
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString(MODE, MODE_ADD);
				nbt.putInt(REGION_IDX, 0);
				if (regionCount > 0) {
					String region = cachedRegions.get(0);
					nbt.putString(REGION, region);
				}
				stack.setTag(nbt);
			}
		} else {
			// check if region data was changed and update cache
			Collection<IRegion> regions = RegionManager.get().getAllRegions();
			if (regionCount != regions.size()) {
				WorldProtector.LOGGER.info("Region Stick cache updated");
				cachedRegions = regions.stream()
						.map(IRegion::getName)
						.collect(Collectors.toList());
				regionCount = cachedRegions.size();
				int regionIndex = stack.getTag().getInt(REGION_IDX);
				regionIndex = Math.max(0, Math.min(regionIndex, regionCount - 1));
				stack.getTag().putInt(REGION_IDX, regionIndex);
			}
		}
	}

	private void setDisplayName(ItemStack regionStick, String region, String mode){
		regionStick.setDisplayName(new StringTextComponent(TextFormatting.AQUA + "Region Stick [" + region + ", " + mode + "]"));
	}

	public String getMode(ItemStack regionStick){
		return regionStick.getTag().getString(MODE);
	}

	private void setMode(ItemStack regionStick, String mode){
		regionStick.getTag().putString(MODE, mode);
	}

	public String getRegion(ItemStack regionStick) {
		return regionStick.getTag().getString(REGION);
	}

	private void setRegion(ItemStack regionStick, String region){
		regionStick.getTag().putString(REGION, region);
	}

	private boolean cycleRegion(ItemStack regionStick){
		if (regionCount > 0) {
			int regionIndex = regionStick.getTag().getInt(REGION_IDX);
			// get region and set display name
			String selectedRegion = cachedRegions.get(regionIndex);
			setDisplayName(regionStick, selectedRegion, getMode(regionStick));
			// write region nbt
			setRegion(regionStick, selectedRegion);
			// increase region index and write nbt
			regionIndex = (regionIndex + 1) % (regionCount);
			regionStick.getTag().putInt(REGION_IDX, regionIndex);
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
