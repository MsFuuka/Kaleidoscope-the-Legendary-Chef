package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FrostbitePepperCropBlock extends CropBlock {
    public static final int MAX_AGE = 6;
    private static final int AGE_AFTER_HARVEST = 4;
    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};

    public FrostbitePepperCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[Math.min(getAge(state), MAX_AGE)];
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (!isMaxAge(state)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            Block.popResource(level, pos, new ItemStack(ModItems.FROSTBITE_PEPPER.get(), 5));
        }
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlock(pos, getStateForAge(AGE_AFTER_HARVEST), 2);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int age = getAge(state);
        if (age < 4 || random.nextInt(10) != 0) {
            return;
        }
        double x = pos.getX() + 0.25 + random.nextDouble() * 0.5;
        double y = pos.getY() + 0.4 + age * 0.03 + random.nextDouble() * 0.15;
        double z = pos.getZ() + 0.25 + random.nextDouble() * 0.5;
        level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z,
                (random.nextDouble() - 0.5) * 0.02, -0.02 - random.nextDouble() * 0.02,
                (random.nextDouble() - 0.5) * 0.02);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.FROSTBITE_PEPPER.get();
    }
}
