package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.EverfrostStoveBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.FrostbitePepperCropBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.FrozenMalletBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.IceCrystalBudBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PrimalIceBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PrimalIceCrystalClusterBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PrimordialSmoothieBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.WinterPowderSnowBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.XilingGreenBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, KaleidoscopeTheLegendaryChef.MODID);

    private static final DeferredRegister<Block> MINECRAFT_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");

    public static final RegistryObject<Block> EVERFROST_STOVE = BLOCKS.register("everfrost_stove",
            () -> new EverfrostStoveBlock(BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> PRIMAL_ICE_BLOCK = BLOCKS.register("primal_ice_block",
            () -> new PrimalIceBlock(BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.GLASS).randomTicks()));

    public static final RegistryObject<Block> SMALL_ICE_CRYSTAL_BUD = BLOCKS.register("small_ice_crystal_bud",
            () -> new IceCrystalBudBlock(3, 4, budProperties(1)));

    public static final RegistryObject<Block> LARGE_ICE_CRYSTAL_BUD = BLOCKS.register("large_ice_crystal_bud",
            () -> new IceCrystalBudBlock(4, 3, budProperties(2)));

    public static final RegistryObject<Block> PRIMAL_ICE_CRYSTAL_CLUSTER = BLOCKS.register("primal_ice_crystal_cluster",
            () -> new PrimalIceCrystalClusterBlock(5, 3, budProperties(4)));

    public static final RegistryObject<Block> FROZEN_MALLET = BLOCKS.register("frozen_mallet",
            () -> new FrozenMalletBlock(BlockBehaviour.Properties.of().strength(2.8F).sound(SoundType.GLASS).noOcclusion()));

    public static final RegistryObject<Block> WINTER_POWDER_SNOW = MINECRAFT_BLOCKS.register("winter_powder_snow",
            () -> new WinterPowderSnowBlock(BlockBehaviour.Properties.copy(Blocks.POWDER_SNOW).forceSolidOn()));

    public static final RegistryObject<Block> PILLAR_OF_PINNACLE_ICE = BLOCKS.register("pillar_of_pinnacle_ice",
            () -> new PillarOfPinnacleIceBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(SoundType.GLASS).noOcclusion().pushReaction(PushReaction.BLOCK)));

    public static final RegistryObject<Block> FROSTBITE_PEPPER_CROP = BLOCKS.register("frostbite_pepper_crop",
            () -> new FrostbitePepperCropBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.ICE)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<PrimordialSmoothieBlock> PRIMORDIAL_SMOOTHIE = BLOCKS.register("primordial_smoothie",
            () -> new PrimordialSmoothieBlock(BlockBehaviour.Properties.copy(Blocks.POWDER_SNOW).forceSolidOn().noLootTable()));

    public static final RegistryObject<Block> XILING_GREEN = BLOCKS.register("xiling_green",
            () -> new XilingGreenBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .mapColor(MapColor.ICE)
                    .sound(SoundType.GLASS)
                    .pushReaction(PushReaction.DESTROY)
                    .noOcclusion()));

    private ModBlocks() {
    }

    private static BlockBehaviour.Properties budProperties(int light) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.ICE)
                .forceSolidOn()
                .noOcclusion()
                .strength(1.5F)
                .sound(SoundType.GLASS)
                .lightLevel(state -> light)
                .pushReaction(PushReaction.DESTROY);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        MINECRAFT_BLOCKS.register(bus);
    }
}
