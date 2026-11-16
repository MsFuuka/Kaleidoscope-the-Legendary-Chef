package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.integration.StockpotCarrierAccess;
import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase.SimpleSoupBaseRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public final class NoLiquidSoupBaseRender extends SimpleSoupBaseRender {
    private static final ResourceLocation PRIMAL_ICE = ResourceLocation.fromNamespaceAndPath(
            KaleidoscopeTheLegendaryChef.MODID, "block/primal_ice_block");

    private final ResourceLocation soupBaseTexture;

    public NoLiquidSoupBaseRender(ResourceLocation texture) {
        super(texture);
        this.soupBaseTexture = texture;
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntity entity, float partialTick, PoseStack pose,
                                        MultiBufferSource buffer, int light, int overlay, float height) {
        super.renderWhenPutIngredient(entity, partialTick, pose, buffer, light, overlay, height);
    }

    @Override
    public void renderWhenCooking(StockpotBlockEntity entity, float partialTick, PoseStack pose,
                                  MultiBufferSource buffer, int light, int overlay,
                                  ResourceLocation texture, float height) {
        super.renderWhenCooking(entity, partialTick, pose, buffer, light, overlay, soupBaseTexture, height);
    }

    @Override
    public void renderWhenFinished(StockpotBlockEntity entity, float partialTick, PoseStack pose,
                                  MultiBufferSource buffer, int light, int overlay,
                                  ResourceLocation texture, float height) {
        if (entity instanceof StockpotCarrierAccess access && access.everfrost$showsProductOnly()) {
            ItemStack stack = entity.getResult();
            if (!stack.isEmpty()) {
                if (stack.is(ModItems.FROZEN_MALLET.get())) {
                    renderPrimalIce(pose, buffer, light, overlay, height);
                } else {
                    Level level = entity.getLevel();
                    pose.pushPose();
                    pose.translate(0.5F, 0.42F, 0.5F);
                    pose.mulPose(Axis.XP.rotationDegrees(70F));
                    pose.scale(0.55F, 0.55F, 0.55F);
                    ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
                    renderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, pose, buffer, level, 0);
                    pose.popPose();
                }
            }
            return;
        }
        super.renderWhenFinished(entity, partialTick, pose, buffer, light, overlay, soupBaseTexture, height);
    }

    private void renderPrimalIce(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float height) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager()
                .getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(PRIMAL_ICE);
        Matrix4f matrix = pose.last().pose();
        Matrix3f normal = pose.last().normal();
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        float u0 = sprite.getU(3.0D);
        float u1 = sprite.getU(13.0D);
        float v0 = sprite.getV(3.0D);
        float v1 = sprite.getV(13.0D);
        int lightU = light & 0xFFFF;
        int lightV = (light >> 16) & 0xFFFF;
        int overlayU = overlay & 0xFFFF;
        int overlayV = (overlay >> 16) & 0xFFFF;
        consumer.vertex(matrix, 0.1875F, height, 0.1875F).color(255, 255, 255, 255)
                .uv(u0, v0).overlayCoords(overlayU, overlayV).uv2(lightU, lightV)
                .normal(normal, 0F, 1F, 0F).endVertex();
        consumer.vertex(matrix, 0.1875F, height, 0.8125F).color(255, 255, 255, 255)
                .uv(u0, v1).overlayCoords(overlayU, overlayV).uv2(lightU, lightV)
                .normal(normal, 0F, 1F, 0F).endVertex();
        consumer.vertex(matrix, 0.8125F, height, 0.8125F).color(255, 255, 255, 255)
                .uv(u1, v1).overlayCoords(overlayU, overlayV).uv2(lightU, lightV)
                .normal(normal, 0F, 1F, 0F).endVertex();
        consumer.vertex(matrix, 0.8125F, height, 0.1875F).color(255, 255, 255, 255)
                .uv(u1, v0).overlayCoords(overlayU, overlayV).uv2(lightU, lightV)
                .normal(normal, 0F, 1F, 0F).endVertex();
    }

    public static void renderInputs(StockpotBlockEntity entity, PoseStack pose, MultiBufferSource buffer,
                                    int light, int overlay, float height) {
        NonNullList<ItemStack> inputs = entity.getInputs();
        int count = 0;
        for (ItemStack stack : inputs) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        if (count == 0) {
            return;
        }
        Level level = entity.getLevel();
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        int index = 0;
        for (ItemStack stack : inputs) {
            if (stack.isEmpty()) {
                continue;
            }
            double angle = index * 2.39996;
            double radius = count > 1 ? 0.16 : 0.0;
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;
            index++;
            pose.pushPose();
            pose.translate(0.5F + dx, height, 0.5F + dz);
            pose.mulPose(Axis.XP.rotationDegrees(70F));
            pose.scale(0.4F, 0.4F, 0.4F);
            renderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, pose, buffer, level, 0);
            pose.popPose();
        }
    }
}
