package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.MsFuuka.kaleidoscope_the_legendary_chef.client.HeartJitterCapture;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiHeartJitterMixin {
    @Inject(method = "renderHearts", at = @At("HEAD"))
    private void everfrost$captureTotal(GuiGraphics graphics, Player player, int x, int y, int step, int offset,
                                        float health, int displayHealth, int p9, int absorption,
                                        boolean hardcore, CallbackInfo ci) {
        HeartJitterCapture.begin(Mth.ceil(health / 2.0F) + Mth.ceil(absorption / 2.0F));
    }

    @Redirect(method = "renderHearts", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int everfrost$captureJitter(RandomSource random, int bound) {
        int value = random.nextInt(bound);
        HeartJitterCapture.push(value);
        return value;
    }
}
