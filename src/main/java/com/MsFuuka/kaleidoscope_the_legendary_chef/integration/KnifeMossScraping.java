package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class KnifeMossScraping {
    private static final ResourceLocation COLOSSUS =
            ResourceLocation.fromNamespaceAndPath("legendary_monsters", "overgrown_colossus");
    private static final ResourceLocation GOLEM =
            ResourceLocation.fromNamespaceAndPath("legendary_monsters", "mossy_golem");
    private static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("forge", "tools/knives"));
    private static final String SCRAPED_KEY = "kaleidoscope_the_legendary_chef.moss_scraped";
    private static final String COOLDOWN_KEY = "kaleidoscope_the_legendary_chef.moss_cooldown";
    private static final int MAX_SCRAP = 3;
    private static final int COOLDOWN_TICKS = 4097;

    private KnifeMossScraping() {
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (!COLOSSUS.equals(ForgeRegistries.ENTITY_TYPES.getKey(target.getType()))) {
            return;
        }
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        if (!(attacker instanceof Player player)) {
            return;
        }
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!held.is(KNIVES) || event.getAmount() <= 0.0F) {
            return;
        }
        CompoundTag data = target.getPersistentData();
        if (data.getInt(SCRAPED_KEY) >= MAX_SCRAP) {
            return;
        }
        if (level.random.nextFloat() < 0.40F) {
            data.putInt(SCRAPED_KEY, data.getInt(SCRAPED_KEY) + 1);
            target.spawnAtLocation(new ItemStack(ModItems.TENDER_GREEN_MOSS.get()));
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Level level = target.level();
        if (level.isClientSide) {
            return;
        }
        if (!GOLEM.equals(ForgeRegistries.ENTITY_TYPES.getKey(target.getType()))) {
            return;
        }
        Player player = event.getEntity();
        if (!player.getItemInHand(event.getHand()).is(KNIVES)) {
            return;
        }
        CompoundTag data = target.getPersistentData();
        long now = level.getGameTime();
        if (now - data.getLong(COOLDOWN_KEY) < COOLDOWN_TICKS) {
            return;
        }
        data.putLong(COOLDOWN_KEY, now);
        int count = 1 + level.random.nextInt(2);
        target.spawnAtLocation(new ItemStack(ModItems.TENDER_GREEN_MOSS.get(), count));
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
