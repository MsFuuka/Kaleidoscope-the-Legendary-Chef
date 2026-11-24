package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseCropBlock.class)
public abstract class CookeryCropUseMixin {
    private static final ResourceLocation TOMATO_CROP =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "tomato_crop");

    @Inject(
            method = "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void kaleidoscope$noPickWhenConverting(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (TOMATO_CROP.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()))
                && player.getItemInHand(hand).is(ModItems.CRUSHED_MOSS.get())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
