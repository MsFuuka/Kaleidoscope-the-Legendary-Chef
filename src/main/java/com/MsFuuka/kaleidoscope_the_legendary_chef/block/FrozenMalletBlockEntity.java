package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FrozenMalletBlockEntity extends BlockEntity {
    public FrozenMalletBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FROZEN_MALLET.get(), pos, state);
    }
}
