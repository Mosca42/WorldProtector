package fr.mosca421.worldprotector.item;

import java.util.List;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.util.ExpandUtils;
import fr.mosca421.worldprotector.util.MessageUtils;
import fr.mosca421.worldprotector.util.PlayerUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import static fr.mosca421.worldprotector.util.MessageUtils.sendMessage;
import static fr.mosca421.worldprotector.util.PlayerUtils.*;

public class ItemRegionMarker extends Item {

	public ItemRegionMarker() {
		super(new Item.Properties()
				.maxStackSize(1)
				.group(WorldProtector.WORLD_PROTECTOR_TAB));
	}

	// nbt keys
	public static final String VALID = "valid";
	public static final String Y_DEFAULT_LOW = "y_low_default";
	public static final String Y_DEFAULT_HIGH = "y_high_default";
	public static final String CYCLE_POINT_ID = "id";
	public static final String X1 = "x1";
	public static final String Y1 = "y1";
	public static final String Z1 = "z1";
	public static final String X2 = "x2";
	public static final String Y2 = "y2";
	public static final String Z2 = "z2";

	public static final int FIRST = 0;
	public static final int SECOND = 1;

	private Runnable onFinishUseAction = () -> {};

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(Screen.hasShiftDown()) {
			tooltip.add(new TranslationTextComponent("help.regionmarker.detail.1"));
			tooltip.add(new TranslationTextComponent("help.regionmarker.detail.2"));
			tooltip.add(new TranslationTextComponent("help.regionmarker.optional.1").mergeStyle(TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent("help.regionmarker.detail.3"));
			tooltip.add(new TranslationTextComponent("help.regionmarker.detail.4").mergeStyle(TextFormatting.RED));

		} else {
			tooltip.add(new TranslationTextComponent("help.regionmarker.simple.1"));
			tooltip.add(new TranslationTextComponent("help.regionmarker.simple.2"));
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
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {

	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote) {
			ItemStack markStick = playerIn.getHeldItem(handIn);
			if (!playerIn.hasPermissionLevel(4) || !playerIn.isCreative()) {
				sendMessage(playerIn, new TranslationTextComponent("item.usage.permission")
						.mergeStyle(TextFormatting.RED));
				return ActionResult.resultFail(markStick);
			}
			if (isMainHand(handIn) && isValidRegion(markStick)) {
				int yLow = (int) markStick.getTag().getDouble(Y_DEFAULT_LOW);
				int yHigh = (int) markStick.getTag().getDouble(Y_DEFAULT_HIGH);
				this.onFinishUseAction = () -> ExpandUtils.expandVert(playerIn, markStick, yLow, yHigh);
				playerIn.setActiveHand(handIn);
				return super.onItemRightClick(worldIn, playerIn, handIn);
			}
			return ActionResult.resultFail(markStick);
		} else {
			return ActionResult.resultFail(playerIn.getHeldItem(handIn));
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
				switch (playerItemTag.getInt(CYCLE_POINT_ID)) {
					case FIRST:
						playerItemTag.putInt(X1, pos.getX());
						playerItemTag.putInt(Y1, pos.getY());
						playerItemTag.putInt(Z1, pos.getZ());
						playerItemTag.putInt(CYCLE_POINT_ID, SECOND);
						playerItemTag.putBoolean(VALID, false);
						MessageUtils.sendMessage(player, new StringTextComponent("Position 1 : x=" +
								player.getHeldItem(hand).getTag().getInt(X1) + ", y=" +
								player.getHeldItem(hand).getTag().getInt(Y1) + ", z=" +
								player.getHeldItem(hand).getTag().getInt(Z1))
								.mergeStyle(TextFormatting.DARK_RED));
						break;
					case SECOND:
						playerItemTag.putInt(X2, pos.getX());
						playerItemTag.putInt(Y2, pos.getY());
						playerItemTag.putInt(Z2, pos.getZ());
						playerItemTag.putInt(CYCLE_POINT_ID, 0);
						playerItemTag.putBoolean(VALID, true);
						MessageUtils.sendMessage(player, new StringTextComponent("Position 2 : x=" +
								player.getHeldItem(hand).getTag().getInt(X2) + ", y=" +
								player.getHeldItem(hand).getTag().getInt(Y2) + ", z=" +
								player.getHeldItem(hand).getTag().getInt(Z2))
								.mergeStyle(TextFormatting.DARK_RED));
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
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!worldIn.isRemote && !stack.hasTag()) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt(CYCLE_POINT_ID, 0);
			nbt.putBoolean(VALID, false);
			nbt.putDouble(Y_DEFAULT_LOW, 0);
			nbt.putDouble(Y_DEFAULT_HIGH, 255);
			stack.setTag(nbt);
		}
	}

	private boolean isValidRegion(ItemStack markStick){
		return markStick.getTag().getBoolean(VALID);
	}

	public void setDefaultYValues(ItemStack regionMarker, int yLow, int yHigh) {
		CompoundNBT itemTag = regionMarker.getTag();
		itemTag.putDouble(Y_DEFAULT_LOW, yLow);
		itemTag.putDouble(Y_DEFAULT_HIGH, yHigh);
	}
}
