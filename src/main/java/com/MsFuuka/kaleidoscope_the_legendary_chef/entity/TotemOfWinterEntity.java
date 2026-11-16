package com.MsFuuka.kaleidoscope_the_legendary_chef.entity;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.TotemWinterAura;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModSounds;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class TotemOfWinterEntity extends Entity {
    private static final int HITS_TO_BREAK = 2;
    private static final int HIT_COOLDOWN = 5;
    private static final int RESET_TICKS = 100;
    private static final int WAVE_INTERVAL = 12;
    private static final int HIT_PARTICLES = 32;
    private static final double STORM_SPIN = 0.13D;
    private static final double STORM_RADIUS = 0.72D;
    private static final int STORM_ARMS = 3;
    private static final BlockParticleOption ICE_PARTICLE =
            new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());
    private static final EntityDataAccessor<Boolean> CRACKED =
            SynchedEntityData.defineId(TotemOfWinterEntity.class, EntityDataSerializers.BOOLEAN);

    public long lastHit = -5L;

    private int hits;
    private int lastHitTick = -HIT_COOLDOWN;
    private int waveStep;
    private int waveTimer;

    public TotemOfWinterEntity(EntityType<? extends TotemOfWinterEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CRACKED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.spawnStormParticles();
            return;
        }
        if (++this.waveTimer >= WAVE_INTERVAL) {
            this.waveTimer = 0;
            TotemWinterAura.applyWave((ServerLevel) this.level(), this.blockPosition(), this.waveStep);
            this.waveStep++;
        }
        TotemWinterAura.applyHostileEffects((ServerLevel) this.level(), this.blockPosition());
        if (this.hits >  0 && this.tickCount - this.lastHitTick > RESET_TICKS) {
            this.hits = 0;
            this.entityData.set(CRACKED, false);
        }
    }

    public boolean isCracked() {
        return this.entityData.get(CRACKED);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(ModItems.TOTEM_OF_WINTER.get());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        }
        if (!(source.getEntity() instanceof Player player)) {
            return false;
        }
        this.playHitSound();
        this.lastHit = this.level().getGameTime();
        this.level().broadcastEntityEvent(this, (byte) 32);
        boolean creative = player.isCreative();
        if (!creative && !player.getMainHandItem().is(ModTags.PICKAXES)) {
            return false;
        }
        if (this.tickCount - this.lastHitTick < HIT_COOLDOWN) {
            return true;
        }
        this.lastHitTick = this.tickCount;
        this.spawnHitParticles();
        if (creative) {
            this.discard();
            return true;
        }
        this.hits++;
        if (this.hits < HITS_TO_BREAK) {
            this.entityData.set(CRACKED, true);
            return true;
        }
        this.spawnAtLocation(new ItemStack(ModItems.TOTEM_OF_WINTER.get()));
        this.discard();
        return true;
    }

    private void playHitSound() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Holder<SoundEvent> sound = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSounds.TOTEM_HIT.get());
        ClientboundSoundPacket packet = new ClientboundSoundPacket(sound, SoundSource.BLOCKS,
                this.getX(), this.getY() + 1.0D, this.getZ(), 1.8F, 1.0F, serverLevel.getRandom().nextLong());
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (player.serverLevel() == serverLevel
                    && player.distanceToSqr(this.getX(), this.getY(), this.getZ()) < 1024.0D) {
                player.connection.send(packet);
            }
        }
    }

    private void spawnStormParticles() {
        Level level = this.level();
        double spin = this.tickCount * STORM_SPIN;
        double rise = Math.sin(this.tickCount * 0.045D) * 0.65D;
        for (int arm = 0; arm < STORM_ARMS; arm++) {
            double angle = spin + arm * Math.PI * 2.0D / STORM_ARMS;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double radius = STORM_RADIUS + Math.sin(spin * 0.5D + arm) * 0.1D;
            double x = this.getX() + cos * radius;
            double y = this.getY() + 1.05D + rise + arm * 0.16D;
            double z = this.getZ() + sin * radius;
            level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z,
                    -sin * 0.02D, 0.01D, cos * 0.02D);
            if ((this.tickCount + arm) % 3 == 0) {
                level.addParticle(ICE_PARTICLE, x, y + 0.08D, z,
                        -sin * 0.03D, 0.02D, cos * 0.03D);
            }
        }
    }

    private void spawnHitParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK,
                    Blocks.STONE_BRICKS.defaultBlockState());
            serverLevel.sendParticles(particle, this.getX(), this.getY() + 1.0D, this.getZ(),
                    HIT_PARTICLES, 0.6D, 1.0D, 0.6D, 0.06D);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 32) {
            if (this.level().isClientSide) {
                this.lastHit = this.level().getGameTime();
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
