package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class WinterPowderSnowBlock extends PowderSnowBlock {
    public WinterPowderSnowBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId() {
        return Blocks.POWDER_SNOW.getDescriptionId();
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        return new ItemStack(Items.POWDER_SNOW_BUCKET);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!level.isClientSide()) {
            level.levelEvent(2001, pos, Block.getId(state));
        }
        return new ItemStack(Items.POWDER_SNOW_BUCKET);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(Items.BUCKET)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            player.setItemInHand(hand, new ItemStack(Items.POWDER_SNOW_BUCKET));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            level.levelEvent(2001, pos, Block.getId(state));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
