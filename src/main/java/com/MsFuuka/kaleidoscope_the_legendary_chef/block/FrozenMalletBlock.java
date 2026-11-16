package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import java.util.function.Consumer;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

@SuppressWarnings("deprecation")
public final class FrozenMalletBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final int LAST_STAGE = 5;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, LAST_STAGE);
    private static final double[][][] PARTS = {
            {{7.000, 1.000, 7.000, 9.000, 7.000, 9.000}, {6.000, 0.000, 6.000, 10.000, 2.000, 10.000}, {4.464, 5.379, 6.500, 11.536, 12.450, 9.500}, {0.222, 5.379, 8.000, 5.879, 11.036, 8.000}, {10.121, 5.379, 8.000, 15.778, 11.036, 8.000}, {5.500, 8.000, 8.000, 10.500, 30.000, 8.000}, {2.500, 9.850, 2.500, 13.500, 34.850, 13.500}, {3.000, 10.350, 3.000, 13.000, 34.350, 13.000}},
            {{7.000, 1.000, 7.000, 9.000, 7.000, 9.000}, {6.000, 0.000, 6.000, 10.000, 2.000, 10.000}, {4.464, 5.379, 6.500, 11.536, 12.450, 9.500}, {0.222, 5.379, 8.000, 5.879, 11.036, 8.000}, {10.121, 5.379, 8.000, 15.778, 11.036, 8.000}, {5.500, 8.000, 8.000, 10.500, 30.000, 8.000}, {3.500, 9.850, 3.500, 12.500, 34.850, 12.500}, {4.000, 10.350, 4.000, 12.000, 34.350, 12.000}},
            {{7.000, 1.000, 7.000, 9.000, 7.000, 9.000}, {6.000, 0.000, 6.000, 10.000, 2.000, 10.000}, {4.464, 5.379, 6.500, 11.536, 12.450, 9.500}, {0.222, 5.379, 8.000, 5.879, 11.036, 8.000}, {10.121, 5.379, 8.000, 15.778, 11.036, 8.000}, {5.500, 8.000, 8.000, 10.500, 30.000, 8.000}, {4.500, 9.850, 4.500, 11.500, 34.850, 11.500}, {5.000, 10.350, 5.000, 11.000, 34.350, 11.000}},
            {{7.000, 1.000, 7.000, 9.000, 7.000, 9.000}, {6.000, 0.000, 6.000, 10.000, 2.000, 10.000}, {4.464, 5.379, 6.500, 11.536, 12.450, 9.500}, {0.222, 5.379, 8.000, 5.879, 11.036, 8.000}, {10.121, 5.379, 8.000, 15.778, 11.036, 8.000}, {5.500, 8.000, 8.000, 10.500, 30.000, 8.000}, {4.500, 9.850, 4.500, 11.500, 26.850, 11.500}, {5.000, 10.350, 5.000, 11.000, 26.350, 11.000}},
            {{7.000, 1.000, 7.000, 9.000, 7.000, 9.000}, {6.000, 0.000, 6.000, 10.000, 2.000, 10.000}, {4.464, 5.379, 6.500, 11.536, 12.450, 9.500}, {0.222, 5.379, 8.000, 5.879, 11.036, 8.000}, {10.121, 5.379, 8.000, 15.778, 11.036, 8.000}, {5.500, 8.000, 8.000, 10.500, 30.000, 8.000}, {4.500, 9.850, 4.500, 11.500, 19.850, 11.500}, {5.000, 10.350, 5.000, 11.000, 19.350, 11.000}},
            {{7.000, 1.000, 7.000, 9.000, 7.000, 9.000}, {6.000, 0.000, 6.000, 10.000, 2.000, 10.000}, {4.464, 5.379, 6.500, 11.536, 12.450, 9.500}, {0.222, 5.379, 8.000, 5.879, 11.036, 8.000}, {10.121, 5.379, 8.000, 15.778, 11.036, 8.000}, {5.500, 8.000, 8.000, 10.500, 30.000, 8.000}}
    };
    private static final double MAX_COLLISION_Y = 31.9;
    private static final int DESTROY_PARTICLES = 32;
    private static final SoundType FINAL_STAGE_SOUND = new SoundType(
            SoundType.GLASS.getVolume(), SoundType.GLASS.getPitch(), SoundEvents.METAL_BREAK,
            SoundType.GLASS.getStepSound(), SoundType.GLASS.getPlaceSound(),
            SoundType.GLASS.getHitSound(), SoundType.GLASS.getFallSound());
    private static final float[] HEIGHT_SCALES = new float[PARTS.length];
    private static final VoxelShape[] SHAPES = new VoxelShape[PARTS.length * 4];

    static {
        for (int stage = 0; stage < PARTS.length; stage++) {
            double top = 0.0D;
            for (double[] part : PARTS[stage]) {
                if (part[0] == part[3] || part[1] == part[4] || part[2] == part[5]) {
                    continue;
                }
                top = Math.max(top, part[4]);
            }
            float scale = (float) (Math.min(MAX_COLLISION_Y, top) / top);
            HEIGHT_SCALES[stage] = scale;
            for (int facing = 0; facing < 4; facing++) {
                VoxelShape shape = Shapes.empty();
                for (double[] part : PARTS[stage]) {
                    if (part[0] == part[3] || part[1] == part[4] || part[2] == part[5]) {
                        continue;
                    }
                    double[] box = turn(part, facing);
                    shape = Shapes.or(shape, Block.box(box[0], box[1] * scale, box[2],
                            box[3], box[4] * scale, box[5]));
                }
                SHAPES[stage * 4 + facing] = shape.optimize();
            }
        }
    }

    public FrozenMalletBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(STAGE, 0));
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {
            @Override
            public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
                if (!(level instanceof ClientLevel clientLevel)) {
                    return false;
                }
                int stage = state.getValue(STAGE);
                double[][] parts = PARTS[stage];
                float scale = HEIGHT_SCALES[stage];
                int facing = facingIndex(state.getValue(FACING));
                for (int i = 0; i < DESTROY_PARTICLES; i++) {
                    double[] box = turn(parts[i % parts.length], facing);
                    double x = pos.getX() + (box[0] + level.random.nextDouble() * (box[3] - box[0])) / 16.0D;
                    double y = pos.getY() + (box[1] + level.random.nextDouble() * (box[4] - box[1])) * scale / 16.0D;
                    double z = pos.getZ() + (box[2] + level.random.nextDouble() * (box[5] - box[2])) / 16.0D;
                    manager.add(new TerrainParticle(clientLevel, x, y, z, 0.0D, 0.02D, 0.0D, state)
                            .updateSprite(state, pos));
                }
                return true;
            }
        });
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        return state.getValue(STAGE) == LAST_STAGE ? FINAL_STAGE_SOUND : getSoundType(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STAGE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FrozenMalletBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.FROZEN_MALLET.get());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(STAGE) * 4 + facingIndex(state.getValue(FACING))];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(STAGE) * 4 + facingIndex(state.getValue(FACING))];
    }

    public static float heightScale(int stage) {
        return HEIGHT_SCALES[stage];
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int stage = state.getValue(STAGE);
        if (stage == LAST_STAGE) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
            for (int i = 0; i < 8; i++) {
                level.addParticle(particle,
                        pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6,
                        pos.getY() + 0.75 + level.random.nextDouble() * 0.75,
                        pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6,
                        0.0, 0.02, 0.0);
            }
        } else {
            level.setBlock(pos, state.setValue(STAGE, stage + 1), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static double[] turn(double[] part, int facing) {
        return switch (facing) {
            case 1 -> new double[]{16.0 - part[5], part[1], part[0], 16.0 - part[2], part[4], part[3]};
            case 2 -> new double[]{16.0 - part[3], part[1], 16.0 - part[5], 16.0 - part[0], part[4], 16.0 - part[2]};
            case 3 -> new double[]{part[2], part[1], 16.0 - part[3], part[5], part[4], 16.0 - part[0]};
            default -> part;
        };
    }

    private static int facingIndex(Direction facing) {
        return switch (facing) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
    }
}
