package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.XilingGreenBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.XilingGreenItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class XilingGreenTeapotPour {
    private static final ResourceLocation TEAPOT_ID =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "teapot");
    private static final ResourceLocation EMPTY_CUP_ID =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "empty_cup");

    private XilingGreenTeapotPour() {
    }

    @SubscribeEvent
    public static void onRightClickEmptyCup(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Player player = event.getEntity();
        if (player.isSecondaryUseActive()) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!EMPTY_CUP_ID.equals(ForgeRegistries.BLOCKS.getKey(state.getBlock()))) {
            return;
        }
        ItemStack held = event.getItemStack();
        Item teapot = ForgeRegistries.ITEMS.getValue(TEAPOT_ID);
        boolean fromTeapot = teapot != null && held.is(teapot);
        boolean fromTea = held.getItem() instanceof XilingGreenItem;
        if (!fromTeapot && !fromTea) {
            return;
        }
        CompoundTag data = null;
        if (fromTeapot) {
            data = held.getTagElement("BlockEntityTag");
            if (data == null || data.getInt("Status") != 2) {
                return;
            }
            ItemStack result = ItemStack.of(data.getCompound("Result"));
            if (result.isEmpty() || result.getItem() != ModItems.XILING_GREEN.get()) {
                return;
            }
        }
        if (level.isClientSide) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            event.setUseBlock(Event.Result.DENY);
            return;
        }
        Property<?> cupProp = state.getBlock().getStateDefinition().getProperty("cup_count");
        int cupCount = cupProp instanceof IntegerProperty intProp ? state.getValue(intProp) : 1;
        if (fromTea && cupCount >= 4) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
            return;
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        level.setBlockAndUpdate(pos, ModBlocks.XILING_GREEN.get().defaultBlockState()
                .setValue(XilingGreenBlock.CUP_COUNT, fromTea ? cupCount + 1 : cupCount)
                .setValue(XilingGreenBlock.TEA_COUNT, 1)
                .setValue(HorizontalDirectionalBlock.FACING,
                        state.getValue(HorizontalDirectionalBlock.FACING)));
        if (fromTea) {
            if (!player.isCreative()) {
                held.shrink(1);
            }
        } else {
            ItemStack result = ItemStack.of(data.getCompound("Result"));
            result.shrink(1);
            if (result.isEmpty()) {
                held.removeTagKey("BlockEntityTag");
            } else {
                data.put("Result", result.serializeNBT());
                held.getOrCreateTag().put("BlockEntityTag", data);
            }
        }
        level.playSound(player, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = level.random;
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    pos.getX() + 0.5, pos.getY() + 0.35, pos.getZ() + 0.5,
                    4, 0.12 + random.nextDouble() * 0.04, 0.08, 0.12 + random.nextDouble() * 0.04, 0.02);
        }
    }
}
