package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.XilingGreenBlock;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public final class XilingGreenItem extends BlockItem {
    private final List<Pair<Supplier<MobEffectInstance>, Float>> effects;
    private final List<MobEffectInstance> showEffects = new ArrayList<>();

    public XilingGreenItem(Block block, List<Pair<Supplier<MobEffectInstance>, Float>> effects) {
        super(block, new Item.Properties().stacksTo(16));
        this.effects = effects;
        for (Pair<Supplier<MobEffectInstance>, Float> effect : effects) {
            if (effect.getSecond() >= 1.0F) {
                showEffects.add(effect.getFirst().get());
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        addTeaEffect(level, entity);
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return returnContainer(stack, level, entity);
    }

    private void addTeaEffect(Level level, LivingEntity entity) {
        for (Pair<Supplier<MobEffectInstance>, Float> entry : effects) {
            MobEffectInstance instance = entry.getFirst().get();
            float probability = entry.getSecond();
            if (level.isClientSide || !(level.random.nextFloat() < probability)) {
                continue;
            }
            entity.addEffect(instance);
        }
    }

    private ItemStack returnContainer(ItemStack stack, Level level, LivingEntity entity) {
        Item emptyCup = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "empty_cup"));
        if (emptyCup != Items.AIR && stack.isEmpty()) {
            return new ItemStack(emptyCup);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(this);
        if (id != null) {
            String key = "tooltip.%s.%s.maxim".formatted(id.getNamespace(), id.getPath());
            Component full = Component.translatable(key);
            String text = full.getString();
            for (String line : text.split("\n")) {
                if (!line.isEmpty()) {
                    tooltip.add(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                } else {
                    tooltip.add(CommonComponents.NEW_LINE);
                }
            }
        }
        if (!showEffects.isEmpty()) {
            tooltip.add(CommonComponents.EMPTY);
            PotionUtils.addPotionTooltip(showEffects, tooltip, 1.0F);
        }
    }
}
