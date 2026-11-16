package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class AmbientLoopSound extends AbstractTickableSoundInstance {
    private static final float FADE_STEP = 0.1F;

    private float target;

    AmbientLoopSound(SoundEvent event, BlockPos pos) {
        super(event, SoundSource.BLOCKS, RandomSource.create());
        this.looping = true;
        this.delay = 0;
        this.volume = 0F;
        this.pitch = 1.0F;
        this.relative = false;
        this.attenuation = SoundInstance.Attenuation.NONE;
        setPos(pos);
    }

    void setPos(BlockPos pos) {
        this.x = pos.getX() + 0.5D;
        this.y = pos.getY() + 0.5D;
        this.z = pos.getZ() + 0.5D;
    }

    void setTarget(float value) {
        this.target = value;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        this.volume += (this.target - this.volume) * FADE_STEP;
    }
}
