package fr.mosca421.worldprotector.item;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.core.Region;
import fr.mosca421.worldprotector.core.RegionSaver;
import fr.mosca421.worldprotector.util.MessageUtils;
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

import static fr.mosca421.worldprotector.item.ItemFlagStick.inMainHand;
import static fr.mosca421.worldprotector.item.ItemFlagStick.isSneaking;
import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;

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
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (!worldIn.isRemote) {
			WorldProtector.LOGGER.debug("Finished");
		}
		return stack;
	}

	private void setDisplayName(ItemStack regionStick, String region){
		String mode = regionStick.getTag().getString("mode").equals("add_player") ? "add" : "remove";
		regionStick.setDisplayName(new StringTextComponent(TextFormatting.AQUA + "Region Stick [" + region + ", " + mode + "]"));
	}

	private boolean cycleRegion(ItemStack regionStick){
		if (regionCount > 0) {
			String selectedRegion = cachedRegions.get(regionIndex);
			setDisplayName(regionStick, selectedRegion);
			regionStick.getTag().putString("selected_region", selectedRegion);
			regionIndex = (regionIndex + 1) % (regionCount);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote) {
			ItemStack regionStick = playerIn.getHeldItem(handIn);
			if (!playerIn.hasPermissionLevel(4) || !playerIn.isCreative()) {
				MessageUtils.sendMessage(playerIn, new StringTextComponent(TextFormatting.RED + "You have not the permission to use this item!"));
				return ActionResult.resultFail(regionStick);
			}
			if (isSneaking() && inMainHand(handIn)) {
				switchMode(regionStick);
				return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
			}
			if (Screen.hasControlDown() && inMainHand(handIn)) {
				if (cycleRegion(regionStick)) {
					return new ActionResult<>(ActionResultType.SUCCESS, regionStick);
				}
				MessageUtils.sendMessage(playerIn, new StringTextComponent(TextFormatting.RED + "No regions defined yet!"));
				new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
			}
		}
		return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		if (!player.world.isRemote) {
			if (!(entity instanceof PlayerEntity)) {
				sendMessage(player, new StringTextComponent("This is not a player you dum dum."));
			}
		}
		return true; // false will damage entity
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 25;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if(!worldIn.isRemote) {
			WorldProtector.LOGGER.debug(String.join(", ", cachedRegions));
		}
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	public static String getModeString(ItemStack stack) {
		return "item.regionstick.mode." + stack.getTag().getString("mode");
	}

	private void switchMode(ItemStack itemStack) {
		String mode = itemStack.getTag().getString("mode");
		String region = itemStack.getTag().getString("selected_region");
		switch(mode){
			case "add_player":
				itemStack.getTag().putString("mode", "remove_player");
				itemStack.setDisplayName(new StringTextComponent(TextFormatting.AQUA + "Region Stick [" + region + ", remove]"));
				break;
			case "remove_player":
				itemStack.getTag().putString("mode", "add_player");
				itemStack.setDisplayName(new StringTextComponent(TextFormatting.AQUA + "Region Stick [" + region + ", add]"));
				break;
			default:
				/* should not happen */
				break;
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!regionCacheInitialized) {
			cachedRegions = RegionSaver.getRegions().stream().map(Region::getName).collect(Collectors.toList());
			Collections.sort(cachedRegions);
			regionCount = cachedRegions.size();
			regionCacheInitialized = true;
		}
		if (regionCount != 0 && regionCount != RegionSaver.getRegions().size()) {
			cachedRegions = RegionSaver.getRegions().stream().map(Region::getName).collect(Collectors.toList());
			regionCount = cachedRegions.size();
		}
		if (!worldIn.isRemote && !stack.hasTag()) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("mode", "add_player");
			stack.setTag(nbt);
			if (regionCount > 0) {
				String region = cachedRegions.get(0);
				nbt.putString("selected_region", region);
			}
		}
	}
}
