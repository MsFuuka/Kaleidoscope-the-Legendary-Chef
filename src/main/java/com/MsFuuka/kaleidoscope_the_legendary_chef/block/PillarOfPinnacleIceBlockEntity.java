package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PillarOfPinnacleIceBlockEntity extends BlockEntity {
    private static final int FROST_FILL = 80;

    private int age = 0;
    private int shapeKey = -1;
    private float shapeYaw = 0.0F;
    private VoxelShape shape;

    public PillarOfPinnacleIceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PILLAR_OF_PINNACLE_ICE.get(), pos, state);
    }

    public int getAge() {
        return age;
    }

    public VoxelShape getShape(float yaw) {
        if (age >= PillarOfPinnacleIceBlock.LIFE_TICKS + PillarOfPinnacleIceBlock.SINK_TICKS) {
            return Shapes.empty();
        }
        int key = age < PillarOfPinnacleIceBlock.RISE_TICKS ? age
                : age < PillarOfPinnacleIceBlock.LIFE_TICKS ? PillarOfPinnacleIceBlock.RISE_TICKS
                : age;
        if (shape == null || shapeKey != key || shapeYaw != yaw) {
            shape = PillarOfPinnacleIceBlock.shapeFor(key, yaw);
            shapeKey = key;
            shapeYaw = yaw;
        }
        return shape;
    }

    public void tick(boolean clientSide) {
        age++;
        if (clientSide) {
            if (level == null) {
                return;
            }
            if (age <= PillarOfPinnacleIceBlock.RISE_TICKS) {
                spawnRiseParticles();
            }
            spawnFrostParticles();
            return;
        }
        if (age >= PillarOfPinnacleIceBlock.LIFE_TICKS + PillarOfPinnacleIceBlock.SINK_TICKS
                + PillarOfPinnacleIceBlock.FROST_FADE_TICKS) {
            level.removeBlock(getBlockPos(), false);
        }
    }

    private void spawnRiseParticles() {
        BlockPos pos = getBlockPos();
        double height = Math.max(0.0D, PillarOfPinnacleIceBlock.MODEL_HEIGHT + PillarOfPinnacleIceBlock.offsetOf(age));
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.SNOWFLAKE,
                    pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.8D,
                    pos.getY() + level.random.nextDouble() * height,
                    pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.8D,
                    0.0D, 0.04D, 0.0D);
        }
        level.addParticle(ParticleTypes.CLOUD,
                pos.getX() + 0.5D, pos.getY() + 0.05D, pos.getZ() + 0.5D,
                (level.random.nextDouble() - 0.5D) * 0.15D, 0.02D,
                (level.random.nextDouble() - 0.5D) * 0.15D);
        BlockPos below = pos.below();
        BlockState ground = level.getBlockState(below);
        if (!ground.isAir()) {
            BlockParticleOption debris = new BlockParticleOption(ParticleTypes.BLOCK, ground);
            for (int i = 0; i < 24; i++) {
                double angle = level.random.nextDouble() * Math.PI * 2.0D;
                double radius = level.random.nextDouble() * 0.8D;
                double x = pos.getX() + 0.5D + Math.cos(angle) * radius;
                double z = pos.getZ() + 0.5D + Math.sin(angle) * radius;
                level.addParticle(debris, x, pos.getY() + 0.1D + level.random.nextDouble() * 0.15D, z,
                        Math.cos(angle) * 0.18D, 0.1D + level.random.nextDouble() * 0.16D,
                        Math.sin(angle) * 0.18D);
            }
        }
    }

    private void spawnFrostParticles() {
        int rampEnd = PillarOfPinnacleIceBlock.RISE_TICKS + PillarOfPinnacleIceBlock.FROST_RAMP_TICKS;
        int fadeStart = PillarOfPinnacleIceBlock.LIFE_TICKS + PillarOfPinnacleIceBlock.SINK_TICKS;
        int fadeEnd = fadeStart + PillarOfPinnacleIceBlock.FROST_FADE_TICKS;
        if (age < PillarOfPinnacleIceBlock.RISE_TICKS || age >= fadeEnd) {
            return;
        }
        float intensity;
        if (age < rampEnd) {
            intensity = (float) (age - PillarOfPinnacleIceBlock.RISE_TICKS) / PillarOfPinnacleIceBlock.FROST_RAMP_TICKS;
        } else if (age <= fadeStart) {
            intensity = 1.0F;
        } else {
            intensity = 1.0F - (float) (age - fadeStart) / PillarOfPinnacleIceBlock.FROST_FADE_TICKS;
        }
        BlockPos pos = getBlockPos();
        double cx = pos.getX() + 0.5D;
        double cz = pos.getZ() + 0.5D;
        double outer = PillarOfPinnacleIceBlock.FROST_RADIUS;
        int count = Math.round(FROST_FILL * intensity);
        double phase = age * 0.012D;
        for (int i = 0; i < count; i++) {
            double radius = outer * Math.sqrt((i + 0.5D) / FROST_FILL);
            double angle = (double) i * 2.39996323D + phase;
            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;
            double y = groundYAt(x, z);
            int pick = Math.floorMod(i * 37, 100);
            if (pick < 78) {
                level.addParticle(ParticleTypes.SNOWFLAKE, x, y + 0.03D, z, 0.0D, 0.002D, 0.0D);
            } else {
                level.addParticle(ParticleTypes.CLOUD, x, y + 0.02D, z, 0.0D, 0.004D, 0.0D);
            }
        }
    }

    private double groundYAt(double x, double z) {
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);
        int base = getBlockPos().getY() - 1;
        for (int dy = 8; dy >= 0; dy--) {
            BlockPos p = new BlockPos(bx, base + dy, bz);
            if (level.getBlockState(p).isCollisionShapeFullBlock(level, p)) {
                return p.getY() + 1.02D;
            }
        }
        return base + 1.02D;
    }
}
