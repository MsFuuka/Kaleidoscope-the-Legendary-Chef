package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.MossConversionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class CobbleConversionMixin {
    @Inject(method = "tick", at = @At("HEAD"), remap = true)
    private void kaleidoscope$convertCobble(BlockState state, ServerLevel level, BlockPos pos,
            RandomSource random, CallbackInfo ci) {
        if (state.is(Blocks.COBBLESTONE)) {
            MossConversionManager.convert(level, pos);
        }
    }
}
