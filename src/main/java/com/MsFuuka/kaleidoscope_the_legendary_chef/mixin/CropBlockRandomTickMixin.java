package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.TotemWinterAura;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockRandomTickMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true, remap = true)
    private void kaleidoscope_slowGrowth(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (state.is(ModBlocks.FROSTBITE_PEPPER_CROP.get())) {
            return;
        }
        if (TotemWinterAura.inWinterZone(level, pos) && random.nextFloat() < 0.5F) {
            ci.cancel();
        }
    }
}
