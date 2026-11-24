package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MinecraftUseItemMixin {
    @Inject(method = "itemUsed", at = @At("HEAD"), cancellable = true)
    private void everfrost$noItemUseDip(InteractionHand hand, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getItemInHand(hand).getItem() instanceof FrozenMalletItem
                && !player.isShiftKeyDown()) {
            ci.cancel();
        }
    }
}
