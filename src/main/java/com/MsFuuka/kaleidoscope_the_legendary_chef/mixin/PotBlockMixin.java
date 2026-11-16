package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.OIL_POT;

@Mixin(PotBlock.class)
public abstract class PotBlockMixin {
    @ModifyVariable(
            method = "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private BlockState everfrost$assumeOil(BlockState value, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()
                || value.getValue(PotBlock.HAS_OIL)
                || held.is(TagMod.OIL)
                || held.is(OIL_POT.get())
                || !level.getBlockState(pos.below()).is(ModTags.COLD_SOURCE)) {
            return value;
        }

        BlockState oiled = value.setValue(PotBlock.HAS_OIL, true);
        level.setBlockAndUpdate(pos, oiled);
        return oiled;
    }
}
