package com.MsFuuka.kaleidoscope_the_legendary_chef.integration;

import java.util.Set;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.entity.TotemOfWinterEntity;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class MalletAttackSound {
    private static final Set<SoundEvent> SWINGS = Set.of(
            SoundEvents.PLAYER_ATTACK_STRONG,
            SoundEvents.PLAYER_ATTACK_WEAK,
            SoundEvents.PLAYER_ATTACK_CRIT,
            SoundEvents.PLAYER_ATTACK_KNOCKBACK,
            SoundEvents.PLAYER_ATTACK_SWEEP);

    private static final double TOTEM_REACH = 4.5D;

    private MalletAttackSound() {
    }

    @SubscribeEvent
    public static void onPlaySound(PlayLevelSoundEvent event) {
        Holder<SoundEvent> sound = event.getSound();
        if (sound == null || !SWINGS.contains(sound.value())
                || !(event instanceof PlayLevelSoundEvent.AtPosition at)) {
            return;
        }

        Vec3 pos = at.getPosition();
        Level level = event.getLevel();
        Player player = level.getNearestPlayer(pos.x, pos.y, pos.z, 2.0D, false);
        if (player != null && player.getMainHandItem().is(ModItems.FROZEN_MALLET.get())) {
            if (isAttackingTotem(player)) {
                event.setCanceled(true);
            } else {
                event.setSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSounds.MALLET_SMASH.get()));
            }
        }
    }

    private static boolean isAttackingTotem(Player player) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F).normalize();
        AABB area = player.getBoundingBox().move(look.scale(TOTEM_REACH)).inflate(1.5D);
        for (Entity entity : player.level().getEntities(player, area, e -> e instanceof TotemOfWinterEntity)) {
            Vec3 to = entity.position().subtract(eye);
            double dist = to.length();
            if (dist > 0.001D && to.normalize().dot(look) > 0.5D && dist < TOTEM_REACH) {
                return true;
            }
        }
        return false;
    }
}
