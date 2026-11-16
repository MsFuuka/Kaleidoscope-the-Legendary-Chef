package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import java.util.WeakHashMap;
import java.util.function.Consumer;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.FrozenMalletBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.client.FrozenMalletRenderer;
import com.MsFuuka.kaleidoscope_the_legendary_chef.network.MalletStatePacket;
import com.MsFuuka.kaleidoscope_the_legendary_chef.network.ModMessages;
import com.MsFuuka.kaleidoscope_the_legendary_chef.network.PillarOfPinnacleIcePacket;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.network.PacketDistributor;

public final class FrozenMalletItem extends Item {
    private static final int MAX_SPEED_STACKS = 6;
    private static final double BASE_SPEED_VALUE = 0.5D;
    private static final double MAX_SPEED_VALUE = 1.2D;
    private static final double SPEED_PER_STACK = (MAX_SPEED_VALUE - BASE_SPEED_VALUE) / MAX_SPEED_STACKS;
    private static final double BASE_DAMAGE_MOD = 7.0D;
    private static final String SPEED_STACKS_TAG = "mallet_speed_stacks";
    private static final String LAST_ATTACK_TAG = "mallet_last_attack";
    private static final int SPEED_BAR_COLOR = 0x4AB7E8;
    static final long SPEED_GRACE_TICKS = 120L;
    static final long SPEED_DECAY_STEP = 20L;
    public static final WeakHashMap<Player, Float> MALLET_ATTACK_STRENGTH = new WeakHashMap<>();

    private static float getAttackStrength(Player player) {
        Float value = MALLET_ATTACK_STRENGTH.get(player);
        return value == null ? 0.0F : value;
    }

    public FrozenMalletItem(Properties properties) {
        super(properties);
    }

    public static int getSpeedStacks(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : tag.getInt(SPEED_STACKS_TAG);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return barStacks(stack) > 0 || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int stacks = barStacks(stack);
        if (stacks > 0) {
            return Math.max(1, Math.round(13.0F * (float) stacks / (float) MAX_SPEED_STACKS));
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return barStacks(stack) > 0 ? SPEED_BAR_COLOR : super.getBarColor(stack);
    }

    private static int barStacks(ItemStack stack) {
        int stacks = getSpeedStacks(stack);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            AttributeInstance atk = mc.player.getAttribute(Attributes.ATTACK_SPEED);
            if (atk != null) {
                int derived = levelForSpeedValue(atk.getValue());
                if (derived > stacks) {
                    stacks = derived;
                }
            }
        }
        return stacks;
    }

    public static int getMaxSpeedStacks() {
        return MAX_SPEED_STACKS;
    }

    public static void setSpeedStacks(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(SPEED_STACKS_TAG, value);
    }

    public static long getLastAttack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0L : tag.getLong(LAST_ATTACK_TAG);
    }

    public static void setLastAttack(ItemStack stack, long value) {
        stack.getOrCreateTag().putLong(LAST_ATTACK_TAG, value);
    }

    public static double speedModifierFor(int level) {
        return -3.5D + SPEED_PER_STACK * level;
    }

    public static double speedValueFor(int level) {
        return BASE_SPEED_VALUE + SPEED_PER_STACK * level;
    }

    public static int levelForSpeedValue(double value) {
        int level = (int) Math.round((value - BASE_SPEED_VALUE) / SPEED_PER_STACK);
        return level >= 0 && level <= MAX_SPEED_STACKS
                && Math.abs(value - speedValueFor(level)) < 0.02D ? level : -1;
    }

    public static void applySpeed(Player player, int level) {
        AttributeInstance speed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (speed != null) {
            speed.removeModifier(Item.BASE_ATTACK_SPEED_UUID);
            speed.addTransientModifier(new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID,
                    "Weapon modifier", speedModifierFor(level), AttributeModifier.Operation.ADDITION));
        }
    }

    public static void applyDamage(Player player) {
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(Item.BASE_ATTACK_DAMAGE_UUID);
            damage.addTransientModifier(new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID,
                    "Weapon modifier", BASE_DAMAGE_MOD, AttributeModifier.Operation.ADDITION));
        }
    }

    public static void applyAll(Player player, int level) {
        applyDamage(player);
        applySpeed(player, level);
    }

    public static void syncState(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new MalletStatePacket(getSpeedStacks(stack), stack.getDamageValue()));
        }
    }

    public static void removeModifiers(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (speed != null) {
            speed.removeModifier(Item.BASE_ATTACK_SPEED_UUID);
        }
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(Item.BASE_ATTACK_DAMAGE_UUID);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND) {
            return ImmutableMultimap.of();
        }
        double speed = speedModifierFor(getSpeedStacks(stack));
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", BASE_DAMAGE_MOD, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", speed, AttributeModifier.Operation.ADDITION))
                .build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        if (attacker instanceof Player player && !player.level().isClientSide()) {
            int level = getSpeedStacks(stack);
            if (level < MAX_SPEED_STACKS) {
                float strength = getAttackStrength(player);
                if (strength >= 0.9F) {
                    level = level + 1;
                    setSpeedStacks(stack, level);
                    setLastAttack(stack, player.level().getGameTime());
                    applyAll(player, level);
                }
            }
            syncState(player, stack);
        }
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return FrozenMalletRenderer.get();
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player) || !level.isClientSide()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null) {
            return;
        }
        BlockPos pillarPos = PillarOfPinnacleIceBlock.resolveAim(level, player, mc.hitResult, mc.gameMode.getPickRange(), 1.0F);
        if (pillarPos != null) {
            ModMessages.INSTANCE.sendToServer(new PillarOfPinnacleIcePacket(pillarPos, player.getYRot()));
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown() || context.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).canBeReplaced()) {
            pos = pos.above();
        }

        if (!level.getBlockState(pos).canBeReplaced()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            setSpeedStacks(context.getItemInHand(), 0);
            setLastAttack(context.getItemInHand(), 0L);
            removeModifiers(player);
            level.setBlock(pos, ModBlocks.FROZEN_MALLET.get().defaultBlockState()
                    .setValue(FrozenMalletBlock.FACING, context.getHorizontalDirection().getOpposite()), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 0.9F);
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
