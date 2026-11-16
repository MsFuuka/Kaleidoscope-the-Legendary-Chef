package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import com.MsFuuka.kaleidoscope_the_legendary_chef.crafting.EverfrostStoveRecipe;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModRecipes;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModTags;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.StockpotMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotVisuals;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;
import java.util.Optional;

public class StockpotRecipeEventHandler {

    @SubscribeEvent
    public void onPre(StockpotMatchRecipeEvent.Pre event) {
        if (!isEverfrostStockpot(event)) {
            return;
        }
        Optional<EverfrostStoveRecipe> match = findOurRecipe(event);
        if (match.isPresent()) {
            event.setOutput(toStockpotRecipe(match.get(), event.getLevel()));
        } else if (event.isCancelable()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPost(StockpotMatchRecipeEvent.Post event) {
        if (!isEverfrostStockpot(event)) {
            return;
        }
        Optional<EverfrostStoveRecipe> match = findOurRecipe(event);
        match.ifPresent(recipe -> event.setOutput(toStockpotRecipe(recipe, event.getLevel())));
    }

    private boolean isEverfrostStockpot(StockpotMatchRecipeEvent event) {
        Level level = event.getLevel();
        return level.getBlockState(event.getStockpot().getBlockPos().below()).is(ModTags.COLD_SOURCE);
    }

    private Optional<EverfrostStoveRecipe> findOurRecipe(StockpotMatchRecipeEvent event) {
        StockpotContainer container = event.getContainer();
        if (container.getSoupBase() == null) {
            return Optional.empty();
        }
        SimpleContainer inventory = new SimpleContainer(container.getItems().toArray(new ItemStack[0]));
        return event.getLevel().getRecipeManager()
                .getAllRecipesFor(ModRecipes.EVERFROST_STOVE).stream()
                .filter(r -> "stockpot".equals(r.branch())
                        && Objects.equals(r.base(), container.getSoupBase())
                        && r.matches(inventory, event.getLevel()))
                .findFirst();
    }

    private StockpotRecipe toStockpotRecipe(EverfrostStoveRecipe recipe, Level level) {
        return new StockpotRecipe(recipe.getId(), recipe.ingredients(), recipe.base(),
                recipe.getResultItem(level.registryAccess()), recipe.time(),
                recipe.carrier(), StockpotVisuals.DEFAULT);
    }
}
