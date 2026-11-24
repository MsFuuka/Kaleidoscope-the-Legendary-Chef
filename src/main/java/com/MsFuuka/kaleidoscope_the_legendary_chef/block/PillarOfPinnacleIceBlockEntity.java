package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.effect.BitterColdState;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public final class PillarOfPinnacleIceBlockEntity extends BlockEntity {
    private static final int FROSTFALL_FILL = 80;

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
            spawnFrostfallParticles();
            return;
        }
        if (age >= PillarOfPinnacleIceBlock.LIFE_TICKS + PillarOfPinnacleIceBlock.SINK_TICKS
                + PillarOfPinnacleIceBlock.FROSTFALL_FADE_TICKS) {
            level.removeBlock(getBlockPos(), false);
        } else if (age >= PillarOfPinnacleIceBlock.RISE_TICKS && level instanceof ServerLevel server) {
            applyCold(server);
            extinguishFires(server);
        }
    }

    private void extinguishFires(ServerLevel level) {
        int ramp = PillarOfPinnacleIceBlock.FROSTFALL_RAMP_TICKS;
        double radius = PillarOfPinnacleIceBlock.FROSTFALL_RADIUS
                * Math.min(1.0D, (double) (age - PillarOfPinnacleIceBlock.RISE_TICKS) / ramp);
        BlockPos center = getBlockPos();
        int reach = (int) Math.ceil(PillarOfPinnacleIceBlock.FROSTFALL_RADIUS);
        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                if (Math.sqrt((double) dx * dx + (double) dz * dz) > radius) {
                    continue;
                }
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(p);
                    if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private void applyCold(ServerLevel level) {
        BlockPos center = getBlockPos();
        double radius = PillarOfPinnacleIceBlock.FROSTFALL_RADIUS;
        AABB box = new AABB(center).inflate(radius, radius, radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity entity : entities) {
            if (PillarOfPinnacleIceBlock.inFrostfallRange(entity, center)) {
                BitterColdState.applyPillar(entity);
            }
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

    private void spawnFrostfallParticles() {
        int rampEnd = PillarOfPinnacleIceBlock.RISE_TICKS + PillarOfPinnacleIceBlock.FROSTFALL_RAMP_TICKS;
        int fadeStart = PillarOfPinnacleIceBlock.LIFE_TICKS + PillarOfPinnacleIceBlock.SINK_TICKS;
        int fadeEnd = fadeStart + PillarOfPinnacleIceBlock.FROSTFALL_FADE_TICKS;
        if (age < PillarOfPinnacleIceBlock.RISE_TICKS || age >= fadeEnd) {
            return;
        }
        float intensity;
        if (age < rampEnd) {
            intensity = (float) (age - PillarOfPinnacleIceBlock.RISE_TICKS) / PillarOfPinnacleIceBlock.FROSTFALL_RAMP_TICKS;
        } else if (age <= fadeStart) {
            intensity = 1.0F;
        } else {
            intensity = 1.0F - (float) (age - fadeStart) / PillarOfPinnacleIceBlock.FROSTFALL_FADE_TICKS;
        }
        BlockPos pos = getBlockPos();
        double cx = pos.getX() + 0.5D;
        double cz = pos.getZ() + 0.5D;
        double outer = PillarOfPinnacleIceBlock.FROSTFALL_RADIUS;
        int count = Math.round(FROSTFALL_FILL * intensity);
        double phase = age * 0.012D;
        for (int i = 0; i < count; i++) {
            double radius = outer * Math.sqrt((i + 0.5D) / FROSTFALL_FILL);
            double angle = (double) i * 2.39996323D + phase;
            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;
            double y = groundYAt(x, z);
            int scatter = Math.floorMod(i * i * 41 + i * 17, 100);
            if (radius <= 1.4D || scatter < 13) {
                level.addParticle(ParticleTypes.CLOUD, x, y + 0.02D, z, 0.0D, 0.004D, 0.0D);
            } else {
                level.addParticle(ParticleTypes.SNOWFLAKE, x, y + 0.03D, z, 0.0D, 0.002D, 0.0D);
            }
        }
    }

    private double groundYAt(double x, double z) {
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);
        int base = getBlockPos().getY() - 1;
        for (int dy = 1; dy >= -1; dy--) {
            BlockPos p = new BlockPos(bx, base + dy, bz);
            if (level.getBlockState(p).isCollisionShapeFullBlock(level, p)) {
                return p.getY() + 1.02D;
            }
        }
        return base + 1.02D;
    }
}
