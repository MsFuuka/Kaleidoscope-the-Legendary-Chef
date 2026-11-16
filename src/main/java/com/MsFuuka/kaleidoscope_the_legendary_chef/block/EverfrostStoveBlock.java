package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public final class EverfrostStoveBlock extends HorizontalDirectionalBlock {
    public EverfrostStoveBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double z = pos.getZ() + 0.5;

        level.addParticle(ParticleTypes.SNOWFLAKE,
                x + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                pos.getY() + 1.0 + random.nextDouble() / 3,
                z + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                0, 0.02, 0);

        Direction facing = state.getValue(FACING);
        Direction.Axis axis = facing.getAxis();
        double spread = random.nextDouble() * 0.6 - 0.3;
        double dx = axis == Direction.Axis.X ? facing.getStepX() * 0.6 : spread;
        double dy = 0.25 + random.nextDouble() * 6.0 / 16.0;
        double dz = axis == Direction.Axis.Z ? facing.getStepZ() * 0.6 : spread;

        level.addParticle(ParticleTypes.SNOWFLAKE, x + dx, pos.getY() + dy, z + dz, 0, 0, 0);
    }
}
