package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import java.util.Optional;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.crafting.EverfrostStoveRecipe;
import com.MsFuuka.kaleidoscope_the_legendary_chef.integration.StockpotCarrierAccess;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModTags;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotVisuals;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StockpotBlockEntity.class, remap = false)
public abstract class StockpotBlockEntityMixin implements StockpotCarrierAccess {

    private static final ResourceLocation STOCKPOT_ITEM = ResourceLocation.fromNamespaceAndPath(
            "kaleidoscope_cookery", "stockpot");

    private ResourceLocation everfrost$recipeId;
    private boolean everfrost$productOnly;

    @Override
    public boolean everfrost$showsProductOnly() {
        if (everfrost$productOnly) {
            return true;
        }
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        Level level = pot.getLevel();
        if (level == null) {
            return false;
        }
        return findOwnRecipe(level, everfrost$recipeId)
                .map(recipe -> !isContainerCarrier(recipe.carrier())).orElse(false);
    }

    @Inject(method = "hasHeatSource(Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$coldSourceAsHeat(Level level, CallbackInfoReturnable<Boolean> info) {
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        BlockState below = level.getBlockState(pot.getBlockPos().below());
        if (below.is(ModTags.COLD_SOURCE)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "addSoupBase(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$restrictSoupBase(Level level, LivingEntity entity, ItemStack stack,
                                            CallbackInfoReturnable<Boolean> info) {
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        BlockState below = level.getBlockState(pot.getBlockPos().below());
        boolean onStove = below.is(ModTags.COLD_SOURCE);
        boolean ourBase = stack.is(ModItems.PRIMORDIAL_SMOOTHIE_BUCKET.get()) || stack.is(Items.POWDER_SNOW_BUCKET);
        if (onStove && ourBase) {
            return;
        }
        if (onStove || ourBase) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(method = "applySuspiciousRecipe", at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$blockSuspiciousRecipe(CallbackInfo info) {
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        Level level = pot.getLevel();
        if (level == null) {
            return;
        }
        BlockState below = level.getBlockState(pot.getBlockPos().below());
        if (below.is(ModTags.COLD_SOURCE)) {
            info.cancel();
        }
    }

    @Inject(method = "spawnParticleWithoutLid(Lnet/minecraft/world/level/Level;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$noBubbleWithoutLid(Level level, CallbackInfo info) {
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        if (level.getBlockState(pot.getBlockPos().below()).is(ModTags.COLD_SOURCE)) {
            info.cancel();
        }
    }

    @Inject(method = "spawnParticleWithLid(Lnet/minecraft/world/level/Level;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$noBubbleWithLid(Level level, CallbackInfo info) {
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        if (level.getBlockState(pot.getBlockPos().below()).is(ModTags.COLD_SOURCE)) {
            info.cancel();
        }
    }

    @Redirect(method = "tick(Lnet/minecraft/world/level/Level;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"),
            remap = true)
    private void everfrost$muteBoilSound(Level instance, Player player, double x, double y, double z,
                                         SoundEvent sound, SoundSource category, float volume, float pitch) {
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        Level level = pot.getLevel();
        if (level != null && level.getBlockState(pot.getBlockPos().below()).is(ModTags.COLD_SOURCE)) {
            return;
        }
        instance.playSound(player, x, y, z, sound, category, volume, pitch);
    }

    @Inject(method = "getRecipeById(Lnet/minecraft/world/level/Level;Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/datafixers/util/Either;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$resolveOwnRecipe(Level level, ResourceLocation id,
                                             CallbackInfoReturnable<Either<StockpotRecipe, FlexStockpotRecipe>> info) {
        if (id == null || !id.getNamespace().equals(KaleidoscopeTheLegendaryChef.MODID)) {
            return;
        }
        Optional<EverfrostStoveRecipe> own = findOwnRecipe(level, id);
        if (own.isEmpty()) {
            return;
        }
        EverfrostStoveRecipe recipe = own.get();
        everfrost$recipeId = id;
        everfrost$productOnly = !isContainerCarrier(recipe.carrier());
        StockpotBlockEntity pot = (StockpotBlockEntity) (Object) this;
        StockpotVisuals visuals = pot.visuals == null ? StockpotVisuals.DEFAULT : pot.visuals;
        StockpotRecipe resolved = new StockpotRecipe(id, recipe.ingredients(), recipe.base(),
                recipe.result(), recipe.time(), recipe.carrier(), visuals);
        info.setReturnValue(Either.left(resolved));
        info.cancel();
    }

    private Optional<EverfrostStoveRecipe> findOwnRecipe(Level level, ResourceLocation id) {
        if (level == null || id == null || !id.getNamespace().equals(KaleidoscopeTheLegendaryChef.MODID)) {
            return Optional.empty();
        }
        return level.getRecipeManager().byKey(id)
                .filter(EverfrostStoveRecipe.class::isInstance)
                .map(EverfrostStoveRecipe.class::cast);
    }

    private static boolean isContainerCarrier(Ingredient carrier) {
        for (ItemStack stack : carrier.getItems()) {
            if (stack.is(Items.BOWL)
                    || BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(STOCKPOT_ITEM)) {
                return true;
            }
        }
        return false;
    }
}
