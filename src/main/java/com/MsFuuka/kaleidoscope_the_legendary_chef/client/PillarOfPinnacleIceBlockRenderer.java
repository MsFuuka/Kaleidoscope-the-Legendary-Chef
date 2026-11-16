package com.MsFuuka.kaleidoscope_the_legendary_chef.client;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlock;
import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.miauczel.legendary_monsters.entity.client.Model.IceSpikeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector4f;

public final class PillarOfPinnacleIceBlockRenderer implements BlockEntityRenderer<PillarOfPinnacleIceBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, "textures/entity/pillar_of_pinnacle_ice.png");
    private final IceSpikeModel<Entity> model;
    private final RenderType renderType;

    public PillarOfPinnacleIceBlockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = IceSpikeModel.createLayerDefinition().bakeRoot();
        this.model = new IceSpikeModel<>(root);
        this.renderType = RenderType.entityCutout(TEXTURE);
    }

    @Override
    public void render(PillarOfPinnacleIceBlockEntity blockEntity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        float elapsed = blockEntity.getAge() + partialTick;
        if (elapsed >= PillarOfPinnacleIceBlock.LIFE_TICKS + PillarOfPinnacleIceBlock.SINK_TICKS) {
            return;
        }
        double offsetY = PillarOfPinnacleIceBlock.offsetOf(elapsed);
        Level level = blockEntity.getLevel();
        BlockState state = level == null ? blockEntity.getBlockState() : level.getBlockState(blockEntity.getBlockPos());
        float yaw = PillarOfPinnacleIceBlock.yawOf(state);
        float scaleXZ = PillarOfPinnacleIceBlock.MODEL_SCALE * PillarOfPinnacleIceBlock.horizontalScaleOf(yaw);
        double[] offset = PillarOfPinnacleIceBlock.horizontalOffsetOf(yaw);

        Vector4f normal = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
        pose.last().pose().transform(normal);
        Vector4f origin = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        pose.last().pose().transform(origin);
        double cut = origin.x * normal.x + origin.y * normal.y + origin.z * normal.z;

        pose.pushPose();
        pose.translate(0.0D, offsetY, 0.0D);
        pose.translate(0.5D - offset[0], PillarOfPinnacleIceBlock.GROUND_LIFT, 0.5D - offset[1]);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F - yaw));
        pose.scale(-scaleXZ, -PillarOfPinnacleIceBlock.MODEL_SCALE, scaleXZ);
        pose.translate(0.0D, -0.626D, 0.0D);
        pose.scale(0.7F, 0.7F, 0.7F);
        model.setupAnim(null, PillarOfPinnacleIceBlock.STABLE_PROGRESS, 0.0F, 0.0F, yaw, 0.0F);
        VertexConsumer target = buffer.getBuffer(renderType);
        VertexConsumer consumer = offsetY < -1.0E-5D
                ? new GroundClipConsumer(target, normal.x, normal.y, normal.z, cut)
                : target;
        model.renderToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
        if (buffer instanceof MultiBufferSource.BufferSource source) {
            source.endBatch(renderType);
        }
    }

    private static final class V {
        double x;
        double y;
        double z;
        int r;
        int g;
        int b;
        int a;
        float u;
        float v;
        int ou;
        int ov;
        int lu;
        int lv;
        float nx;
        float ny;
        float nz;
    }

    private static final class GroundClipConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final double nx;
        private final double ny;
        private final double nz;
        private final double cut;
        private final V pending = new V();
        private final V[] quad = new V[4];
        private int count;

        private GroundClipConsumer(VertexConsumer delegate, double nx, double ny, double nz, double cut) {
            this.delegate = delegate;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
            this.cut = cut;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            pending.x = x;
            pending.y = y;
            pending.z = z;
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            pending.r = r;
            pending.g = g;
            pending.b = b;
            pending.a = a;
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            pending.u = u;
            pending.v = v;
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            pending.ou = u;
            pending.ov = v;
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            pending.lu = u;
            pending.lv = v;
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            pending.nx = x;
            pending.ny = y;
            pending.nz = z;
            return this;
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            delegate.defaultColor(r, g, b, a);
        }

        @Override
        public void unsetDefaultColor() {
            delegate.unsetDefaultColor();
        }

        @Override
        public void endVertex() {
            V copy = new V();
            copy.x = pending.x;
            copy.y = pending.y;
            copy.z = pending.z;
            copy.r = pending.r;
            copy.g = pending.g;
            copy.b = pending.b;
            copy.a = pending.a;
            copy.u = pending.u;
            copy.v = pending.v;
            copy.ou = pending.ou;
            copy.ov = pending.ov;
            copy.lu = pending.lu;
            copy.lv = pending.lv;
            copy.nx = pending.nx;
            copy.ny = pending.ny;
            copy.nz = pending.nz;
            quad[count++] = copy;
            if (count == 4) {
                clipTriangle(quad[0], quad[1], quad[2]);
                clipTriangle(quad[0], quad[2], quad[3]);
                count = 0;
            }
        }

        private void clipTriangle(V a, V b, V c) {
            V[] in = {a, b, c};
            V[] out = new V[4];
            int n = 0;
            for (int i = 0; i < 3; i++) {
                V p = in[i];
                V q = in[(i + 1) % 3];
                boolean pIn = distance(p) >= 0.0D;
                boolean qIn = distance(q) >= 0.0D;
                if (pIn && qIn) {
                    out[n++] = q;
                } else if (pIn) {
                    out[n++] = intersect(p, q);
                } else if (qIn) {
                    out[n++] = intersect(p, q);
                    out[n++] = q;
                }
            }
            if (n < 3) {
                return;
            }
            emit(out[0]);
            emit(out[1]);
            emit(out[2]);
            emit(n == 4 ? out[3] : out[2]);
        }

        private double distance(V v) {
            return v.x * nx + v.y * ny + v.z * nz - cut;
        }

        private V intersect(V a, V b) {
            double da = distance(a);
            double db = distance(b);
            double denom = da - db;
            double t = denom == 0.0D ? 0.0D : da / denom;
            if (t < 0.0D) {
                t = 0.0D;
            } else if (t > 1.0D) {
                t = 1.0D;
            }
            V r = new V();
            r.x = mix(a.x, b.x, t);
            r.y = mix(a.y, b.y, t);
            r.z = mix(a.z, b.z, t);
            r.r = mix(a.r, b.r, t);
            r.g = mix(a.g, b.g, t);
            r.b = mix(a.b, b.b, t);
            r.a = mix(a.a, b.a, t);
            r.u = mix(a.u, b.u, t);
            r.v = mix(a.v, b.v, t);
            r.ou = mix(a.ou, b.ou, t);
            r.ov = mix(a.ov, b.ov, t);
            r.lu = mix(a.lu, b.lu, t);
            r.lv = mix(a.lv, b.lv, t);
            r.nx = mix(a.nx, b.nx, t);
            r.ny = mix(a.ny, b.ny, t);
            r.nz = mix(a.nz, b.nz, t);
            return r;
        }

        private void emit(V v) {
            delegate.vertex(v.x, v.y, v.z)
                    .color(v.r, v.g, v.b, v.a)
                    .uv(v.u, v.v)
                    .overlayCoords(v.ou, v.ov)
                    .uv2(v.lu, v.lv)
                    .normal(v.nx, v.ny, v.nz)
                    .endVertex();
        }

        private static int mix(int a, int b, double t) {
            return (int) Math.round(a + (b - a) * t);
        }

        private static float mix(float a, float b, double t) {
            return (float) (a + (b - a) * t);
        }

        private static double mix(double a, double b, double t) {
            return a + (b - a) * t;
        }
    }
}
