package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.FrozenMalletBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.FrozenMalletBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public final class FrozenMalletBlockRenderer implements BlockEntityRenderer<FrozenMalletBlockEntity> {
    private static final ResourceLocation[] MODELS = {
            id("frozen_mallet"),
            id("frozen_mallet_first"),
            id("frozen_mallet_second"),
            id("frozen_mallet_third"),
            id("frozen_mallet_fourth"),
            id("frozen_mallet_fifth")
    };

    public FrozenMalletBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FrozenMalletBlockEntity blockEntity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = blockEntity.getBlockState();
        int stage = state.getValue(FrozenMalletBlock.STAGE);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(MODELS[stage]);
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        pose.pushPose();
        pose.translate(0.5F, 0.0F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-degrees(state.getValue(FrozenMalletBlock.FACING))));
        pose.translate(-0.5F, 0.0F, -0.5F);
        pose.scale(2.0F, 2.0F * FrozenMalletBlock.heightScale(stage), 2.0F);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .renderModel(pose.last(), consumer, state, model, 1.0F, 1.0F, 1.0F, light, overlay, ModelData.EMPTY, RenderType.cutout());
        pose.popPose();
    }

    private static float degrees(Direction facing) {
        return switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, "block/" + path);
    }
}
