package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import java.util.WeakHashMap;

import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
        if (level instanceof ServerLevel server) {
            long now = level.getGameTime();
            long last = everfrost$lastFreezeHit.computeIfAbsent(entity, e -> now);
            if (now - last >= 40L) {
                if (!PrimalIceNecklaceItem.isWorn(entity)) {
                    entity.hurt(server.damageSources().freeze(), 1.0F);
                    BlockParticleOption ice = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());
                    server.sendParticles(ice, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D, entity.getZ(),
                            8, entity.getBbWidth() * 0.5D, entity.getBbHeight() * 0.5D,
                            entity.getBbWidth() * 0.5D, 0.02D);
                }
                everfrost$lastFreezeHit.put(entity, now);
            }
        }
        ci.cancel();
    }

    @Inject(method = "removeAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void everfrost$keepFrozenTicks(LivingEntity entity, AttributeMap map, int amplifier, CallbackInfo ci) {
        ci.cancel();
    }
}
