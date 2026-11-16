package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StoveBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class FrozenRuneConversion {
    private static final ResourceLocation FROZEN_RUNE =
            ResourceLocation.fromNamespaceAndPath("legendary_monsters", "frozen_rune");

    private FrozenRuneConversion() {
    }

    @SubscribeEvent
    public static void onRightClickStove(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!FROZEN_RUNE.equals(ForgeRegistries.ITEMS.getKey(held.getItem()))) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof StoveBlock) || state.getValue(StoveBlock.LIT)) {
            return;
        }

        if (level.isClientSide) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    ModSounds.FREEZE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
            event.setUseBlock(Event.Result.DENY);
            return;
        }

        Player player = event.getEntity();
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);

        level.setBlockAndUpdate(pos, ModBlocks.EVERFROST_STOVE.get().defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, state.getValue(HorizontalDirectionalBlock.FACING)));
        if (!player.isCreative()) {
            held.shrink(1);
        }
        level.playSound(player, pos, ModSounds.FREEZE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
