package com.MsFuuka.kaleidoscope_the_legendary_chef.compat.jade;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.MossConversionManager;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.TenderGreenMossCropBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.ITooltip;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

@WailaPlugin("kaleidoscope_the_legendary_chef")
public final class JadePlugin implements IWailaPlugin {
    private static final ResourceLocation MOSS_ICON_ID =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_the_legendary_chef", "tender_green_moss_icon");
    private static final IBlockComponentProvider MOSS_ICON = new IBlockComponentProvider() {
        @Override
        public ResourceLocation getUid() {
            return MOSS_ICON_ID;
        }

        @Override
        public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
            return IElementHelper.get().item(new ItemStack(ModItems.TENDER_GREEN_MOSS.get()));
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        }
    };

    private static final ResourceLocation CONVERT_ID =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_the_legendary_chef", "cobble_convert");
    private static final IBlockComponentProvider CONVERT = new IBlockComponentProvider() {
        @Override
        public ResourceLocation getUid() {
            return CONVERT_ID;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!accessor.getBlockState().is(Blocks.COBBLESTONE)) {
                return;
            }
            BlockPos pos = accessor.getPosition();
            int ticks = MossConversionManager.remainingTicks(accessor.getLevel(), pos);
            if (ticks < 0) {
                return;
            }
            tooltip.add(Component.translatable(
                    "tooltip.kaleidoscope_the_legendary_chef.cobble_convert", (ticks + 19) / 20));
        }
    };

    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.usePickedResult(ModBlocks.TENDER_GREEN_MOSS_CROP.get());
        registration.registerBlockIcon(MOSS_ICON, TenderGreenMossCropBlock.class);
        registration.registerBlockComponent(CONVERT, Block.class);
    }
}
