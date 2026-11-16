package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public final class PrimalIceBlock extends Block {
    private static final int GROWTH_CHANCE = 5;
    private static final Direction[] DIRECTIONS = Direction.values();

    public PrimalIceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(GROWTH_CHANCE) != 0) {
            return;
        }

        Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        BlockPos target = pos.relative(direction);
        BlockState targetState = level.getBlockState(target);
        Block next = null;
        if (targetState.isAir() || targetState.is(Blocks.WATER) && targetState.getFluidState().getAmount() == 8) {
            next = ModBlocks.SMALL_ICE_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.SMALL_ICE_CRYSTAL_BUD.get())
                && targetState.getValue(IceCrystalBudBlock.FACING) == direction) {
            next = ModBlocks.LARGE_ICE_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.LARGE_ICE_CRYSTAL_BUD.get())
                && targetState.getValue(IceCrystalBudBlock.FACING) == direction) {
            next = ModBlocks.PRIMAL_ICE_CRYSTAL_CLUSTER.get();
        }

        if (next != null) {
            level.setBlockAndUpdate(target, next.defaultBlockState()
                    .setValue(IceCrystalBudBlock.FACING, direction)
                    .setValue(IceCrystalBudBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER));
        }
    }
}
