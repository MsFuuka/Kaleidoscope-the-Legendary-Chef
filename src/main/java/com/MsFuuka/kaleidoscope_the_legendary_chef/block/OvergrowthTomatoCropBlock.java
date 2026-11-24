package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import java.util.HashMap;
import java.util.Map;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public final class OvergrowthTomatoCropBlock extends CropBlock {
    public static final int MAX_AGE = 7;
    private record ConvertedCrop(ResourceKey<Level> dimension, BlockPos pos) {
    }

    private static final Map<ConvertedCrop, Long> RECENT_CONVERSIONS = new HashMap<>();
    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    public OvergrowthTomatoCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static void markConverted(Level level, BlockPos pos) {
        long now = level.getGameTime();
        RECENT_CONVERSIONS.entrySet().removeIf(entry -> now - entry.getValue() > 2);
        RECENT_CONVERSIONS.put(new ConvertedCrop(level.dimension(), pos.immutable()), now);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(ModItems.TENDER_GREEN_MOSS.get())) {
            return InteractionResult.PASS;
        }
        Long convertedAt = RECENT_CONVERSIONS.get(new ConvertedCrop(level.dimension(), pos));
        if (convertedAt != null && level.getGameTime() - convertedAt <= 1) {
            return InteractionResult.PASS;
        }
        if (state.getValue(AGE) >= getMaxAge()) {
            Block.popResource(level, pos, new ItemStack(ModItems.OVERGROWTH_TOMATO.get()));
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlock(pos, getStateForAge(5), 2);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[Math.min(getAge(state), MAX_AGE)];
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.OVERGROWTH_TOMATO_SEEDS.get();
    }
}
