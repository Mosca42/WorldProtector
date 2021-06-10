package fr.mosca421.worldprotector.item;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.RegionFlag;
import fr.mosca421.worldprotector.util.RegionFlagUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

import static fr.mosca421.worldprotector.util.MessageUtils.sendStatusMessage;

public class ItemFlagStick extends Item {

	public ItemFlagStick() {
		super(new Properties()
				.maxStackSize(1)
				.group(WorldProtector.WORLD_PROTECTOR_TAB));
	}

	private static final List<String> flags;

	// nbt keys
	public static final String FLAG_IDX = "flag_idx";
	public static final String FLAG = "flag";
	public static final String MODE = "mode";

	public static final String MODE_ADD = "add";
	public static final String MODE_REMOVE = "remove";

	static {
		// init flag list
		flags = RegionFlag.getFlags();
		Collections.sort(flags);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent("help.tooltip.flag-stick.detail.1"));
			tooltip.add(new TranslationTextComponent("help.tooltip.flag-stick.detail.2"));
			tooltip.add(new TranslationTextComponent("help.tooltip.flag-stick.detail.3"));
			tooltip.add(new TranslationTextComponent("help.tooltip.flag-stick.detail.4")
					.mergeStyle(TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent("help.tooltip.flag-stick.detail.5")
					.mergeStyle(TextFormatting.RED));
		} else {
			tooltip.add(new TranslationTextComponent("help.tooltip.flag-stick.simple.1"));
			tooltip.add(new TranslationTextComponent("help.tooltip.flag-stick.simple.2"));
			tooltip.add(new TranslationTextComponent("help.tooltip.details.shift")
					.mergeStyle(TextFormatting.DARK_BLUE)
					.mergeStyle(TextFormatting.ITALIC));
		}
	}

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
	    if (!worldIn.isRemote && entityLiving instanceof ServerPlayerEntity) {
	    	ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
	    	String selectedRegion = player.getHeldItemOffhand().getTag().getString(ItemRegionStick.REGION);
	    	String selectedFlag = stack.getTag().getString(FLAG);
			int finishAction = stack.getTag().getInt("finish_action");
			switch (finishAction) {
				case 1:
					RegionFlagUtils.addAllFlags(selectedRegion, player);
					break;
				case 2:
					RegionFlagUtils.addFlag(selectedRegion, player, selectedFlag);
					break;
				case 3:
					RegionFlagUtils.removeAllFlags(selectedRegion, player);
					break;
				case 4:
					RegionFlagUtils.removeFlag(selectedRegion, player, selectedFlag);
					break;
				default:
					WorldProtector.LOGGER.error("Oh oh");
					break;
			}
			player.getCooldownTracker().setCooldown(this, 20);
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
				sendStatusMessage(playerIn, new TranslationTextComponent("item.usage.permission")
						.mergeStyle(TextFormatting.RED));
				return ActionResult.resultFail(flagStick);
			}
			ItemStack offHand = playerIn.getHeldItemOffhand();
			ItemStack mainHand = playerIn.getHeldItemMainhand();
			if (offHand.getItem() instanceof ItemRegionStick && mainHand.getItem() instanceof ItemFlagStick) {
				String selectedRegion = offHand.getTag().getString(ItemRegionStick.REGION);
				String selectedFag = getSelectedFlag(playerIn.getHeldItem(handIn));
				String flagMode = getMode(flagStick);
				if (selectedFag.isEmpty() || selectedRegion.isEmpty()) {
					return ActionResult.resultFail(flagStick);
				}
				boolean allFlagsSelected = selectedFag.equals(RegionFlag.ALL.toString());
				switch (flagMode) {
					case MODE_ADD:
						if (allFlagsSelected) {
							flagStick.getTag().putInt("finish_action", 1);
						} else {
							flagStick.getTag().putInt("finish_action", 2);
						}
						break;
					case MODE_REMOVE:
						if (allFlagsSelected) {
							flagStick.getTag().putInt("finish_action", 3);
						} else {
							flagStick.getTag().putInt("finish_action", 4);
						}
						break;
					default:
						break;
				}
				playerIn.setActiveHand(handIn);
				return super.onItemRightClick(worldIn, playerIn, handIn);
			} else {
				if (handIn == Hand.MAIN_HAND) {
					if (playerIn.isSneaking()) {
						switchMode(flagStick);
						return ActionResult.resultSuccess(flagStick);
					} else {
						cycleFlags(flagStick);
						return ActionResult.resultSuccess(flagStick);
					}
				}
			}
			// check for region stick and add/remove flags
		} else {
			return ActionResult.resultFail(playerIn.getHeldItem(handIn));
		}
		return null;
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
						sendStatusMessage(player, "message.flags.container.noflags");
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
						sendStatusMessage(player, "message.flags.container.noflags");
						return ActionResultType.FAIL;
					}
					List<String> validFlags = nameTags.stream().filter(RegionFlag::contains).collect(Collectors.toList());
					if (validFlags.isEmpty()) {
						sendStatusMessage(player, "message.flags.container.novalidflags");
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
			// ensure flag stick has a nbt tag and is initialized as needed
			if (!stack.hasTag()) {
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString(MODE, MODE_ADD);
				nbt.putString(FLAG, RegionFlag.ALL.toString());
				nbt.putInt(FLAG_IDX, 0);
				nbt.putInt("finish_action", 0);
				stack.setTag(nbt);
				setDisplayName(stack, RegionFlag.ALL, MODE_ADD);
			} else {
				int flagIdx = stack.getTag().getInt(FLAG_IDX);
				String flag = flags.get(flagIdx);
				String mode = stack.getTag().getString(MODE);
				setDisplayName(stack, flag, mode);
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
