package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.entity.TotemOfWinterEntity;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class TotemWinterAura {
    private static final ResourceLocation CHILI_CROP =
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "chili_crop");
    private static final int RADIUS = 3;
    private static final int MIN_Y = -1;
    private static final int MAX_Y = 2;
    private static final int FREEZE_PARTICLES = 12;
    private static final List<List<Cell>> SHELLS = buildShells();

    private TotemWinterAura() {
    }

    public static int shellCount() {
        return SHELLS.size();
    }

    public static void applyWave(ServerLevel level, BlockPos center, int shell) {
        for (Cell cell : SHELLS.get(Math.floorMod(shell, SHELLS.size()))) {
            convert(level, center.offset(cell.dx(), cell.dy(), cell.dz()));
        }
    }

    private static void convert(ServerLevel level, BlockPos pos) {
        Block chiliCrop = ForgeRegistries.BLOCKS.getValue(CHILI_CROP);
        BlockState state = level.getBlockState(pos);
        if (chiliCrop != null && state.is(chiliCrop)) {
            int age = state.getValue(BlockStateProperties.AGE_7);
            level.setBlock(pos, ModBlocks.FROSTBITE_PEPPER_CROP.get().defaultBlockState()
                    .setValue(BlockStateProperties.AGE_7, age), 2);
        } else if (state.is(Blocks.WATER)) {
            level.setBlock(pos, ModBlocks.WINTER_POWDER_SNOW.get().defaultBlockState(), 2);
        }
    }

    public static void applyHostileEffects(ServerLevel level, BlockPos center) {
        AABB box = new AABB(center).inflate(RADIUS, 2.0D, RADIUS);
        MobEffect freeze = ModEffects.FREEZE.get();
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (!living.isAlive() || !(living instanceof Enemy)) {
                continue;
            }
            if (living instanceof Skeleton skeleton) {
                if (skeleton.getHealth() / skeleton.getMaxHealth() < 0.25F) {
                    skeleton.convertTo(EntityType.STRAY, true);
                    level.levelEvent(1048, skeleton.blockPosition(), 0);
                    level.sendParticles(ParticleTypes.SNOWFLAKE, skeleton.getX(),
                            skeleton.getY() + skeleton.getBbHeight() * 0.5D, skeleton.getZ(), FREEZE_PARTICLES,
                            skeleton.getBbWidth() * 0.5D, skeleton.getBbHeight() * 0.4D,
                            skeleton.getBbWidth() * 0.5D, 0.02D);
                    continue;
                }
                if (skeleton.getEffect(freeze) == null) {
                    skeleton.addEffect(new MobEffectInstance(freeze, 100, 0));
                    level.sendParticles(ParticleTypes.SNOWFLAKE, skeleton.getX(),
                            skeleton.getY() + skeleton.getBbHeight() * 0.5D, skeleton.getZ(), FREEZE_PARTICLES,
                            skeleton.getBbWidth() * 0.5D, skeleton.getBbHeight() * 0.4D,
                            skeleton.getBbWidth() * 0.5D, 0.02D);
                }
                continue;
            }
            if (!living.canFreeze()) {
                continue;
            }
            if (living.getEffect(freeze) == null) {
                living.addEffect(new MobEffectInstance(freeze, 100, 0));
                level.sendParticles(ParticleTypes.SNOWFLAKE, living.getX(),
                        living.getY() + living.getBbHeight() * 0.5D, living.getZ(), FREEZE_PARTICLES,
                        living.getBbWidth() * 0.5D, living.getBbHeight() * 0.4D,
                        living.getBbWidth() * 0.5D, 0.02D);
            }
        }
    }

    public static boolean inWinterZone(Level level, BlockPos pos) {
        AABB box = new AABB(pos).inflate(RADIUS, 2.0D, RADIUS);
        for (Entity entity : level.getEntitiesOfClass(TotemOfWinterEntity.class, box)) {
            if (Math.abs(entity.getX() - pos.getX() - 0.5D) <= RADIUS + 0.5D
                    && Math.abs(entity.getZ() - pos.getZ() - 0.5D) <= RADIUS + 0.5D
                    && Math.abs(entity.getY() - pos.getY() - 0.5D) <= 2.5D) {
                return true;
            }
        }
        return false;
    }

    private static List<List<Cell>> buildShells() {
        List<Cell> cells = new ArrayList<>();
        int maxShell = 0;
        for (int dy = MIN_Y; dy <= MAX_Y; dy++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    int horizontal = Math.abs(dx) + Math.abs(dz);
                    int vertical = Math.abs(dy);
                    int shell = dy == 0 ? horizontal : horizontal + vertical - 1;
                    cells.add(new Cell(dx, dy, dz, shell));
                    maxShell = Math.max(maxShell, shell);
                }
            }
        }

        cells.sort(Comparator.comparingInt(Cell::shell)
                .thenComparing(cell -> cell.dy() == 0 ? 1 : 0)
                .thenComparingInt(Cell::dy)
                .thenComparingInt(Cell::dx)
                .thenComparingInt(Cell::dz));

        List<List<Cell>> shells = new ArrayList<>();
        for (int i = 0; i <= maxShell; i++) {
            shells.add(new ArrayList<>());
        }
        for (Cell cell : cells) {
            shells.get(cell.shell()).add(cell);
        }
        return Collections.unmodifiableList(shells);
    }

    private record Cell(int dx, int dy, int dz, int shell) {
    }
}
