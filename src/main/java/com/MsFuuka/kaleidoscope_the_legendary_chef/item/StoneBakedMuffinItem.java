package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class StoneBakedMuffinItem extends BlockItem {
    public StoneBakedMuffinItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.is(ModBlocks.STONE_BAKED_MUFFIN.get())) {
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }
}
