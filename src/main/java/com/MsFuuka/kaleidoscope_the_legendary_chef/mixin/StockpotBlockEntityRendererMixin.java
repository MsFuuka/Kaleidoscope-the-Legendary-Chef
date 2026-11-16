package com.MsFuuka.kaleidoscope_the_legendary_chef.mixin;

import java.util.function.Function;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.client.NoLiquidSoupBaseRender;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModTags;
import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.StockpotBlockEntityRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(StockpotBlockEntityRender.class)
public abstract class StockpotBlockEntityRendererMixin {
    private static final ISoupBaseRender FALLBACK_RENDER = new NoLiquidSoupBaseRender(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, "block/primordial_smoothie"));

    @Inject(method = "renderItems(Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/kitchen/StockpotBlockEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void everfrost$skipCookeryItems(StockpotBlockEntity entity, PoseStack pose, MultiBufferSource buffer,
                                            int light, int overlay, boolean finished, CallbackInfo info) {
        if (entity.getLevel() != null
                && entity.getLevel().getBlockState(entity.getBlockPos().below()).is(ModTags.COLD_SOURCE)) {
            NoLiquidSoupBaseRender.renderInputs(entity, pose, buffer, light, overlay, 0.38F);
            info.cancel();
        }
    }

    @Redirect(method = "render(Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/kitchen/StockpotBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false)
    private Object everfrost$fallbackSoupBaseRender(Function<Object, Object> function, Object id) {
        Object result = id == null ? null : function.apply(id);
        return result == null ? FALLBACK_RENDER : result;
    }
}
