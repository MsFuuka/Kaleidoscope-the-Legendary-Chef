package com.MsFuuka.kaleidoscope_the_legendary_chef.effect;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.PrimalIceNecklaceItem;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModEffects;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeTheLegendaryChef.MODID)
public final class BitterColdEffect extends MobEffect {
    public BitterColdEffect() {
        super(MobEffectCategory.HARMFUL, 0x7FC4E8);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(ModEffects.BITTER_COLD.get())
                || PrimalIceNecklaceItem.isWorn(entity)
                || entity.hasEffect(net.miauczel.legendary_monsters.effect.ModEffects.FREEZE.get())
                || !event.getSource().is(DamageTypes.FREEZE)) {
            return;
        }
        event.setAmount(event.getAmount() * (BitterColdState.isFrostfall(entity) ? 1.30F : 1.20F));
    }
}
