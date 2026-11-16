package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.FrozenMalletBlockEntity;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, KaleidoscopeTheLegendaryChef.MODID);

    public static final RegistryObject<BlockEntityType<FrozenMalletBlockEntity>> FROZEN_MALLET = BLOCK_ENTITIES.register("frozen_mallet",
            () -> BlockEntityType.Builder.of(FrozenMalletBlockEntity::new, ModBlocks.FROZEN_MALLET.get()).build(null));

    public static final RegistryObject<BlockEntityType<PillarOfPinnacleIceBlockEntity>> PILLAR_OF_PINNACLE_ICE = BLOCK_ENTITIES.register("pillar_of_pinnacle_ice",
            () -> BlockEntityType.Builder.of(PillarOfPinnacleIceBlockEntity::new, ModBlocks.PILLAR_OF_PINNACLE_ICE.get()).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
