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
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

public class ItemFlagStick extends Item {

	public ItemFlagStick() {
		super(new Properties()
				.maxStackSize(1)
				.group(WorldProtector.WORLD_PROTECTOR_TAB));
		this.flags = new ArrayList<>();
		this.flagsLoaded = false;
	}

	// nbt keys
	public static final String FLAG_IDX = "flag_idx";
	public static final String FLAG = "flag";
	public static final String MODE = "mode";

	public static final String MODE_ADD = "add";
	public static final String MODE_REMOVE = "remove";

	private List<String> flags;
	private boolean flagsLoaded;
	private Runnable onFinishUseAction = () -> {}; // TODO: check -> could cause problems if 2 sticks are used

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent(TextFormatting.LIGHT_PURPLE +  "Select" + TextFormatting.RESET + " the flag by " +
					TextFormatting.LIGHT_PURPLE + TextFormatting.ITALIC + "CTRL" + TextFormatting.RESET + " right clicking."));
			tooltip.add(new TranslationTextComponent(TextFormatting.AQUA +  "Switch" + TextFormatting.RESET + " modes by " +
					TextFormatting.AQUA + TextFormatting.ITALIC + "SHIFT" + TextFormatting.RESET + " right clicking."));
			tooltip.add(new TranslationTextComponent("Hold down the right mouse button to add/remove the selected flag to/from the region."));
			tooltip.add(new TranslationTextComponent("Alternatively: Shift-right click on a container with name tags, named after flags, to add/remove all the corresponding flags to/from the region!"));
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
			((PlayerEntity) entityLiving).getCooldownTracker().setCooldown(this, 20);
		}
        return stack;
    }

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
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
			if (PlayerUtils.isHoldingCtrl() && isMainHand) {
				switchMode(flagStick);
				return ActionResult.resultSuccess(flagStick);
			}
			// CTRL -> cycle flags
			if (PlayerUtils.isSneaking() && isMainHand) {
				cycleFlags(flagStick);
				return ActionResult.resultSuccess(flagStick);
			}
			// check for region stick and add/remove flags
			String flagMode = getMode(flagStick);
			String selectedRegion;
			if (isMainHand) {
				ItemStack offHand = playerIn.getHeldItemOffhand();
				if (offHand.getItem() instanceof ItemRegionStick) {
					selectedRegion = offHand.getTag().getString(ItemRegionStick.REGION);
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
			boolean allFlagsSelected = selectedFag.equals(RegionFlag.ALL.toString());
			switch (flagMode) {
				case MODE_ADD:
					if (allFlagsSelected){
						this.onFinishUseAction = () -> RegionFlagUtils.addAllFlags(selectedRegion, playerIn);
					} else {
						this.onFinishUseAction = () -> RegionFlagUtils.addFlag(selectedRegion, playerIn, selectedFag);
					}
					break;
				case MODE_REMOVE:
					if (allFlagsSelected){
						this.onFinishUseAction = () -> RegionFlagUtils.removeAllFlags(selectedRegion, playerIn);
					} else {
						this.onFinishUseAction = () -> RegionFlagUtils.removeFlag(selectedRegion, playerIn, selectedFag);
					}
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
	public ActionResultType onItemUse(ItemUseContext context) {
		if (!context.getWorld().isRemote) {
			TileEntity target = context.getWorld().getTileEntity(context.getPos());
			PlayerEntity player = context.getPlayer();
			ItemStack mainHand = player.getHeldItemMainhand();
			ItemStack offHand = player.getHeldItemOffhand();
			if (offHand.getItem() instanceof ItemRegionStick && mainHand.getItem() instanceof ItemFlagStick) {
				ItemRegionStick regionStick = (ItemRegionStick) offHand.getItem();
				ItemFlagStick flagStick = (ItemFlagStick) mainHand.getItem();
				String flagMode = flagStick.getMode(mainHand);
				String selectedRegion = regionStick.getRegion(offHand);
				if (target instanceof LockableLootTileEntity) {
					LockableLootTileEntity container = (LockableLootTileEntity) target;
					if (container.isEmpty()) {
						sendMessage(player,  "message.flags.container.noflags");
						return ActionResultType.FAIL;
					}
					List<String> nameTags = new ArrayList<>();
					for (int i = 0; i < container.getSizeInventory(); i++) {
						ItemStack stack = container.getStackInSlot(i);
						if (stack.getItem() instanceof NameTagItem) {
							nameTags.add(stack.getDisplayName().getString());
						}
					}
					if (nameTags.isEmpty()) {
						sendMessage(player,  "message.flags.container.noflags");
						return ActionResultType.FAIL;
					}
					List<String> validFlags = nameTags.stream().filter(RegionFlag::contains).collect(Collectors.toList());
					if (validFlags.isEmpty()) {
						sendMessage(player,  "message.flags.container.novalidflags");
						return ActionResultType.FAIL;
					}
					switch (flagMode) {
						case MODE_ADD:
							RegionFlagUtils.addFlags(selectedRegion, player, validFlags);
							break;
						case MODE_REMOVE:
							RegionFlagUtils.removeFlags(selectedRegion, player, validFlags);
							break;
						default:
							/* should never happen */
							return ActionResultType.FAIL;
					}
					return ActionResultType.SUCCESS;
				}
			} else {
				return ActionResultType.FAIL;
			}
		}
		return ActionResultType.FAIL;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		return true; // false will damage entity
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote) {
			if (!flagsLoaded) {
				flags = RegionFlag.getFlags();
				Collections.sort(flags);
				flagsLoaded = true;
			}
			// ensure flag stick has a nbt tag and is initialized as needed
			if (!stack.hasTag()){
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString(MODE, MODE_ADD);
				nbt.putString(FLAG, RegionFlag.ALL.toString());
				nbt.putInt(FLAG_IDX, 0);
				setDisplayName(stack, RegionFlag.ALL, MODE_ADD);
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
		int flagIndex = flagStick.getTag().getInt(FLAG_IDX);
		// get flag and set display name
		String selectedFlag = flags.get(flagIndex);
		setDisplayName(flagStick, selectedFlag, getMode(flagStick));
		// write flag nbt
		flagStick.getTag().putString(FLAG, selectedFlag);
		// increase flag index and write nbt
		flagIndex = (flagIndex + 1) % (flags.size());
		flagStick.getTag().putInt(FLAG_IDX, flagIndex);
	}

	private String getMode(ItemStack flagStick) {
		return flagStick.getTag().getString(MODE);
	}

	private void setMode(ItemStack flagStick, String mode){
		flagStick.getTag().putString(MODE, mode);
	}

	private String getSelectedFlag(ItemStack flagStick){
		return flagStick.getTag().getString(FLAG);
	}

	private void setDisplayName(ItemStack flagStick, String flag, String mode){
		flagStick.setDisplayName(new StringTextComponent(TextFormatting.GREEN + "Flag Stick [" + flag + ", " + mode + "]"));
	}

	private void setDisplayName(ItemStack flagStick, RegionFlag flag, String mode){
		setDisplayName(flagStick, flag.toString(), mode);
	}
}
