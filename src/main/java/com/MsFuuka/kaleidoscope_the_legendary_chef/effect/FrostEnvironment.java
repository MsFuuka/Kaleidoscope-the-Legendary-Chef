package com.MsFuuka.kaleidoscope_the_legendary_chef.effect;

import java.util.UUID;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class FrostEnvironment {
    private static final UUID COLD_WATER_UUID = UUID.fromString("8d6f2a19-4b0e-4c7a-9d31-6b2a5e8f0c44");
    private static final double COLD_WATER_SLOW = 0.10D;
    private static final int FIRE_REACH = 2;
    private static final float FREEZE_FIRE_RESIST = 0.5F;
    private static final float FREEZE_FROSTBITE = 1.40F;

    private FrostEnvironment() {
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide) {
            return;
        }
        boolean immune = PrimalIceNecklaceItem.isWorn(entity);
        boolean cold = BitterColdState.isCold(entity);
        if (cold && nearFire(entity)) {
            BitterColdState.clear(entity);
            cold = false;
        }
        applyWaterSlow(entity, !immune && cold && entity.isInWater());
        if (!immune && BitterColdState.isFrostfall(entity) && entity.isInWater()) {
            entity.hurt(level.damageSources().freeze(), 5.0F);
            BitterColdState.clear(entity);
        }
        if ((entity.hasEffect(ModEffects.BITTER_COLD.get())
                || entity.hasEffect(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get()))
                && entity.isOnFire()) {
            entity.clearFire();
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (PrimalIceNecklaceItem.isWorn(entity) && source.is(DamageTypes.FREEZE)) {
            event.setCanceled(true);
            return;
        }
        if (!entity.hasEffect(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get())) {
            return;
        }
        if (source.is(DamageTypes.FREEZE)
                && !entity.hasEffect(ModEffects.BITTER_COLD.get())) {
            event.setAmount(event.getAmount() * FREEZE_FROSTBITE);
        }
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR)
                || source.is(DamageTypes.FIREBALL) || source.is(DamageTypes.UNATTRIBUTED_FIREBALL)) {
            event.setAmount(event.getAmount() * FREEZE_FIRE_RESIST);
        }
    }

    private static boolean nearFire(LivingEntity entity) {
        Level level = entity.level();
        BlockPos center = entity.blockPosition();
        for (int dx = -FIRE_REACH; dx <= FIRE_REACH; dx++) {
            for (int dy = -1; dy <= FIRE_REACH; dy++) {
                for (int dz = -FIRE_REACH; dz <= FIRE_REACH; dz++) {
                    if (dx * dx + dy * dy + dz * dz > FIRE_REACH * FIRE_REACH) {
                        continue;
                    }
                    BlockState state = level.getBlockState(center.offset(dx, dy, dz));
                    if (state.is(BlockTags.FIRE) || state.is(BlockTags.CAMPFIRES)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void applyWaterSlow(LivingEntity entity, boolean active) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        AttributeModifier existing = speed.getModifier(COLD_WATER_UUID);
        if (!active) {
            if (existing != null) {
                speed.removeModifier(existing);
            }
            return;
        }
        if (existing == null) {
            speed.addPermanentModifier(new AttributeModifier(COLD_WATER_UUID, "cold_water_slow",
                    -COLD_WATER_SLOW, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }
}
