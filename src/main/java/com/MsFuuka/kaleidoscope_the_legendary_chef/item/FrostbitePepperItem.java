package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class FrostbitePepperItem extends Item {
    public static final int BITTER_COLD_DURATION = 300;

    public FrostbitePepperItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    @Override
    public FoodProperties getFoodProperties() {
        return new FoodProperties.Builder()
                .nutrition(3)
                .saturationMod(0.4F)
                .alwaysEat()
                .effect(() -> new MobEffectInstance(ModEffects.BITTER_COLD.get(), BITTER_COLD_DURATION, 0, false, true), 1.0F)
                .build();
    }
}
