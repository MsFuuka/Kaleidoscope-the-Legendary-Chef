package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.TenderGreenMossCropBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class TenderGreenMossItem extends Item {
    public TenderGreenMossItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!TenderGreenMossCropBlock.isPlantableBase(level.getBlockState(pos)) || !level.isEmptyBlock(pos.above())) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos.above(), ModBlocks.TENDER_GREEN_MOSS_CROP.get().defaultBlockState(), 3);
            if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
