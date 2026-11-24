package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class MalletPillarBreak {
    private MalletPillarBreak() {
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }
        Player player = event.getEntity();
        if (!player.getMainHandItem().is(ModItems.FROZEN_MALLET.get())) {
            return;
        }
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(ModBlocks.PILLAR_OF_PINNACLE_ICE.get())
                && !level.getBlockState(pos.above()).is(ModBlocks.PILLAR_OF_PINNACLE_ICE.get())) {
            return;
        }
        event.setUseBlock(Event.Result.DENY);
        event.setCanceled(true);
        PillarOfPinnacleIceBlock.shatter(level, pos);
        player.swing(event.getHand());
    }
}
