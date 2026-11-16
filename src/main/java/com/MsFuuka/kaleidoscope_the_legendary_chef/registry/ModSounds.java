package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, KaleidoscopeTheLegendaryChef.MODID);

    public static final RegistryObject<SoundEvent> EVERFROST_STOVE = register("everfrost_stove");
    public static final RegistryObject<SoundEvent> FREEZE = register("freeze");
    public static final RegistryObject<SoundEvent> FROZEN = register("frozen");
    public static final RegistryObject<SoundEvent> MALLET_SMASH = register("mallet_smash");
    public static final RegistryObject<SoundEvent> TOTEM_HIT = register("totem_hit");

    private ModSounds() {
    }

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, name)));
    }

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
