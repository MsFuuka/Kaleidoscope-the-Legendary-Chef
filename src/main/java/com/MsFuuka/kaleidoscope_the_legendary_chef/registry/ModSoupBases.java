package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.client.NoLiquidSoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SimpleSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.Predicate;

public final class ModSoupBases {
    public static final ResourceLocation POWDER_SNOW = ResourceLocation.fromNamespaceAndPath(
            KaleidoscopeTheLegendaryChef.MODID, "powder_snow");
    public static final ResourceLocation PRIMORDIAL_SMOOTHIE = ResourceLocation.fromNamespaceAndPath(
            KaleidoscopeTheLegendaryChef.MODID, "primordial_smoothie");

    private ModSoupBases() {
    }

    public static void register() {
        SoupBaseManager.registerSoupBase(powderSnowBase());
        SoupBaseManager.registerSoupBase(primordialSmoothieBase());
    }

    private static ISoupBase powderSnowBase() {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                "minecraft", "block/powder_snow");
        Predicate<ItemStack> soupBase = stack -> stack.is(Items.POWDER_SNOW_BUCKET);
        Predicate<ItemStack> container = stack -> stack.is(Items.BUCKET);
        TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnContainer =
                (level, entity, stack) -> new ItemStack(Items.BUCKET);
        TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnSoupBase =
                (level, entity, stack) -> new ItemStack(Items.POWDER_SNOW_BUCKET);
        return new SimpleSoupBase(POWDER_SNOW, new ItemStack(Items.POWDER_SNOW_BUCKET),
                texture, 0xBFE6FF, soupBase, container, returnContainer, returnSoupBase) {
            @Override
            public ISoupBaseRender getRender() {
                return new NoLiquidSoupBaseRender(texture);
            }
        };
    }

    private static ISoupBase primordialSmoothieBase() {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                KaleidoscopeTheLegendaryChef.MODID, "block/primordial_smoothie");
        Predicate<ItemStack> soupBase = stack -> stack.is(ModItems.PRIMORDIAL_SMOOTHIE_BUCKET.get());
        Predicate<ItemStack> container = stack -> stack.is(Items.BUCKET);
        TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnContainer =
                (level, entity, stack) -> new ItemStack(Items.BUCKET);
        TriFunction<Level, LivingEntity, ItemStack, ItemStack> returnSoupBase =
                (level, entity, stack) -> new ItemStack(ModItems.PRIMORDIAL_SMOOTHIE_BUCKET.get());
        return new SimpleSoupBase(PRIMORDIAL_SMOOTHIE, new ItemStack(ModItems.PRIMORDIAL_SMOOTHIE_BUCKET.get()),
                texture, 0x9FE8E0, soupBase, container, returnContainer, returnSoupBase) {
            @Override
            public ISoupBaseRender getRender() {
                return new NoLiquidSoupBaseRender(texture);
            }
        };
    }
}
