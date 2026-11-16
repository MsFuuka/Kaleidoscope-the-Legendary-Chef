package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FrozenMalletInputHandler {
    private static final long AIM_HOLD_MILLIS = 150L;
    private static long pressMillis = -1L;

    private FrozenMalletInputHandler() {
    }

    public static boolean canShowAim() {
        return pressMillis >= 0L && System.currentTimeMillis() - pressMillis >= AIM_HOLD_MILLIS;
    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null || mc.player.isShiftKeyDown()) {
            return;
        }
        InteractionHand hand = event.getHand();
        ItemStack stack = mc.player.getItemInHand(hand);
        if (!(stack.getItem() instanceof FrozenMalletItem)) {
            return;
        }
        event.setCanceled(true);
        event.setSwingHand(false);
        mc.gameMode.useItem(mc.player, hand);
        pressMillis = System.currentTimeMillis();
    }
}
