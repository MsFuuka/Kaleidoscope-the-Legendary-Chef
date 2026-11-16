package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import java.util.WeakHashMap;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class FrostbiteDamage {
    private static final float FROSTBITE = 4.0F;
    private static final float CRIT_MULTIPLIER = 1.5F;
    private static final WeakHashMap<Player, Boolean> CRITICAL = new WeakHashMap<>();

    private FrostbiteDamage() {
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (!player.getMainHandItem().is(ModItems.FROZEN_MALLET.get())) {
            return;
        }
        CRITICAL.put(player, event.isVanillaCritical());
        event.setDamageModifier(1.0F);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.is(DamageTypes.FREEZE)
                || !source.is(DamageTypes.PLAYER_ATTACK)
                || !(source.getEntity() instanceof Player player)
                || !player.getMainHandItem().is(ModItems.FROZEN_MALLET.get())) {
            return;
        }

        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }

        boolean critical = Boolean.TRUE.equals(CRITICAL.remove(player));
        float amount = critical ? FROSTBITE * CRIT_MULTIPLIER : FROSTBITE;
        int cooldown = target.invulnerableTime;
        target.invulnerableTime = 0;
        target.hurt(target.damageSources().freeze(), amount);
        target.invulnerableTime = cooldown;
    }
}
