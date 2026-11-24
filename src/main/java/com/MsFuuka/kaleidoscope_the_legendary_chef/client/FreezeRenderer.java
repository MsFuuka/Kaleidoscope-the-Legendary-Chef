package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID, value = Dist.CLIENT)
public final class FreezeRenderer {
    private static final ThreadLocal<PoseStack> STACK = ThreadLocal.withInitial(() -> null);

    private FreezeRenderer() {
    }

    @SubscribeEvent
    public static void onPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get())
                || PrimalIceNecklaceItem.isWorn(entity)) {
            return;
        }
        float time = entity.tickCount + event.getPartialTick();
        PoseStack stack = event.getPoseStack();
        PoseStack previous = STACK.get();
        if (previous != null) {
            previous.popPose();
            STACK.set(null);
        }
        stack.pushPose();
        STACK.set(stack);
        stack.translate(Math.sin(time * 1.7F) * 0.012D, Math.sin(time * 2.9F) * 0.008D,
                Math.cos(time * 2.1F) * 0.012D);
    }

    @SubscribeEvent
    public static void onPost(RenderLivingEvent.Post<?, ?> event) {
        PoseStack stack = STACK.get();
        if (stack != null) {
            stack.popPose();
            STACK.set(null);
        }
    }
}
