package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID, value = Dist.CLIENT)
public final class FreezeHeartOverlay {
    private static final ResourceLocation ICONS =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/icons.png");

    private FreezeHeartOverlay() {
    }

    @SubscribeEvent
    public static void onRender(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || !minecraft.gameMode.canHurtPlayer()
                || player.hasEffect(MobEffects.POISON) || player.hasEffect(MobEffects.WITHER)) {
            return;
        }
        int frozen = player.getTicksFrozen();
        if (frozen <= 0) {
            return;
        }
        float byTicks = Mth.clamp((float) frozen / player.getTicksRequiredToFreeze(), 0.0F, 1.0F);
        float alpha = byTicks;
        if (alpha <= 0.0F) {
            return;
        }
        int hearts = Mth.ceil(player.getHealth() / 2.0F);
        boolean hardcore = player.level().getLevelData().isHardcore();
        int v = hardcore ? 45 : 0;
        int left = event.getWindow().getGuiScaledWidth() / 2 - 91;
        int top = event.getWindow().getGuiScaledHeight() - 39;
        var graphics = event.getGuiGraphics();
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        for (int i = hearts - 1; i >= 0; i--) {
            int x = left + (i % 10) * 8;
            int y = top - (i / 10) * 10 + HeartJitterCapture.jitterFor(i);
            graphics.blit(ICONS, x, y, 178, v, 9, 9);
        }
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
