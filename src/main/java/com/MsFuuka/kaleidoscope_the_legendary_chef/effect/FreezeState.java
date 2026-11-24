package com.MsFuuka.kaleidoscope_the_legendary_chef.effect;

import java.util.WeakHashMap;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class FreezeState {
    public static final int DURATION = 300;
    private static final int STEP_TICKS = 10;
    private static final int MAX_STEPS = 14;
    private static final int HUD_STEP = 10;

    private static final WeakHashMap<LivingEntity, Integer> STEP = new WeakHashMap<>();
    private static final WeakHashMap<LivingEntity, Long> LAST_STEP = new WeakHashMap<>();

    private FreezeState() {
    }

    public static void apply(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get(),
                DURATION, 0, false, true));
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        long now = entity.level().getGameTime();
        Integer steps = STEP.get(entity);
        if (PrimalIceNecklaceItem.isWorn(entity)) {
            entity.setTicksFrozen(0);
            return;
        }
        if (entity.hasEffect(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get())) {
            if (steps == null) {
                steps = Mth.clamp(entity.getTicksFrozen() / HUD_STEP, 0, MAX_STEPS);
                STEP.put(entity, steps);
                LAST_STEP.put(entity, now);
            }
            long last = LAST_STEP.getOrDefault(entity, now);
            if (steps < MAX_STEPS && now - last >= STEP_TICKS) {
                steps++;
                STEP.put(entity, steps);
                LAST_STEP.put(entity, now);
            }
            entity.setTicksFrozen(hudFor(steps));
        } else if (steps != null) {
            long last = LAST_STEP.getOrDefault(entity, now);
            if (now - last >= STEP_TICKS) {
                steps--;
                LAST_STEP.put(entity, now);
                if (steps < 0) {
                    STEP.remove(entity);
                    LAST_STEP.remove(entity);
                    entity.setTicksFrozen(0);
                    return;
                }
                STEP.put(entity, steps);
            }
            entity.setTicksFrozen(hudFor(steps));
        }
    }

    private static int hudFor(int steps) {
        return steps * HUD_STEP;
    }
}
