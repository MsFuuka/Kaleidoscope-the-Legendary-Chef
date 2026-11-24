package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.MossConversionManager;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.TenderGreenMossCropBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "kaleidoscope_the_legendary_chef", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MossCobbleEventHandler {
    private MossCobbleEventHandler() {
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            MossConversionManager.onLevelLoad(level);
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !event.getPlacedBlock().is(Blocks.COBBLESTONE)) {
            return;
        }
        if (level.getBlockState(event.getPos().above()).is(ModBlocks.TENDER_GREEN_MOSS_CROP.get())) {
            return;
        }
        if (TenderGreenMossCropBlock.canConvertConnected(level, event.getPos())) {
            MossConversionManager.schedule(level, event.getPos());
        }
    }
}
