package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class PrimordialSmoothieBucketItem extends BlockItem {
    public PrimordialSmoothieBucketItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getDescriptionId() {
        return Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = super.useOn(context);
        if (result.consumesAction()) {
            Player player = context.getPlayer();
            if (player != null && !player.getAbilities().instabuild) {
                player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
            }
        }
        return result;
    }
}
