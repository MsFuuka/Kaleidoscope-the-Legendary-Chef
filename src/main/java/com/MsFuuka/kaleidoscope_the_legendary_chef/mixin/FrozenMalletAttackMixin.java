package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;

@Mixin(Player.class)
public abstract class FrozenMalletAttackMixin {
    @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void everfrost$captureStrength(Entity target, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        FrozenMalletItem.MALLET_ATTACK_STRENGTH.put(player, player.getAttackStrengthScale(0.5F));
    }
}
