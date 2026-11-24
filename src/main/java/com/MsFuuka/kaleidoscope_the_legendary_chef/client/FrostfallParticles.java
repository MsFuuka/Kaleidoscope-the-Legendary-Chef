package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.effect.BitterColdState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID, value = Dist.CLIENT)
public final class FrostfallParticles {
    private FrostfallParticles() {
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!BitterColdState.isFrostfall(entity)) {
            return;
        }
        int steps = BitterColdState.stepsOf(entity);
        if (steps <= 0 || entity.level().random.nextInt(30) >= steps) {
            return;
        }
        double w = entity.getBbWidth();
        double h = entity.getBbHeight();
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5D) * w;
        double y = entity.getY() + entity.level().random.nextDouble() * h;
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5D) * w;
        entity.level().addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.0D, -0.02D, 0.0D);
    }
}
