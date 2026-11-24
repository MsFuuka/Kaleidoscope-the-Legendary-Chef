package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class PrimordialSmoothieBlock extends PowderSnowBlock {
    public PrimordialSmoothieBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!level.isClientSide()) {
            level.levelEvent(2001, pos, Block.getId(state));
        }
        return new ItemStack(ModItems.PRIMORDIAL_SMOOTHIE_BUCKET.get());
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.PRIMORDIAL_SMOOTHIE_BUCKET.get());
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && entity.isOnFire()) {
            entity.clearFire();
        }
        super.entityInside(state, level, pos, entity);
        if (entity instanceof LivingEntity living && !living.canFreeze()
                && !PrimalIceNecklaceItem.isWorn(living)) {
            int required = living.getTicksRequiredToFreeze();
            living.setTicksFrozen(required);
            if (!level.isClientSide && living.tickCount % 40 == 0) {
                living.hurt(level.damageSources().freeze(), 1.0F);
            }
        }
    }
}
