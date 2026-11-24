package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import java.util.List;
import java.util.function.Supplier;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.CrushedMossItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrostbitePepperItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.MossySwissRollItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.OvergrowthTomatoSeedsItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimordialSmoothieBucketItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.StoneBakedMuffinItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.TenderGreenMossItem;
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
            () -> new FrostbitePepperItem(new Item.Properties()));

    public static final RegistryObject<Item> PRIMORDIAL_SMOOTHIE = ITEMS.register("primordial_smoothie",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PRIMORDIAL_SMOOTHIE_BUCKET = ITEMS.register("primordial_smoothie_bucket",
            () -> new PrimordialSmoothieBucketItem(ModBlocks.PRIMORDIAL_SMOOTHIE.get(),
                    new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> A_SONG_OF_ICE_AND_FIRE = ITEMS.register("a_song_of_ice_and_fire",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SNOWBALL_BOWL_IN_POETRY = ITEMS.register("snowball_bowl_in_poetry",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROSTFALL_LAMB_BLOOD_PUDDING = ITEMS.register("frostfall_lamb_blood_pudding",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COLD_AGED_MIXED_GRILL = ITEMS.register("cold_aged_mixed_grill",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOFFEE_APPLE = ITEMS.register("toffee_apple",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOFFEE_GOLDEN_APPLE = ITEMS.register("toffee_golden_apple",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOFFEE_APPLE_PLATTER = ITEMS.register("toffee_apple_platter",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOFFEE_GOLDEN_APPLE_PLATTER = ITEMS.register("toffee_golden_apple_platter",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PRIMAL_ICE_NECKLACE = ITEMS.register("primal_ice_necklace",
            () -> new PrimalIceNecklaceItem(new Item.Properties()));

    public static final RegistryObject<Item> POEM_OF_SNOW = ITEMS.register("poem_of_snow",
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

    public static final RegistryObject<Item> TENDER_GREEN_MOSS = ITEMS.register("tender_green_moss",
            () -> new TenderGreenMossItem(new Item.Properties()));

    public static final RegistryObject<Item> DRIED_TENDER_GREEN_MOSS = ITEMS.register("dried_tender_green_moss",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CRUSHED_MOSS = ITEMS.register("crushed_moss",
            () -> new CrushedMossItem(new Item.Properties()));

    public static final RegistryObject<Item> OVERGROWTH_TOMATO_SEEDS = ITEMS.register("overgrowth_tomato_seeds",
            () -> new OvergrowthTomatoSeedsItem(new Item.Properties()));

    public static final RegistryObject<Item> OVERGROWTH_TOMATO = ITEMS.register("overgrowth_tomato",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OVERGROWTH_TOMATO_SLICE = ITEMS.register("overgrowth_tomato_slice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOSSY_SWEET_DOUGH = ITEMS.register("mossy_sweet_dough",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOSSY_SWISS_ROLL = ITEMS.register("mossy_swiss_roll",
            () -> new MossySwissRollItem(ModBlocks.MOSSY_SWISS_ROLL.get(), new Item.Properties()));

    public static final RegistryObject<Item> STONE_BAKED_MUFFIN = ITEMS.register("stone_baked_muffin",
            () -> new StoneBakedMuffinItem(ModBlocks.STONE_BAKED_MUFFIN.get(), new Item.Properties()));

    public static final RegistryObject<Item> SUGARED_TOMATO = ITEMS.register("sugared_tomato",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOSS_DYED_PARFAIT = ITEMS.register("moss_dyed_parfait",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SWAMP_CHOWDER = ITEMS.register("swamp_chowder",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OVERGROWTH_SALAD = ITEMS.register("overgrowth_salad",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOTEM_OF_NATURE = ITEMS.register("totem_of_nature",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ANCIENT_SANDSTONE_GRIT = ITEMS.register("ancient_sandstone_grit",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WILD_RIVER_VALLEY_RICE = ITEMS.register("wild_river_valley_rice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ANCIENT_RIVER_VALLEY_RICE = ITEMS.register("ancient_river_valley_rice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RIVER_VALLEY_RICE = ITEMS.register("river_valley_rice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BOMBER_FENNEL = ITEMS.register("bomber_fennel",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DRIED_BOMBER_FENNEL = ITEMS.register("dried_bomber_fennel",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FERVENT_SPICE = ITEMS.register("fervent_spice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BOMBER_STEAMED_BUN = ITEMS.register("bomber_steamed_bun",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PLATE_OF_BOMBER_STEAMED_BUNS = ITEMS.register("plate_of_bomber_steamed_buns",
            () -> new Item(new Item.Properties()));

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
