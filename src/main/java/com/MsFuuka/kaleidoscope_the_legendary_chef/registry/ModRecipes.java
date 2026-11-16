package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.crafting.EverfrostStoveRecipe;
import com.MsFuuka.kaleidoscope_the_legendary_chef.crafting.EverfrostStoveRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final ResourceLocation EVERFROST_STOVE_ID =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "everfrost_stove");

    public static final RecipeType<EverfrostStoveRecipe> EVERFROST_STOVE =
            RecipeType.simple(EVERFROST_STOVE_ID);

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "kaleidoscope_cookery");

    public static final RegistryObject<EverfrostStoveRecipeSerializer> EVERFROST_STOVE_SERIALIZER =
            SERIALIZERS.register("everfrost_stove", EverfrostStoveRecipeSerializer::new);

    private ModRecipes() {
    }

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
