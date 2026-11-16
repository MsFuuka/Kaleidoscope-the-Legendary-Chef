package com.MsFuuka.kaleidoscope_the_legendary_chef.crafting;

import java.util.ArrayList;
import java.util.List;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;

public record EverfrostStoveRecipe(ResourceLocation id, int time, int stirFryCount,
                                  Ingredient carrier, NonNullList<Ingredient> ingredients,
                                  ItemStack result, String branch, ResourceLocation base) implements Recipe<SimpleContainer> {

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        return items.size() == ingredients.size()
                && RecipeMatcher.findMatches(items, ingredients) != null;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EVERFROST_STOVE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.EVERFROST_STOVE;
    }
}
