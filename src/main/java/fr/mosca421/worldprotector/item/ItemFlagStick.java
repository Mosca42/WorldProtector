package fr.mosca421.worldprotector.item;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.RegionFlag;
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

    public static boolean isSneaking(){
		return Screen.hasShiftDown();
	}

	public static boolean inMainHand(Hand handIn){
		return handIn == Hand.MAIN_HAND;
	}

	private void switchMode(ItemStack itemStack){
		String mode = itemStack.getTag().getString("mode");
		String flag = itemStack.getTag().getString("selected_flag");
		switch(mode){
			case "add_flag":
				itemStack.getTag().putString("mode", "remove_flag");
				itemStack.setDisplayName(new StringTextComponent(TextFormatting.GREEN + "Flag Stick [" + flag + ", remove]"));
				break;
			case "remove_flag":
				itemStack.getTag().putString("mode", "add_flag");
				itemStack.setDisplayName(new StringTextComponent(TextFormatting.GREEN + "Flag Stick [" + flag + ", add]"));
				break;
			default:
				/* should not happen */
				break;
		}
	}

	private void cycleFlags(ItemStack flagStick){
		String selectedFlag = flags.get(flagIndex);
		setDisplayName(flagStick, selectedFlag);
		flagStick.getTag().putString("selected_flag", selectedFlag);
		flagIndex = (flagIndex + 1) % (flags.size());
	}


	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote) {
			ItemStack flagStick = playerIn.getHeldItem(handIn);
			if (!playerIn.hasPermissionLevel(4) || !playerIn.isCreative()) {
				sendMessage(playerIn, new StringTextComponent(TextFormatting.RED + "You have not the permission to use this item!"));
				return ActionResult.resultFail(flagStick);
			}
			if (isSneaking() && inMainHand(handIn)) {
				switchMode(flagStick);
				return ActionResult.resultSuccess(flagStick);
			}
			if (Screen.hasControlDown() && inMainHand(handIn)) {
				cycleFlags(flagStick);
				return ActionResult.resultSuccess(flagStick);
			}
			String flagMode = flagStick.getTag().getString("mode");
			String selectedRegion;
			if (inMainHand(handIn)) {
				ItemStack offHand = playerIn.getHeldItemOffhand();
				if (offHand.getItem() instanceof ItemRegionStick) {
					selectedRegion = offHand.getTag().getString("selected_region");
				} else {
					return ActionResult.resultFail(flagStick);
				}
			} else {
				// Offhand
				return ActionResult.resultFail(flagStick);
			}
			String selectedFag = playerIn.getHeldItem(handIn).getTag().getString("selected_flag");
			if (selectedFag.isEmpty() || selectedRegion.isEmpty()) {
				return ActionResult.resultFail(flagStick);
			}
			switch (flagMode) {
				case "add_flag":
					this.onFinishUseAction = () -> {
						RegionFlagUtils.addFlag(selectedRegion, playerIn, selectedFag);
					};
					break;
				case "remove_flag":
					this.onFinishUseAction = () -> {
						RegionFlagUtils.removeFlag(selectedRegion, playerIn, selectedFag);
					};
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

	private void setDisplayName(ItemStack flagStick, String flag){
		String mode = flagStick.getTag().getString("mode").equals("add_flag") ? "add" : "remove";
		flagStick.setDisplayName(new StringTextComponent(TextFormatting.GREEN + "Flag Stick [" + flag + ", " + mode + "]"));
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		return true; // false will damage entity
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
		if (!player.getEntityWorld().isRemote()) {


		}
	}

    public static String getModeString(ItemStack stack) {
        return "item.flagstick.mode." + stack.getTag().getString("mode");
    }

    @Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!flagsLoaded) {
			flags = new ArrayList<>(RegionFlag.getFlags());
			Collections.sort(flags);
			flagsLoaded = true;
		}
		if (!worldIn.isRemote && !stack.hasTag()) {
			CompoundNBT nbt = new CompoundNBT();
            nbt.putString("mode", "add_flag");
            nbt.putString("selected_flag", flags.get(0));
			stack.setTag(nbt);
		}

	}
}
