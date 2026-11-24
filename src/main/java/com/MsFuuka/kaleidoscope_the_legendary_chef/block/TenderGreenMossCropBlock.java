package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class TenderGreenMossCropBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    public static final int MAX_AGE = 2;
    public static final int CONVERT_INTERVAL = 1360;

    public static int convertDelay(Level level) {
        return CONVERT_INTERVAL - (int) (level.getGameTime() % CONVERT_INTERVAL);
    }
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final int[][] SIDES = {{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final int CONVERT_SEARCH_LIMIT = 128;

    public TenderGreenMossCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isPlantableBase(level.getBlockState(pos.below()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
            BlockPos currentPos, BlockPos facingPos) {
        if (!state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    public static boolean isPlantableBase(BlockState state) {
        return state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.COBBLESTONE) || state.is(Blocks.MOSSY_COBBLESTONE);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.TENDER_GREEN_MOSS.get());
    }

    public BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(AGE, Math.min(age, MAX_AGE));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(AGE) < MAX_AGE || !player.getItemInHand(hand).is(Items.SHEARS)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            Block.popResource(level, pos, new ItemStack(ModItems.TENDER_GREEN_MOSS.get(), 1 + level.random.nextInt(3)));
            level.setBlock(pos, getStateForAge(0), 2);
        }
        level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE) {
            if (random.nextInt(3) == 0) {
                int nextAge = age + 1;
                level.setBlock(pos, getStateForAge(nextAge), 2);
                if (nextAge == 1) {
                    level.scheduleTick(pos, this, convertDelay(level));
                } else if (nextAge == MAX_AGE) {
                    convertCobble(level, pos);
                    spread(level, pos);
                }
            }
            return;
        }
        level.scheduleTick(pos, this, convertDelay(level));
        if (random.nextInt(8) == 0) {
            spread(level, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) == 0) {
            return;
        }
        convertCobble(level, pos);
        level.scheduleTick(pos, this, CONVERT_INTERVAL);
    }

    private void convertCobble(ServerLevel level, BlockPos pos) {
        BlockPos ground = pos.below();
        if (level.getBlockState(ground).is(Blocks.COBBLESTONE)) {
            level.setBlock(ground, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3);
        }
        MossConversionManager.scheduleAround(level, ground);
    }

    public static boolean canConvertConnected(LevelReader level, BlockPos cobble) {
        if (!level.getBlockState(cobble).is(Blocks.COBBLESTONE)) {
            return false;
        }
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(cobble);
        queue.add(cobble);
        int count = 0;
        while (!queue.isEmpty() && count < CONVERT_SEARCH_LIMIT) {
            BlockPos current = queue.poll();
            count++;
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                if (!visited.add(neighbor)) {
                    continue;
                }
                if (!level.getBlockState(neighbor).is(Blocks.MOSSY_COBBLESTONE)) {
                    continue;
                }
                BlockState above = level.getBlockState(neighbor.above());
                if (above.is(ModBlocks.TENDER_GREEN_MOSS_CROP.get())
                        && above.getValue(TenderGreenMossCropBlock.AGE) != 0) {
                    return true;
                }
                queue.add(neighbor);
            }
        }
        return false;
    }

    private void spread(ServerLevel level, BlockPos pos) {
        for (int[] side : SIDES) {
            for (int dy = -2; dy <= 1; dy++) {
                if (side[0] == 0 && side[1] == 0 && dy == 0) {
                    continue;
                }
                BlockPos ground = new BlockPos(pos.getX() + side[0], pos.getY() + dy, pos.getZ() + side[1]);
                BlockState groundState = level.getBlockState(ground);
                if (!groundState.is(Blocks.MOSS_BLOCK) && !groundState.is(Blocks.MOSSY_COBBLESTONE)) {
                    continue;
                }
                BlockPos above = ground.above();
                if (level.isEmptyBlock(above)) {
                    level.setBlock(above, getStateForAge(0), 3);
                    return;
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int nextAge = state.getValue(AGE) + 1;
        level.setBlock(pos, getStateForAge(nextAge), 2);
        if (nextAge >= 1) {
            level.scheduleTick(pos, this, convertDelay(level));
        }
    }
}
