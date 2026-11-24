package com.MsFuuka.kaleidoscope_the_legendary_chef;

import com.mojang.logging.LogUtils;
import com.MsFuuka.kaleidoscope_the_legendary_chef.integration.StockpotRecipeEventHandler;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlockEntities;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModCreativeTabs;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEffects;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEntities;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModRecipes;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModSoupBases;
import com.MsFuuka.kaleidoscope_the_legendary_chef.network.ModMessages;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(KaleidoscopeTheLegendaryChef.MODID)
@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KaleidoscopeTheLegendaryChef {
    public static final String MODID = "kaleidoscope_the_legendary_chef";
    public static final Logger LOGGER = LogUtils.getLogger();

    public KaleidoscopeTheLegendaryChef(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new StockpotRecipeEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("{} initialized", MODID);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ModSoupBases.register();
        ModMessages.register();
        CuriosApi.registerCurio(ModItems.PRIMAL_ICE_NECKLACE.get(), (PrimalIceNecklaceItem) ModItems.PRIMAL_ICE_NECKLACE.get());
    }
}
