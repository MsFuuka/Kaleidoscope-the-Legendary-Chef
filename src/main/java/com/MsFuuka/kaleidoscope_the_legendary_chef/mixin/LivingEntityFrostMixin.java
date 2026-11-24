package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.MsFuuka.kaleidoscope_the_legendary_chef.effect.BitterColdState;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFrostMixin {
    @Inject(method = "tryAddFrost", at = @At("HEAD"), cancellable = true)
    private void frostfall$skipVanillaSlow(CallbackInfo info) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (PrimalIceNecklaceItem.isWorn(self) || BitterColdState.hasSlow(self)) {
            info.cancel();
        }
    }
}
