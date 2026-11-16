package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import java.util.ArrayList;
import java.util.List;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlockEntities;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PillarOfPinnacleIceBlock extends Block implements EntityBlock {
    public static final int RISE_TICKS = 5;
    public static final int LIFE_TICKS = 120;
    public static final int SINK_TICKS = 20;
    public static final double FROST_RADIUS = 3.0D;
    public static final int FROST_RAMP_TICKS = 30;
    public static final int FROST_FADE_TICKS = 40;
    public static final float MODEL_SCALE = 2.0F;
    public static final float STABLE_PROGRESS = 0.9F;
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    private static final double LAUNCH_SPEED = 0.685D;
    private static final double LAUNCH_HORIZONTAL = 0.25D;

    private static final double[][] CUBES = {
            {-3.7493D, -16.9291D, -4.6581D, 3.2507D, 4.0709D, 2.3419D},
            {-6.7493D, -5.9291D, -5.6581D, -2.7493D, 4.0709D, -1.6581D},
            {-6.7493D, -8.9291D, 0.3419D, -2.7493D, 4.0709D, 4.3419D},
            {1.2507D, -8.9291D, -5.6581D, 5.2507D, 4.0709D, -1.6581D},
            {1.2507D, -5.9291D, 0.3419D, 5.2507D, 4.0709D, 4.3419D}
    };
    private static final int[] CORNERS = {0, 1, 5, 4};
    private static final double[] EXTENTS = extents();
    public static final double GROUND_LIFT = liftOf();
    public static final double MODEL_HEIGHT = modelHeight();

    public PillarOfPinnacleIceBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ROTATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    public static float yawOf(BlockState state) {
        return state.hasProperty(ROTATION) ? state.getValue(ROTATION) * 45.0F : 0.0F;
    }

    private static double[] extents() {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (double[] cube : CUBES) {
            minX = Math.min(minX, cube[0]);
            maxX = Math.max(maxX, cube[3]);
            minZ = Math.min(minZ, cube[2]);
            maxZ = Math.max(maxZ, cube[5]);
        }
        double factor = 0.7D * MODEL_SCALE / 16.0D;
        return new double[]{
                (maxX - minX) * factor,
                (maxZ - minZ) * factor,
                -((minX + maxX) / 2.0D + 0.9955D) * factor,
                ((minZ + maxZ) / 2.0D + 0.7706D) * factor
        };
    }

    private static double liftOf() {
        float partY = stablePartY();
        double lowest = 2.0D;
        for (double[] cube : CUBES) {
            lowest = Math.min(lowest, heightOf(cube[4], partY));
        }
        return Math.max(0.0D, -lowest);
    }

    private static double modelHeight() {
        float partY = stablePartY();
        double highest = -2.0D;
        for (double[] cube : CUBES) {
            highest = Math.max(highest, heightOf(cube[1], partY));
        }
        return highest + GROUND_LIFT;
    }

    public static float stablePartY() {
        return 24.0F - (STABLE_PROGRESS + Mth.sin(STABLE_PROGRESS * 2.7F)) * 7.2F;
    }

    public static float horizontalScaleOf(float yaw) {
        double angle = Math.toRadians(90.0D - yaw);
        double cos = Math.abs(Math.cos(angle));
        double sin = Math.abs(Math.sin(angle));
        double width = Math.max(cos * EXTENTS[0] + sin * EXTENTS[1], sin * EXTENTS[0] + cos * EXTENTS[1]);
        return (float) Math.min(1.0D, 1.0D / width);
    }

    public static double[] horizontalOffsetOf(float yaw) {
        float scale = horizontalScaleOf(yaw);
        double angle = Math.toRadians(90.0D - yaw);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new double[]{
                scale * (EXTENTS[2] * cos + EXTENTS[3] * sin),
                scale * (-EXTENTS[2] * sin + EXTENTS[3] * cos)
        };
    }

    public static double offsetOf(float elapsed) {
        if (elapsed < RISE_TICKS) {
            return -MODEL_HEIGHT * (1.0D - Mth.clamp(elapsed / (float) RISE_TICKS, 0.0F, 1.0F));
        }
        if (elapsed <= LIFE_TICKS) {
            return 0.0D;
        }
        return -MODEL_HEIGHT * Mth.clamp((elapsed - LIFE_TICKS) / (float) SINK_TICKS, 0.0F, 1.0F);
    }

    public static VoxelShape shapeFor(float elapsed, float yaw) {
        double offsetY = offsetOf(elapsed);
        float scaleXZ = MODEL_SCALE * horizontalScaleOf(yaw);
        float partY = stablePartY();
        double angle = Math.toRadians(90.0D - yaw);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double[] offset = horizontalOffsetOf(yaw);
        List<AABB> boxes = new ArrayList<>();
        List<double[]> edges = new ArrayList<>();
        for (double[] cube : CUBES) {
            double[] px = new double[4];
            double[] pz = new double[4];
            for (int k = 0; k < 4; k++) {
                int i = CORNERS[k];
                double mx = (i & 1) == 0 ? cube[0] : cube[3];
                double mz = (i & 4) == 0 ? cube[2] : cube[5];
                double cx = -scaleXZ * 0.7D * (mx + 0.9955D) / 16.0D;
                double cz = scaleXZ * 0.7D * (mz + 0.7706D) / 16.0D;
                px[k] = Mth.clamp(cx * cos + cz * sin + 0.5D - offset[0], 0.0D, 1.0D);
                pz[k] = Mth.clamp(-cx * sin + cz * cos + 0.5D - offset[1], 0.0D, 1.0D);
            }
            double heightA = heightOf(cube[1], partY) + GROUND_LIFT + offsetY;
            double heightB = heightOf(cube[4], partY) + GROUND_LIFT + offsetY;
            double minY = Mth.clamp(Math.min(heightA, heightB), 0.0D, 2.0D);
            double maxY = Mth.clamp(Math.max(heightA, heightB), 0.0D, 2.0D);
            if (maxY <= minY) {
                continue;
            }
            for (int k = 0; k < 4; k++) {
                int n = (k + 1) & 3;
                edges.add(new double[]{px[k], minY, pz[k], px[n], minY, pz[n]});
                edges.add(new double[]{px[k], maxY, pz[k], px[n], maxY, pz[n]});
                edges.add(new double[]{px[k], minY, pz[k], px[k], maxY, pz[k]});
            }
            double cx = (px[0] + px[1] + px[2] + px[3]) / 4.0D;
            double cz = (pz[0] + pz[1] + pz[2] + pz[3]) / 4.0D;
            double halfX = (cube[3] - cube[0]) * 0.7D * scaleXZ / 32.0D;
            double halfZ = (cube[5] - cube[2]) * 0.7D * scaleXZ / 32.0D;
            double x0 = Mth.clamp(cx - halfX, 0.0D, 1.0D);
            double x1 = Mth.clamp(cx + halfX, 0.0D, 1.0D);
            double z0 = Mth.clamp(cz - halfZ, 0.0D, 1.0D);
            double z1 = Mth.clamp(cz + halfZ, 0.0D, 1.0D);
            if (x1 > x0 && z1 > z0) {
                boxes.add(new AABB(x0, minY, z0, x1, maxY, z1));
            }
        }
        return PillarOfPinnacleIceOutlineShape.of(boxes, edges.toArray(new double[0][]));
    }

    private static double heightOf(double my, float partY) {
        return -MODEL_SCALE * (0.7D * (my + partY) / 16.0D - 0.626D);
    }

    private static VoxelShape shapeAt(BlockState state, BlockGetter level, BlockPos pos) {
        float yaw = yawOf(state);
        if (level.getBlockEntity(pos) instanceof PillarOfPinnacleIceBlockEntity entity) {
            return entity.getShape(yaw);
        }
        return shapeFor(RISE_TICKS, yaw);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PillarOfPinnacleIceBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.PILLAR_OF_PINNACLE_ICE.get()) {
            return null;
        }
        return level.isClientSide()
                ? (lvl, pos, st, be) -> ((PillarOfPinnacleIceBlockEntity) be).tick(true)
                : (lvl, pos, st, be) -> ((PillarOfPinnacleIceBlockEntity) be).tick(false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeAt(state, level, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = level.getBlockEntity(pos) instanceof PillarOfPinnacleIceBlockEntity entity ? entity.getAge() : RISE_TICKS;
        if (age < RISE_TICKS) {
            return Shapes.empty();
        }
        double top = Math.max(0.0D, MODEL_HEIGHT + offsetOf(age));
        if (top <= 0.0D) {
            return Shapes.empty();
        }
        return Shapes.create(new AABB(0.0D, 0.0D, 0.0D, 1.0D, top, 1.0D));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    public static boolean isValidTarget(Level level, BlockPos aim) {
        return resolvePillarPos(level, aim) != null;
    }

    public static BlockPos resolveAim(Level level, Player player, HitResult crosshair, double reach, float partialTick) {
        if (crosshair instanceof EntityHitResult entityHit) {
            return resolvePillarPos(level, entityHit.getEntity().blockPosition());
        }
        HitResult hit = player.pick(reach, partialTick, false);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() == HitResult.Type.MISS) {
            return null;
        }
        return resolvePillarPos(level, blockHit.getBlockPos());
    }

    public static BlockPos resolvePillarPos(Level level, BlockPos aim) {
        BlockState aimState = level.getBlockState(aim);
        BlockPos ground;
        if (aimState.isCollisionShapeFullBlock(level, aim)) {
            ground = aim;
        } else if (aimState.isAir() || aimState.canBeReplaced()) {
            ground = aim.below();
        } else {
            return null;
        }
        if (!level.getBlockState(ground).isCollisionShapeFullBlock(level, ground)) {
            return null;
        }
        BlockPos pillarPos = ground.above();
        if (!canPillarColumn(level, pillarPos)) {
            return null;
        }
        return pillarPos;
    }

    public static boolean placeFrom(Level level, BlockPos aim, float yaw) {
        BlockPos pillarPos = resolvePillarPos(level, aim);
        if (pillarPos == null) {
            return false;
        }
        breakAndClear(level, pillarPos);
        breakAndClear(level, pillarPos.above());
        int rotation = Math.floorMod(Math.round(yaw / 45.0F), 8);
        level.setBlock(pillarPos, ModBlocks.PILLAR_OF_PINNACLE_ICE.get().defaultBlockState().setValue(ROTATION, rotation), Block.UPDATE_ALL);
        launchEntities(level, pillarPos);
        playPillarSound(level, pillarPos);
        return true;
    }

    private static void launchEntities(Level level, BlockPos pos) {
        double centerX = pos.getX() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos.offset(1, 2, 1)))) {
            if (entity instanceof Enemy) {
                double dx = entity.getX() - centerX;
                double dz = entity.getZ() - centerZ;
                double length = Math.sqrt(dx * dx + dz * dz);
                if (length < 1.0E-4D) {
                    dx = 1.0D;
                    dz = 0.0D;
                    length = 1.0D;
                }
                entity.setDeltaMovement(dx / length * LAUNCH_HORIZONTAL, LAUNCH_SPEED, dz / length * LAUNCH_HORIZONTAL);
            } else {
                entity.setDeltaMovement(0.0D, LAUNCH_SPEED, 0.0D);
            }
            entity.setOnGround(false);
            entity.hurtMarked = true;
            entity.hasImpulse = true;
        }
    }

    private static void playPillarSound(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        Holder<SoundEvent> sound = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSounds.FREEZE.get());
        ClientboundSoundPacket packet = new ClientboundSoundPacket(sound, SoundSource.BLOCKS,
                x, y, z, 1.5F, 0.9F, serverLevel.getRandom().nextLong());
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (player.serverLevel() == serverLevel && player.distanceToSqr(x, y, z) < 1024.0D) {
                player.connection.send(packet);
            }
        }
    }

    private static boolean canPillarColumn(Level level, BlockPos base) {
        for (int i = 0; i < 2; i++) {
            BlockPos p = base.above(i);
            BlockState s = level.getBlockState(p);
            if (s.isCollisionShapeFullBlock(level, p)) {
                return false;
            }
            if (!level.getFluidState(p).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void breakAndClear(Level level, BlockPos p) {
        BlockState s = level.getBlockState(p);
        if (s.isAir()) {
            return;
        }
        if (s.isCollisionShapeFullBlock(level, p)) {
            return;
        }
        level.levelEvent(2001, p, Block.getId(s));
        SoundType sound = s.getSoundType();
        level.playSound(null, p, sound.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }
}
