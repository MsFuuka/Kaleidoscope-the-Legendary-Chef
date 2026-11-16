package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TotemOfWinterModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, "totem_of_winter"), "main");

    private final ModelPart bone3;
    private final ModelPart body;
    private final ModelPart ice;

    public TotemOfWinterModel(ModelPart root) {
        this.bone3 = root.getChild("bone3");
        this.body = this.bone3.getChild("body");
        this.ice = this.bone3.getChild("ice");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(48, 40).addBox(-1.0F, -35.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-11.0F, -24.5F, -3.0F, 30.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 24.0F, 0.0F));

        PartDefinition body = bone3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -18.0F, -5.0F, 10.0F, 15.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 52).addBox(-1.0F, -23.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 37).addBox(-2.0F, -3.0F, -6.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(48, 26).addBox(-2.0F, -20.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(40, 12).addBox(-2.0F, -25.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition ice = bone3.addOrReplaceChild("ice", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = ice.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 60).addBox(-2.0F, -7.0F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 0.0F, -3.0F, 0.1733F, 0.3542F, 0.4674F));

        PartDefinition cube_r2 = ice.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 60).addBox(-3.0F, -12.0F, -1.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

        PartDefinition cube_r3 = ice.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 60).addBox(-1.0F, -8.0F, -1.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 4.0F, -0.3076F, -0.5457F, -0.2293F));

        PartDefinition cube_r4 = ice.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(40, 60).addBox(-1.0F, -9.0F, -1.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
