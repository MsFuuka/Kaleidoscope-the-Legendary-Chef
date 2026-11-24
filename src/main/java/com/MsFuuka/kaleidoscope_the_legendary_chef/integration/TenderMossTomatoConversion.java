package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.OvergrowthTomatoCropBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class TenderMossTomatoConversion {
    private static final ResourceLocation TOMATO_CROP =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "tomato_crop");
    private TenderMossTomatoConversion() {
    }

    @SubscribeEvent
    public static void onRightClickTomato(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!held.is(ModItems.CRUSHED_MOSS.get())) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!TOMATO_CROP.equals(ForgeRegistries.BLOCKS.getKey(state.getBlock()))) {
            return;
        }
        int age = state.getValue(CropBlock.AGE);

        if (level.isClientSide) {
            level.setBlock(pos, ModBlocks.OVERGROWTH_TOMATO_CROP.get().defaultBlockState()
                    .setValue(BlockStateProperties.AGE_7, age), 3);
            OvergrowthTomatoCropBlock.markConverted(level, pos);
            event.setUseBlock(Event.Result.DENY);
            return;
        }

        Player player = event.getEntity();
        level.setBlockAndUpdate(pos, ModBlocks.OVERGROWTH_TOMATO_CROP.get().defaultBlockState()
                .setValue(BlockStateProperties.AGE_7, age));
        OvergrowthTomatoCropBlock.markConverted(level, pos);
        if (!player.isCreative()) {
            held.shrink(1);
        }
        event.setUseBlock(Event.Result.DENY);
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
