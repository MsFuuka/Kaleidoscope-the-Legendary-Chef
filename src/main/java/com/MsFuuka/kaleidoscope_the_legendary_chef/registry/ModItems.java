package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import java.util.List;
import java.util.function.Supplier;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimordialSmoothieBucketItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.TotemOfWinterItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.XilingGreenItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KaleidoscopeTheLegendaryChef.MODID);

    public static final RegistryObject<Item> FROSTBALL = ITEMS.register("frostball",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROSTBITE_PEPPER = ITEMS.register("frostbite_pepper",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PRIMORDIAL_SMOOTHIE = ITEMS.register("primordial_smoothie",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PRIMORDIAL_SMOOTHIE_BUCKET = ITEMS.register("primordial_smoothie_bucket",
            () -> new PrimordialSmoothieBucketItem(ModBlocks.PRIMORDIAL_SMOOTHIE.get(),
                    new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> A_SONG_OF_ICE_AND_FIRE = ITEMS.register("a_song_of_ice_and_fire",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROSTFALL_LAMB_BLOOD_PUDDING = ITEMS.register("frostfall_lamb_blood_pudding",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROZEN_MALLET = ITEMS.register("frozen_mallet",
            () -> new FrozenMalletItem(new Item.Properties().stacksTo(1).durability(925)));

    public static final RegistryObject<Item> EVERFROST_STOVE = ITEMS.register("everfrost_stove",
            () -> new BlockItem(ModBlocks.EVERFROST_STOVE.get(), new Item.Properties()));

    public static final RegistryObject<Item> PRIMAL_ICE_BLOCK = ITEMS.register("primal_ice_block",
            () -> new BlockItem(ModBlocks.PRIMAL_ICE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> SMALL_ICE_CRYSTAL_BUD = ITEMS.register("small_ice_crystal_bud",
            () -> new BlockItem(ModBlocks.SMALL_ICE_CRYSTAL_BUD.get(), new Item.Properties()));

    public static final RegistryObject<Item> LARGE_ICE_CRYSTAL_BUD = ITEMS.register("large_ice_crystal_bud",
            () -> new BlockItem(ModBlocks.LARGE_ICE_CRYSTAL_BUD.get(), new Item.Properties()));

    public static final RegistryObject<Item> PRIMAL_ICE_CRYSTAL_CLUSTER = ITEMS.register("primal_ice_crystal_cluster",
            () -> new BlockItem(ModBlocks.PRIMAL_ICE_CRYSTAL_CLUSTER.get(), new Item.Properties()));

    public static final RegistryObject<Item> TOTEM_OF_WINTER = ITEMS.register("totem_of_winter",
            () -> new TotemOfWinterItem(new Item.Properties()));

    public static final RegistryObject<Item> XILING_GREEN_TEA_BAG = ITEMS.register("xiling_green_tea_bag",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> XILING_GREEN = ITEMS.register("xiling_green",
            () -> new XilingGreenItem(ModBlocks.XILING_GREEN.get(),
                    List.of(Pair.<Supplier<MobEffectInstance>, Float>of(
                            () -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.3F))));

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
