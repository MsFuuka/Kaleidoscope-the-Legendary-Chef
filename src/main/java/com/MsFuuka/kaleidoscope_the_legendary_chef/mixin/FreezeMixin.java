package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import java.util.WeakHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.miauczel.legendary_monsters.effect.custom.Freeze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Freeze.class)
public class FreezeMixin {
    private static final WeakHashMap<LivingEntity, Long> everfrost$lastFreezeHit = new WeakHashMap<>();

    @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true)
    private void everfrost$applyFreeze(LivingEntity entity, int amplifier, CallbackInfo ci) {
        Level level = entity.level();
        if (!level.isClientSide()) {
            long now = level.getGameTime();
            long last = everfrost$lastFreezeHit.computeIfAbsent(entity, e -> now);
            if (now - last >= 25L) {
                entity.hurt(level.damageSources().freeze(), 1.0F);
                everfrost$lastFreezeHit.put(entity, now);
            }
        }
        ci.cancel();
    }
}
