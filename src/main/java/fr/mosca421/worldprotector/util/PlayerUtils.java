package fr.mosca421.worldprotector.util;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Hand;

public final class PlayerUtils {

    public static boolean isSneaking(){
        return Screen.hasShiftDown();
    }

    public static boolean isHoldingCtrl(){
        return Screen.hasControlDown();
    }

    public static boolean isMainHand(Hand handIn){
        return Hand.MAIN_HAND == handIn;
    }
}
