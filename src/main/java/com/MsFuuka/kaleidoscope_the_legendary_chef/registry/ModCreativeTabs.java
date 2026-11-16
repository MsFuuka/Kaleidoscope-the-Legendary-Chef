package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KaleidoscopeTheLegendaryChef.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.kaleidoscope_the_legendary_chef.main"))
            .icon(() -> new ItemStack(ModItems.FROSTBITE_PEPPER.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.FROSTBALL.get());
                output.accept(ModItems.FROSTBITE_PEPPER.get());
                output.accept(ModItems.PRIMORDIAL_SMOOTHIE.get());
                output.accept(ModItems.PRIMORDIAL_SMOOTHIE_BUCKET.get());
                output.accept(ModItems.A_SONG_OF_ICE_AND_FIRE.get());
                output.accept(ModItems.FROSTFALL_LAMB_BLOOD_PUDDING.get());
                output.accept(ModItems.FROZEN_MALLET.get());
                output.accept(ModItems.EVERFROST_STOVE.get());
                output.accept(ModItems.PRIMAL_ICE_BLOCK.get());
                output.accept(ModItems.SMALL_ICE_CRYSTAL_BUD.get());
                output.accept(ModItems.LARGE_ICE_CRYSTAL_BUD.get());
                output.accept(ModItems.PRIMAL_ICE_CRYSTAL_CLUSTER.get());
                output.accept(ModItems.TOTEM_OF_WINTER.get());
                output.accept(ModItems.XILING_GREEN_TEA_BAG.get());
                output.accept(ModItems.XILING_GREEN.get());
            })
            .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
