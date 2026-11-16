package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModSounds;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModTags;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID, value = Dist.CLIENT)
public final class EverfrostStoveAmbience {
    private static final int SCAN_INTERVAL = 5;
    private static final int RADIUS = 12;
    private static final int VERTICAL = 5;
    private static final float MAX_VOLUME = 0.25F;
    private static final float STOVE_WHILE_COOKING = 0.05F;
    private static final float STOVE_WHILE_COOKING_LIDDED = 0.15F;
    private static final float MAX_POT_VOLUME = 0.45F;
    private static final float MAX_POT_VOLUME_LIDDED = 0.2F;
    private static final double FULL_DISTANCE = 3.0D;
    private static final double MAX_DISTANCE = 12.0D;

    private static AmbientLoopSound stoveSound;
    private static AmbientLoopSound potSound;
    private static int timer;

    private EverfrostStoveAmbience() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Player player = mc.player;
        SoundManager manager = mc.getSoundManager();
        if (level == null || player == null) {
            stoveSound = stop(manager, stoveSound);
            potSound = stop(manager, potSound);
            return;
        }
        if (++timer < SCAN_INTERVAL) {
            return;
        }
        timer = 0;
        Scan scan = scan(level, player.blockPosition());
        boolean cooking = scan.pot() != null;
        float stoveMax = !cooking ? MAX_VOLUME
                : (scan.lidded() ? STOVE_WHILE_COOKING_LIDDED : STOVE_WHILE_COOKING);
        float potMax = scan.lidded() ? MAX_POT_VOLUME_LIDDED : MAX_POT_VOLUME;
        float stoveVolume = scan.stove() == null ? 0F
                : attenuation(distance(player, scan.stove())) * stoveMax;
        float potVolume = scan.pot() == null ? 0F
                : attenuation(distance(player, scan.pot())) * potMax;
        stoveSound = apply(manager, stoveSound, scan.stove(), stoveVolume, ModSounds.EVERFROST_STOVE.get());
        potSound = apply(manager, potSound, scan.pot(), potVolume, ModSounds.FROZEN.get());
    }

    private static AmbientLoopSound apply(SoundManager manager, AmbientLoopSound sound, BlockPos pos,
                                          float target, SoundEvent event) {
        if (target <= 0F) {
            if (sound == null) {
                return null;
            }
            sound.setTarget(0F);
            if (sound.getVolume() <= 0.003F) {
                manager.stop(sound);
                return null;
            }
            return sound;
        }
        if (sound == null) {
            sound = new AmbientLoopSound(event, pos);
            manager.play(sound);
        } else {
            sound.setPos(pos);
        }
        sound.setTarget(target);
        return sound;
    }

    private static AmbientLoopSound stop(SoundManager manager, AmbientLoopSound sound) {
        if (sound != null) {
            manager.stop(sound);
        }
        return null;
    }

    private static Scan scan(Level level, BlockPos origin) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos stove = null;
        BlockPos pot = null;
        boolean lidded = false;
        double stoveDist = Double.MAX_VALUE;
        double potDist = Double.MAX_VALUE;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                for (int dy = -VERTICAL; dy <= VERTICAL; dy++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    BlockState state = level.getBlockState(cursor);
                    double d = (double) dx * dx + (double) dy * dy + (double) dz * dz;
                    if (d < stoveDist && state.is(ModBlocks.EVERFROST_STOVE.get())) {
                        stoveDist = d;
                        stove = cursor.immutable();
                    }
                    if (d < potDist && state.getBlock() instanceof StockpotBlock && isCooking(level, cursor)) {
                        potDist = d;
                        pot = cursor.immutable();
                        lidded = level.getBlockEntity(cursor) instanceof StockpotBlockEntity stockpot
                                && stockpot.hasLid();
                    }
                }
            }
        }
        return new Scan(stove, pot, lidded);
    }

    private static boolean isCooking(Level level, BlockPos pos) {
        if (!level.getBlockState(pos.below()).is(ModTags.COLD_SOURCE)) {
            return false;
        }
        return level.getBlockEntity(pos) instanceof StockpotBlockEntity pot && pot.getStatus() == 2;
    }

    private static float attenuation(double dist) {
        double t = 1.0D - (dist - FULL_DISTANCE) / (MAX_DISTANCE - FULL_DISTANCE);
        t = Math.max(0.0D, Math.min(1.0D, t));
        return (float) (t * t * (3.0D - 2.0D * t));
    }

    private static double distance(Player player, BlockPos pos) {
        double dx = player.getX() - (pos.getX() + 0.5D);
        double dy = player.getY() - (pos.getY() + 0.5D);
        double dz = player.getZ() - (pos.getZ() + 0.5D);
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private record Scan(BlockPos stove, BlockPos pot, boolean lidded) {
    }
}
