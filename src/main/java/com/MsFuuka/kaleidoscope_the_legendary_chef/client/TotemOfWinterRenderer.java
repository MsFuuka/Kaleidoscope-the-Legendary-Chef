package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.entity.TotemOfWinterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TotemOfWinterRenderer extends EntityRenderer<TotemOfWinterEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            KaleidoscopeTheLegendaryChef.MODID, "textures/entity/totem_of_winter.png");
    private static final ResourceLocation CRACKED_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            KaleidoscopeTheLegendaryChef.MODID, "textures/entity/totem_of_winter_cracked.png");

    private final TotemOfWinterModel<TotemOfWinterEntity> model;

    public TotemOfWinterRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TotemOfWinterModel<>(context.bakeLayer(TotemOfWinterModel.LAYER_LOCATION));
    }

    @Override
    public void render(TotemOfWinterEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
        float wobble = (float) (entity.level().getGameTime() - entity.lastHit) + partialTicks;
        if (wobble < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(wobble / 1.5F * (float) Math.PI) * 3.0F));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        ResourceLocation texture = entity.isCracked() ? CRACKED_TEXTURE : TEXTURE;
        VertexConsumer consumer = buffers.getBuffer(this.model.renderType(texture));
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TotemOfWinterEntity entity) {
        return entity.isCracked() ? CRACKED_TEXTURE : TEXTURE;
    }
}
