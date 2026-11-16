package com.MsFuuka.kaleidoscope_the_legendary_chef.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;

public final class EverfrostStoveRecipeSerializer implements RecipeSerializer<EverfrostStoveRecipe> {

    @Override
    public EverfrostStoveRecipe fromJson(ResourceLocation id, JsonObject json) {
        int time = GsonHelper.getAsInt(json, "time", 200);
        int stirFryCount = GsonHelper.getAsInt(json, "stir_fry_count", 3);
        String branch = GsonHelper.getAsString(json, "branch", "wok");
        ResourceLocation base = json.has("base")
                ? ResourceLocation.tryParse(GsonHelper.getAsString(json, "base")) : null;
        Ingredient carrier = json.has("carrier")
                ? Ingredient.fromJson(json.get("carrier"))
                : Ingredient.EMPTY;

        JsonArray array = GsonHelper.getAsJsonArray(json, "ingredients");
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (JsonElement element : array) {
            ingredients.add(Ingredient.fromJson(element));
        }

        ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true, true);
        return new EverfrostStoveRecipe(id, time, stirFryCount, carrier, ingredients, result, branch, base);
    }

    @Override
    public EverfrostStoveRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        int time = buffer.readVarInt();
        int stirFryCount = buffer.readVarInt();
        String branch = buffer.readUtf();
        ResourceLocation base = buffer.readBoolean() ? buffer.readResourceLocation() : null;
        Ingredient carrier = Ingredient.fromNetwork(buffer);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            ingredients.add(Ingredient.fromNetwork(buffer));
        }
        return new EverfrostStoveRecipe(id, time, stirFryCount, carrier, ingredients,
                buffer.readItem(), branch, base);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, EverfrostStoveRecipe recipe) {
        buffer.writeVarInt(recipe.time());
        buffer.writeVarInt(recipe.stirFryCount());
        buffer.writeUtf(recipe.branch());
        if (recipe.base() != null) {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(recipe.base());
        } else {
            buffer.writeBoolean(false);
        }
        recipe.carrier().toNetwork(buffer);
        buffer.writeVarInt(recipe.ingredients().size());
        for (Ingredient ingredient : recipe.ingredients()) {
            ingredient.toNetwork(buffer);
        }
        buffer.writeItem(recipe.result());
    }
}
