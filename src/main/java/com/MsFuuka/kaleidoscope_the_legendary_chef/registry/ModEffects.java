package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.effect.BitterColdEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, KaleidoscopeTheLegendaryChef.MODID);

    public static final RegistryObject<MobEffect> BITTER_COLD = EFFECTS.register("bitter_cold", BitterColdEffect::new);

    private ModEffects() {
    }

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
