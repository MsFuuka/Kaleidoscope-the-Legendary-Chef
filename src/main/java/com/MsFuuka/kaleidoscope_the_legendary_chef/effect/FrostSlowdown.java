package com.MsFuuka.kaleidoscope_the_legendary_chef.effect;

import java.util.UUID;
import java.util.WeakHashMap;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class FrostSlowdown {
    public static final double SHATTER_SLOW = 0.10D;
    private static final UUID SLOW_UUID = UUID.fromString("b3f1c7a9-5e42-4d88-9a30-7c1e6f2d8b40");
    private static final WeakHashMap<LivingEntity, Long> EXPIRE = new WeakHashMap<>();

    private FrostSlowdown() {
    }

    public static void apply(LivingEntity entity, int durationTicks) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        if (PrimalIceNecklaceItem.isWorn(entity)) {
            EXPIRE.remove(entity);
            AttributeModifier existing = speed.getModifier(SLOW_UUID);
            if (existing != null) {
                speed.removeModifier(existing);
            }
            return;
        }
        if (speed.getModifier(SLOW_UUID) == null) {
            speed.addPermanentModifier(new AttributeModifier(SLOW_UUID, "shatter_cold",
                    -SHATTER_SLOW, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        EXPIRE.put(entity, entity.level().getGameTime() + durationTicks);
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        Long expire = EXPIRE.get(entity);
        if (expire == null) {
            return;
        }
        if (entity.level().getGameTime() >= expire) {
            EXPIRE.remove(entity);
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                AttributeModifier existing = speed.getModifier(SLOW_UUID);
                if (existing != null) {
                    speed.removeModifier(existing);
                }
            }
        }
    }
}
