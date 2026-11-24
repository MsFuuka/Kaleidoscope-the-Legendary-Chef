package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MossySwissRollBlock extends HorizontalDirectionalBlock {
    public static final IntegerProperty BITE = IntegerProperty.create("bite", 0, 3);
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D);

    public MossySwissRollBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BITE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BITE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(BITE) >= 3 ? 1.0F : super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        return eat(level, pos, state, player);
    }

    private static InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        player.awardStat(Stats.EAT_CAKE_SLICE);
        player.getFoodData().eat(3, 0.3F);
        level.playSound(player, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, 1.0F);
        int bite = state.getValue(BITE);
        if (bite < 3) {
            level.setBlock(pos, state.setValue(BITE, bite + 1), 3);
        } else {
            level.destroyBlock(pos, false);
            Block.popResource(level, pos, new ItemStack(Items.STONE_BRICK_SLAB));
        }
        return InteractionResult.SUCCESS;
    }
}
