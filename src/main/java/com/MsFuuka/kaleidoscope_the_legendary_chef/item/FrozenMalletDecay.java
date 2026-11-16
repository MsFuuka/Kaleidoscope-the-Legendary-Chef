package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import java.util.WeakHashMap;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class FrozenMalletDecay {

    private FrozenMalletDecay() {
    }

    private static final WeakHashMap<Player, Boolean> APPLIED = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof FrozenMalletItem mallet) {
            int level = FrozenMalletItem.getSpeedStacks(stack);
            if (!APPLIED.containsKey(player)) {
                FrozenMalletItem.applyAll(player, level);
                APPLIED.put(player, Boolean.TRUE);
            }
            if (level <= 0) {
                return;
            }
            long now = player.level().getGameTime();
            long last = FrozenMalletItem.getLastAttack(stack);
            if (last == 0L) {
                FrozenMalletItem.setLastAttack(stack, now);
                return;
            }
            long elapsed = now - last;
            if (elapsed <= FrozenMalletItem.SPEED_GRACE_TICKS) {
                return;
            }
            long overdue = elapsed - FrozenMalletItem.SPEED_GRACE_TICKS;
            int toRemove = (int) Math.min((long) level, overdue / FrozenMalletItem.SPEED_DECAY_STEP);
            if (toRemove <= 0) {
                return;
            }
            int newLevel = level - toRemove;
            FrozenMalletItem.setSpeedStacks(stack, newLevel);
            FrozenMalletItem.setLastAttack(stack, last + toRemove * FrozenMalletItem.SPEED_DECAY_STEP);
            FrozenMalletItem.applySpeed(player, newLevel);
            FrozenMalletItem.syncState(player, stack);
        } else if (APPLIED.containsKey(player)) {
            FrozenMalletItem.removeModifiers(player);
            APPLIED.remove(player);
        }
    }
}
