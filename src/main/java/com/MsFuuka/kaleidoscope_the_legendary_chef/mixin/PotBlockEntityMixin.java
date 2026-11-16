package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import java.util.Optional;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IPot;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.MsFuuka.kaleidoscope_the_legendary_chef.crafting.EverfrostStoveRecipe;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModRecipes;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PotBlockEntity.class, remap = false)
public abstract class PotBlockEntityMixin {
    @Shadow
    private int currentTick;

    @Invoker(value = "applyRecipe", remap = false)
    abstract void invokeApplyRecipe(Level level, SimpleContainer container, PotRecipe recipe);

    @Inject(method = "startCooking(Lnet/minecraft/world/level/Level;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$startCooking(Level level, CallbackInfo info) {
        PotBlockEntity pot = (PotBlockEntity) (Object) this;
        if (!everfrost$isEverfrostPot(pot)) {
            return;
        }
        info.cancel();

        SimpleContainer container = pot.getContainer();
        Optional<EverfrostStoveRecipe> match = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.EVERFROST_STOVE).stream()
                .filter(recipe -> "wok".equals(recipe.branch()) && recipe.matches(container, level))
                .findFirst();
        if (match.isEmpty()) {
            return;
        }

        EverfrostStoveRecipe recipe = match.get();
        PotRecipe adapted = new PotRecipe(recipe.getId(), recipe.time(), recipe.stirFryCount(),
                recipe.carrier(), recipe.ingredients(), recipe.result());
        invokeApplyRecipe(level, container, adapted);
    }

    @Inject(method = "addIngredient(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("RETURN"), remap = false)
    private void everfrost$resetOilTimer(Level level, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        PotBlockEntity pot = (PotBlockEntity) (Object) this;
        if (info.getReturnValueZ() && everfrost$isEverfrostPot(pot)) {
            currentTick = 1200;
        }
    }

    @Inject(method = "hasHeatSource(Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$coldSourceCountsAsHeat(Level level, CallbackInfoReturnable<Boolean> info) {
        PotBlockEntity pot = (PotBlockEntity) (Object) this;
        if (level.getBlockState(pot.getBlockPos().below()).is(ModTags.COLD_SOURCE)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "tick(Lnet/minecraft/world/level/Level;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$quietEmptyPot(Level level, CallbackInfo info) {
        PotBlockEntity pot = (PotBlockEntity) (Object) this;
        if (pot.getStatus() != IPot.PUT_INGREDIENT || !pot.isEmpty() || !everfrost$isEverfrostPot(pot)) {
            return;
        }

        if (!level.isClientSide) {
            BlockPos pos = pot.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.getValue(PotBlock.HAS_OIL)) {
                level.setBlockAndUpdate(pos, state.setValue(PotBlock.HAS_OIL, false));
            }
        }
        info.cancel();
    }

    @Inject(method = "onShovelHit(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"), remap = false)
    private void everfrost$sprayOnStirFry(Level level, LivingEntity entity, ItemStack stack, CallbackInfo info) {
        PotBlockEntity pot = (PotBlockEntity) (Object) this;
        if (!(level instanceof ServerLevel server) || !everfrost$isEverfrostPot(pot)) {
            return;
        }

        BlockPos pos = pot.getBlockPos();
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.35;
        double z = pos.getZ() + 0.5;
        server.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 14, 0.35, 0.15, 0.35, 0.05);
        server.sendParticles(ParticleTypes.SMOKE, x, y - 0.05, z, 3, 0.2, 0.1, 0.2, 0.01);
    }

    @Unique
    private boolean everfrost$isEverfrostPot(PotBlockEntity pot) {
        Level level = pot.getLevel();
        return level != null && level.getBlockState(pot.getBlockPos().below()).is(ModTags.COLD_SOURCE);
    }
}
