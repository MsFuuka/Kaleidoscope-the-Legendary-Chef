package com.MsFuuka.kaleidoscope_the_legendary_chef.registry;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    public static final TagKey<Block> COLD_SOURCE = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, "cold_source"));

    public static final TagKey<Item> PICKAXES = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("forge", "tools/pickaxes"));

    private ModTags() {
    }
}
