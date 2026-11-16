package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import com.MsFuuka.kaleidoscope_the_legendary_chef.entity.TotemOfWinterEntity;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TotemOfWinterItem extends Item {
    public TotemOfWinterItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.FAIL;
        }
        Level level = context.getLevel();
        BlockPos pos = new BlockPlaceContext(context).getClickedPos();
        Vec3 at = Vec3.atBottomCenterOf(pos);
        AABB box = ModEntities.TOTEM_OF_WINTER.get().getDimensions().makeBoundingBox(at.x(), at.y(), at.z());
        if (!level.noCollision((Entity) null, box) || !level.getEntities((Entity) null, box).isEmpty()) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel) {
            TotemOfWinterEntity totem = ModEntities.TOTEM_OF_WINTER.get().create(level);
            if (totem == null) {
                return InteractionResult.FAIL;
            }
            float rotation = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
            totem.moveTo(at.x(), at.y(), at.z(), rotation, 0.0F);
            level.addFreshEntity(totem);
            level.playSound(null, at.x(), at.y(), at.z(), SoundType.GLASS.getPlaceSound(), SoundSource.BLOCKS, 0.75F, 0.8F);
        }
        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
