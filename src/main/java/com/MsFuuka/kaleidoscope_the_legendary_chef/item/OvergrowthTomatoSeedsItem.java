package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class OvergrowthTomatoSeedsItem extends Item {
    public OvergrowthTomatoSeedsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(Blocks.FARMLAND) || !level.isEmptyBlock(pos.above())) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos.above(), ModBlocks.OVERGROWTH_TOMATO_CROP.get().defaultBlockState(), 3);
            if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
