package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @ModifyConstant(method = "aiStep", constant = @Constant(floatValue = 0.2F))
    private float everfrost$noUseSlowdown(float original) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        return player.getUseItem().getItem() instanceof FrozenMalletItem ? 1.0F : original;
    }
}
