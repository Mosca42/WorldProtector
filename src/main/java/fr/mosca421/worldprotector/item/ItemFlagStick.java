package fr.mosca421.worldprotector.item;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.PlayerUtils;
import fr.mosca421.worldprotector.util.RegionFlagUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

public class ItemFlagStick extends Item {

	public ItemFlagStick() {
		super(new Properties()
				.maxStackSize(1)
				.group(WorldProtector.WORLD_PROTECTOR_TAB));
		this.flags = new ArrayList<>();
		this.flagIndex = 0;
		this.flagsLoaded = false;
	}

	public static final String MODE_KEY = "mode";
	public static final String MODE_ADD = "add";
	public static final String MODE_REMOVE = "remove";
	public static final String FLAG_KEY = "flag";

	private List<String> flags;
	private int flagIndex;
	private boolean flagsLoaded;
	private Runnable onFinishUseAction = () -> {};

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent(TextFormatting.LIGHT_PURPLE +  "Select" + TextFormatting.RESET + " the flag by " +
					TextFormatting.LIGHT_PURPLE + TextFormatting.ITALIC + "SHIFT" + TextFormatting.RESET + " right clicking."));
			tooltip.add(new TranslationTextComponent(TextFormatting.AQUA +  "Switch" + TextFormatting.RESET + " modes by " +
					TextFormatting.AQUA + TextFormatting.ITALIC + "CTRL" + TextFormatting.RESET + " right clicking."));
			tooltip.add(new TranslationTextComponent("Hold down the right mouse button to add/remove the selected flag to/from the region."));
			tooltip.add(new TranslationTextComponent(TextFormatting.RED + "Keep the Region Stick with the selected region in your off hand!"));
		} else {
			tooltip.add(new TranslationTextComponent("Use the Flag Stick to simply add/remove flags to/from a region."));
			tooltip.add(new TranslationTextComponent("Hold the " + TextFormatting.RED + "Region Stick" + TextFormatting.RESET + " in your " + TextFormatting.RED + "offhand" + TextFormatting.RESET + "!"));
			tooltip.add(new StringTextComponent( "Hold " + TextFormatting.DARK_BLUE + TextFormatting.ITALIC + "SHIFT" + TextFormatting.RESET + " for more details."));
		}
	}

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
	    if (!worldIn.isRemote) {
			this.onFinishUseAction.run();
		}
        return stack;
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote) {
			ItemStack flagStick = playerIn.getHeldItem(handIn);
			// check player permission
			if (!playerIn.hasPermissionLevel(4) || !playerIn.isCreative()) {
				sendMessage(playerIn, new TranslationTextComponent("item.usage.permission")
						.mergeStyle(TextFormatting.RED));
				return ActionResult.resultFail(flagStick);
			}
			boolean isMainHand = PlayerUtils.isMainHand(handIn);
			// SHIFT -> switch mode
			if (PlayerUtils.isSneaking() && isMainHand) {
				switchMode(flagStick);
				return ActionResult.resultSuccess(flagStick);
			}
			// CTRL -> cycle flags
			if (PlayerUtils.isHoldingCtrl() && isMainHand) {
				cycleFlags(flagStick);
				return ActionResult.resultSuccess(flagStick);
			}
			// check for region stick and add/remove flags
			String flagMode = getMode(flagStick);
			String selectedRegion;
			if (isMainHand) {
				ItemStack offHand = playerIn.getHeldItemOffhand();
				if (offHand.getItem() instanceof ItemRegionStick) {
					selectedRegion = offHand.getTag().getString(ItemRegionStick.REGION_KEY);
				} else {
					return ActionResult.resultFail(flagStick);
				}
			} else {
				// Offhand
				return ActionResult.resultFail(flagStick);
			}
			String selectedFag = getSelectedFlag(playerIn.getHeldItem(handIn));
			if (selectedFag.isEmpty() || selectedRegion.isEmpty()) {
				return ActionResult.resultFail(flagStick);
			}
			switch (flagMode) {
				case MODE_ADD:
					this.onFinishUseAction = () -> RegionFlagUtils.addFlag(selectedRegion, playerIn, selectedFag);
					break;
				case "remove":
					this.onFinishUseAction = () -> RegionFlagUtils.removeFlag(selectedRegion, playerIn, selectedFag);
					break;
				default:
					break;
			}
			playerIn.setActiveHand(handIn);
			return super.onItemRightClick(worldIn, playerIn, handIn);
		} else {
			return ActionResult.resultFail(playerIn.getHeldItem(handIn));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		// No functionality yet
		return true; // false will damage entity
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote) {
			if (!flagsLoaded) {
				flags = new ArrayList<>(RegionFlag.getFlags());
				Collections.sort(flags);
				flagsLoaded = true;
			}
			// ensure flag stick has a nbt tag and is initialized as needed
			if (!stack.hasTag()){
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString(MODE_KEY, MODE_ADD);
				nbt.putString(FLAG_KEY, flags.get(0));
				stack.setTag(nbt);
			}
		}

	}

	private void switchMode(ItemStack flagStick){
		String mode = getMode(flagStick);
		String flag = getSelectedFlag(flagStick);
		switch(mode){
			case MODE_ADD:
				setMode(flagStick, MODE_REMOVE);
				setDisplayName(flagStick, flag, MODE_REMOVE);
				break;
			case MODE_REMOVE:
				setMode(flagStick, MODE_ADD);
				setDisplayName(flagStick, flag, MODE_ADD);
				break;
			default:
				/* should not happen */
				break;
		}
	}

	private void cycleFlags(ItemStack flagStick){
		String selectedFlag = flags.get(flagIndex);
		setDisplayName(flagStick, selectedFlag, getMode(flagStick));
		flagStick.getTag().putString(FLAG_KEY, selectedFlag);
		flagIndex = (flagIndex + 1) % (flags.size());
	}

	private String getMode(ItemStack flagStick) {
		return flagStick.getTag().getString(MODE_KEY);
	}

	private void setMode(ItemStack flagStick, String mode){
		flagStick.getTag().putString(MODE_KEY, mode);
	}

	private String getSelectedFlag(ItemStack flagStick){
		return flagStick.getTag().getString(FLAG_KEY);
	}

	private void setDisplayName(ItemStack flagStick, String flag, String mode){
		flagStick.setDisplayName(new StringTextComponent(TextFormatting.GREEN + "Flag Stick [" + flag + ", " + mode + "]"));
	}
}
