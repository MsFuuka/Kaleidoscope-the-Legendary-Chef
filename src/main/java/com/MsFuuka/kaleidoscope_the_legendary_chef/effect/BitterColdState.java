package com.MsFuuka.kaleidoscope_the_legendary_chef.effect;

import java.util.UUID;
import java.util.WeakHashMap;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class BitterColdState {
    public static final int FROSTFALL_DURATION = 300;
    public static final int CONVERT_TICKS = 60;
    private static final int STEP_TICKS = 25;
    private static final int MAX_STEPS = 7;
    private static final double COLD_SLOW = 0.05D;
    private static final double BASE_AMOUNT = 0.05D;
    private static final double STEP_AMOUNT = 0.015D;
    private static final double MAX_AMOUNT = 0.15D;
    private static final int FROSTFALL_HUD_MAX = 70;
    private static final UUID SLOW_UUID = UUID.fromString("d84f2a61-3b70-4c19-9e52-a6c0f1d7b834");

    private static final WeakHashMap<LivingEntity, Integer> STEP = new WeakHashMap<>();
    private static final WeakHashMap<LivingEntity, Long> LAST_STEP = new WeakHashMap<>();
    private static final WeakHashMap<LivingEntity, Integer> EXPOSURE = new WeakHashMap<>();
    private static final WeakHashMap<LivingEntity, Long> LAST_IN_PILLAR = new WeakHashMap<>();

    private BitterColdState() {
    }

    public static void applyPillar(LivingEntity entity) {
        if (entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get())) {
            clearState(entity);
            return;
        }
        long now = entity.level().getGameTime();
        Long lastSeen = LAST_IN_PILLAR.get(entity);
        boolean reentered = lastSeen == null || now - lastSeen > 1L;
        LAST_IN_PILLAR.put(entity, now);
        if (isFrostfall(entity)) {
            if (reentered) {
                setEffect(entity, FROSTFALL_DURATION, 1);
            }
            return;
        }
        int exposure = EXPOSURE.merge(entity, 1, Integer::sum);
        if (exposure >= CONVERT_TICKS) {
            EXPOSURE.remove(entity);
            STEP.put(entity, 0);
            LAST_STEP.put(entity, now);
            setEffect(entity, FROSTFALL_DURATION, 1);
        } else {
            setEffect(entity, CONVERT_TICKS - exposure + 1, 0);
        }
    }

    private static void setEffect(LivingEntity entity, int ticks, int amplifier) {
        entity.removeEffect(ModEffects.BITTER_COLD.get());
        entity.addEffect(new MobEffectInstance(ModEffects.BITTER_COLD.get(), ticks, amplifier, false, true));
    }

    public static void clear(LivingEntity entity) {
        clearState(entity);
        entity.setTicksFrozen(0);
    }

    public static void clearState(LivingEntity entity) {
        entity.removeEffect(ModEffects.BITTER_COLD.get());
        STEP.remove(entity);
        LAST_STEP.remove(entity);
        EXPOSURE.remove(entity);
        setSlow(entity, 0.0D);
    }

    public static boolean isCold(LivingEntity entity) {
        MobEffectInstance effect = entity.getEffect(ModEffects.BITTER_COLD.get());
        return effect != null && effect.getAmplifier() == 0;
    }

    public static boolean isFrostfall(LivingEntity entity) {
        MobEffectInstance effect = entity.getEffect(ModEffects.BITTER_COLD.get());
        return effect != null && effect.getAmplifier() >= 1;
    }

    public static int stepsOf(LivingEntity entity) {
        Integer steps = STEP.get(entity);
        return steps == null ? 0 : steps;
    }

    public static void setFrozen(LivingEntity entity, int ticks) {
        entity.setTicksFrozen(PrimalIceNecklaceItem.isWorn(entity) ? 0 : ticks);
    }

    public static boolean hasSlow(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        return speed != null && speed.getModifier(SLOW_UUID) != null;
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get())) {
            clearState(entity);
            return;
        }
        if (!entity.hasEffect(ModEffects.BITTER_COLD.get())) {
            clear(entity);
            return;
        }
        if (!isFrostfall(entity)) {
            setSlow(entity, COLD_SLOW);
            entity.setTicksFrozen(0);
            return;
        }
        long now = entity.level().getGameTime();
        int steps = STEP.getOrDefault(entity, 0);
        if (steps == 0) {
            LAST_STEP.putIfAbsent(entity, now);
        }
        long last = LAST_STEP.getOrDefault(entity, now);
        if (steps < MAX_STEPS && now - last >= STEP_TICKS) {
            steps++;
            STEP.put(entity, steps);
            LAST_STEP.put(entity, now);
        }
        setSlow(entity, COLD_SLOW + amountFor(steps));
        setFrozen(entity, Math.round((float) steps / MAX_STEPS * FROSTFALL_HUD_MAX));
    }

    private static double amountFor(int steps) {
        return steps < 0 ? 0.0D : Math.min(MAX_AMOUNT, BASE_AMOUNT + steps * STEP_AMOUNT);
    }

    private static void setSlow(LivingEntity entity, double amount) {
        if (PrimalIceNecklaceItem.isWorn(entity)) {
            amount = 0.0D;
        }
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        AttributeModifier existing = speed.getModifier(SLOW_UUID);
        if (amount <= 0.0D) {
            if (existing != null) {
                speed.removeModifier(existing);
            }
            return;
        }
        if (existing != null && Math.abs(existing.getAmount() + amount) < 1.0E-6D) {
            return;
        }
        if (existing != null) {
            speed.removeModifier(existing);
        }
        speed.addPermanentModifier(new AttributeModifier(SLOW_UUID, "bitter_cold_slow",
                -amount, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
