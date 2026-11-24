package com.MsFuuka.kaleidoscope_the_legendary_chef.item;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class PrimalIceNecklaceItem extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<UUID> WEARERS = ConcurrentHashMap.newKeySet();

    public PrimalIceNecklaceItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity instanceof Player player && !player.level().isClientSide) {
            if (WEARERS.add(player.getUUID())) {
                LOGGER.info("[Kaleidoscope] Necklace immunity active for {}", player.getName().getString());
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack oldStack) {
        LivingEntity entity = slotContext.entity();
        if (entity instanceof Player player && !player.level().isClientSide) {
            if (WEARERS.remove(player.getUUID())) {
                LOGGER.info("[Kaleidoscope] Necklace immunity removed for {}", player.getName().getString());
            }
        }
    }

    public static boolean isWorn(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (WEARERS.contains(player.getUUID())) {
            return true;
        }
        return CuriosApi.getCuriosInventory(player)
                .map(inventory -> inventory.isEquipped(ModItems.PRIMAL_ICE_NECKLACE.get()))
                .orElse(false);
    }
}
