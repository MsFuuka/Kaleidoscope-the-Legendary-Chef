package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.entity.TotemOfWinterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, KaleidoscopeTheLegendaryChef.MODID);

    public static final RegistryObject<EntityType<TotemOfWinterEntity>> TOTEM_OF_WINTER = ENTITY_TYPES.register("totem_of_winter",
            () -> EntityType.Builder.of(TotemOfWinterEntity::new, MobCategory.MISC)
                    .sized(1.0F, 2.2F)
                    .clientTrackingRange(10)
                    .build("totem_of_winter"));

    private ModEntities() {
    }

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
