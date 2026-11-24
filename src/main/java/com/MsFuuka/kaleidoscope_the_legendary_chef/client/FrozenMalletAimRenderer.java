package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "kaleidoscope_the_legendary_chef", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FrozenMalletAimRenderer {
    private FrozenMalletAimRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof FrozenMalletItem)) {
            return;
        }
        if (!mc.options.keyUse.isDown() || !player.isUsingItem()) {
            return;
        }
        if (!(player.getUseItem().getItem() instanceof FrozenMalletItem)) {
            return;
        }
        if (72000 - player.getUseItemRemainingTicks() < 3) {
            return;
        }
        if (player.isShiftKeyDown()) {
            return;
        }
        if (mc.gameMode == null) {
            return;
        }
        double reach = mc.gameMode.getPickRange();
        BlockPos pillarPos = PillarOfPinnacleIceBlock.resolveAim(mc.level, player, mc.hitResult, reach, 1.0F);
        if (pillarPos == null) {
            return;
        }
        BlockPos ground = pillarPos.below();
        if (player.getEyePosition().distanceTo(Vec3.atCenterOf(ground)) - 0.5D > reach) {
            return;
        }
        Vec3 camPos = event.getCamera().getPosition();

        AABB box = new AABB(ground, ground.offset(1, 1, 1)).inflate(0.02D);
        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        stack.translate(-camPos.x, -camPos.y, -camPos.z);
        VertexConsumer vc = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(stack, vc, box, 0.25F, 0.85F, 1.0F, 0.9F);
        stack.popPose();
        mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
    }
}
