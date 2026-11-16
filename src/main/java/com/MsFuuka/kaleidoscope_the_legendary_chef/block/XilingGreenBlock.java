package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.item.XilingGreenItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class XilingGreenBlock extends HorizontalDirectionalBlock {
    public static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 2, 15);
    public static final IntegerProperty CUP_COUNT = IntegerProperty.create("cup_count", 1, 4);
    public static final IntegerProperty TEA_COUNT = IntegerProperty.create("tea_count", 1, 4);
    private static final int MAX_COUNT = 4;

    public XilingGreenBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(CUP_COUNT, 1)
                .setValue(TEA_COUNT, 1)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CUP_COUNT, TEA_COUNT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack itemInHand = player.getItemInHand(hand);
        int teaCount = state.getValue(TEA_COUNT);
        int cupCount = state.getValue(CUP_COUNT);

        Item teapot = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "teapot"));
        if (teapot != null && itemInHand.is(teapot) && teaCount < cupCount) {
            CompoundTag data = itemInHand.getTagElement("BlockEntityTag");
            if (data != null && data.getInt("Status") == 2) {
                ItemStack result = ItemStack.of(data.getCompound("Result"));
                if (!result.isEmpty() && result.getItem() == this.asItem()) {
                    level.setBlock(pos, state.setValue(TEA_COUNT, teaCount + 1), 3);
                    level.playSound(player, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    result.shrink(1);
                    if (result.isEmpty()) {
                        itemInHand.removeTagKey("BlockEntityTag");
                    } else {
                        data.put("Result", result.serializeNBT());
                        itemInHand.getOrCreateTag().put("BlockEntityTag", data);
                    }
                    spawnPourParticles(level, pos);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        Item emptyCup = cookeryEmptyCupItem();
        if (emptyCup != null && itemInHand.is(emptyCup) && cupCount < MAX_COUNT) {
            level.setBlock(pos, state.setValue(CUP_COUNT, cupCount + 1), 3);
            level.playSound(player, pos, getSoundType(state).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            itemInHand.shrink(1);
            return InteractionResult.SUCCESS;
        }

        if (itemInHand.getItem() instanceof XilingGreenItem) {
            if (cupCount < MAX_COUNT) {
                level.setBlock(pos, state.setValue(CUP_COUNT, cupCount + 1).setValue(TEA_COUNT, teaCount + 1), 3);
                level.playSound(player, pos, getSoundType(state).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (teaCount > 0) {
            ItemStack teaStack = new ItemStack((ItemLike) this, 1);
            if (itemInHand.isEmpty()) {
                getItemToLivingEntity(player, teaStack);
            } else {
                Block.popResource(level, pos, teaStack);
            }
            BlockState newState;
            if (cupCount == 1) {
                newState = Blocks.AIR.defaultBlockState();
            } else if (teaCount == 1) {
                Block emptyCupBlock = cookeryEmptyCupBlock();
                if (emptyCupBlock != null) {
                    BlockState emptyState = emptyCupBlock.defaultBlockState();
                    Property<?> cupProp = emptyCupBlock.getStateDefinition().getProperty("cup_count");
                    if (cupProp instanceof IntegerProperty intProp) {
                        newState = emptyState.setValue(intProp, cupCount - 1).setValue(FACING, state.getValue(FACING));
                    } else {
                        newState = state.setValue(CUP_COUNT, cupCount - 1).setValue(TEA_COUNT, 0);
                    }
                } else {
                    newState = state.setValue(CUP_COUNT, cupCount - 1).setValue(TEA_COUNT, 0);
                }
            } else {
                newState = state.setValue(TEA_COUNT, teaCount - 1).setValue(CUP_COUNT, cupCount - 1);
            }
            level.setBlock(pos, newState, 3);
            level.playSound(player, pos, getSoundType(state).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        if (itemInHand.isEmpty() && cupCount > 0) {
            Item empty = cookeryEmptyCupItem();
            if (empty != null) {
                getItemToLivingEntity(player, new ItemStack(empty, 1));
            }
            BlockState newState;
            if (cupCount == 1) {
                newState = Blocks.AIR.defaultBlockState();
            } else {
                newState = state.setValue(CUP_COUNT, cupCount - 1);
            }
            level.setBlock(pos, newState, 3);
            level.playSound(player, pos, getSoundType(state).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static void getItemToLivingEntity(LivingEntity entity, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (entity.getMainHandItem().isEmpty()) {
            RandomSource random = entity.level().random;
            entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
            entity.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else if (entity instanceof Player player) {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        } else {
            ItemEntity drop = entity.spawnAtLocation(stack);
            if (drop != null) {
                drop.setPickUpDelay(0);
            }
        }
    }

    private static void spawnPourParticles(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        RandomSource random = level.random;
        serverLevel.sendParticles(ParticleTypes.CLOUD,
                pos.getX() + 0.5, pos.getY() + 0.35, pos.getZ() + 0.5,
                4, 0.12 + random.nextDouble() * 0.04, 0.08, 0.12 + random.nextDouble() * 0.04, 0.02);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(TEA_COUNT) <= 0) {
            return;
        }
        if (random.nextInt(20) != 0) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        level.addParticle(ParticleTypes.CLOUD,
                x + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                y + random.nextDouble() / 3.0,
                z + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                0.3, 0.1, 0.3);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        int tea = state.getValue(TEA_COUNT);
        int cup = state.getValue(CUP_COUNT);
        int empty = cup - tea;
        Item emptyCupItem = cookeryEmptyCupItem();
        if (empty > 0 && emptyCupItem != null) {
            drops.add(new ItemStack(emptyCupItem, empty));
        }
        if (tea > 0) {
            drops.add(new ItemStack((ItemLike) this, tea));
        }
        return drops;
    }

    private static Item cookeryEmptyCupItem() {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "empty_cup"));
    }

    private static Block cookeryEmptyCupBlock() {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "empty_cup"));
    }
}
