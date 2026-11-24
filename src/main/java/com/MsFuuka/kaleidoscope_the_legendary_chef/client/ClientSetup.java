package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlockEntities;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEntities;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    @SuppressWarnings({"removal", "deprecation"})
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.FROSTBITE_PEPPER_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.OVERGROWTH_TOMATO_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TENDER_GREEN_MOSS_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.LARGE_ICE_CRYSTAL_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PRIMAL_ICE_CRYSTAL_CLUSTER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SMALL_ICE_CRYSTAL_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MOSSY_SWISS_ROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STONE_BAKED_MUFFIN.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        event.register(id("block/frozen_mallet/frozen_mallet"));
        event.register(id("block/frozen_mallet/frozen_mallet_first"));
        event.register(id("block/frozen_mallet/frozen_mallet_second"));
        event.register(id("block/frozen_mallet/frozen_mallet_third"));
        event.register(id("block/frozen_mallet/frozen_mallet_fourth"));
        event.register(id("block/frozen_mallet/frozen_mallet_fifth"));
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TotemOfWinterModel.LAYER_LOCATION, TotemOfWinterModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FROZEN_MALLET.get(), FrozenMalletBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PILLAR_OF_PINNACLE_ICE.get(), PillarOfPinnacleIceBlockRenderer::new);
        event.registerEntityRenderer(ModEntities.TOTEM_OF_WINTER.get(), TotemOfWinterRenderer::new);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, path);
    }
}
