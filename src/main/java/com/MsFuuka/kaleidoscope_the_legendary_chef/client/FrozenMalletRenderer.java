package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.FrozenMalletBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;

public final class FrozenMalletRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation ICON = id("item/frozen_mallet");
    private static final ResourceLocation MODEL = id("block/frozen_mallet/frozen_mallet");
    private static FrozenMalletRenderer instance;

    public FrozenMalletRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static FrozenMalletRenderer get() {
        if (instance == null) {
            instance = new FrozenMalletRenderer();
        }
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        Minecraft minecraft = Minecraft.getInstance();
        if (context == ItemDisplayContext.GUI || context == ItemDisplayContext.GROUND) {
            TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ICON);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));
            if (context == ItemDisplayContext.GROUND) {
                pose.pushPose();
                pose.translate(0.25F, 0.125F, 0.5F);
                pose.scale(0.5F, 0.5F, 1.0F);
                quad(consumer, pose.last(), sprite, light, overlay);
                pose.popPose();
            } else {
                quad(consumer, pose.last(), sprite, light, overlay);
            }
            return;
        }
        BakedModel model = minecraft.getModelManager().getModel(MODEL);
        pose.pushPose();
        pose.scale(2.0F, 2.0F * FrozenMalletBlock.heightScale(0), 2.0F);
        minecraft.getBlockRenderer().getModelRenderer().renderModel(pose.last(), buffer.getBuffer(RenderType.cutout()), null, model,
                1.0F, 1.0F, 1.0F, light, overlay, ModelData.EMPTY, RenderType.cutout());
        pose.popPose();
    }

    private static void quad(VertexConsumer consumer, PoseStack.Pose pose, TextureAtlasSprite sprite, int light, int overlay) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        consumer.vertex(pose.pose(), 0.0F, 0.0F, 0.0F).color(255, 255, 255, 255)
                .uv(u0, v1).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(pose.pose(), 0.0F, 1.0F, 0.0F).color(255, 255, 255, 255)
                .uv(u0, v0).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(pose.pose(), 1.0F, 1.0F, 0.0F).color(255, 255, 255, 255)
                .uv(u1, v0).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(pose.pose(), 1.0F, 0.0F, 0.0F).color(255, 255, 255, 255)
                .uv(u1, v1).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, path);
    }
}
