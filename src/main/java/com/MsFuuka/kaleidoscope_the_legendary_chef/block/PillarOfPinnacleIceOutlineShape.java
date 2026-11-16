package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PillarOfPinnacleIceOutlineShape extends ArrayVoxelShape {
    private static final double EPSILON = 1.0E-7D;

    private final double[][] edges;
    private final List<AABB> boxes;

    private PillarOfPinnacleIceOutlineShape(BitSetDiscreteVoxelShape grid, double[] xs, double[] ys, double[] zs, List<AABB> boxes, double[][] edges) {
        super(grid, xs, ys, zs);
        this.boxes = boxes;
        this.edges = edges;
    }

    public static VoxelShape of(List<AABB> boxes, double[][] edges) {
        if (boxes.isEmpty()) {
            return Shapes.empty();
        }
        double[] xs = boundaries(boxes, 0);
        double[] ys = boundaries(boxes, 1);
        double[] zs = boundaries(boxes, 2);
        if (xs.length < 2 || ys.length < 2 || zs.length < 2) {
            return Shapes.empty();
        }
        BitSetDiscreteVoxelShape grid = new BitSetDiscreteVoxelShape(xs.length - 1, ys.length - 1, zs.length - 1);
        for (AABB box : boxes) {
            for (int ix = 0; ix < xs.length - 1; ix++) {
                if (box.maxX <= xs[ix] + EPSILON || box.minX >= xs[ix + 1] - EPSILON) {
                    continue;
                }
                for (int iy = 0; iy < ys.length - 1; iy++) {
                    if (box.maxY <= ys[iy] + EPSILON || box.minY >= ys[iy + 1] - EPSILON) {
                        continue;
                    }
                    for (int iz = 0; iz < zs.length - 1; iz++) {
                        if (box.maxZ <= zs[iz] + EPSILON || box.minZ >= zs[iz + 1] - EPSILON) {
                            continue;
                        }
                        grid.fill(ix, iy, iz);
                    }
                }
            }
        }
        return new PillarOfPinnacleIceOutlineShape(grid, xs, ys, zs, List.copyOf(boxes), edges);
    }

    private static double[] boundaries(List<AABB> boxes, int axis) {
        double[] values = new double[boxes.size() * 2];
        int count = 0;
        for (AABB box : boxes) {
            values[count++] = coordinate(box, axis, false);
            values[count++] = coordinate(box, axis, true);
        }
        Arrays.sort(values);
        int unique = 0;
        for (int i = 0; i < count; i++) {
            if (unique == 0 || values[i] - values[unique - 1] > EPSILON) {
                values[unique++] = values[i];
            }
        }
        return Arrays.copyOf(values, unique);
    }

    private static double coordinate(AABB box, int axis, boolean max) {
        return switch (axis) {
            case 0 -> max ? box.maxX : box.minX;
            case 1 -> max ? box.maxY : box.minY;
            default -> max ? box.maxZ : box.minZ;
        };
    }

    @Override
    public List<AABB> toAabbs() {
        return boxes;
    }

    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer consumer) {
        for (double[] edge : edges) {
            consumer.consume(edge[0], edge[1], edge[2], edge[3], edge[4], edge[5]);
        }
    }

    @Override
    public VoxelShape optimize() {
        return this;
    }
}
