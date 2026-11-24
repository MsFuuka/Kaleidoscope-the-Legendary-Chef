package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import com.MsFuuka.kaleidoscope_the_legendary_chef.registry.ModBlocks;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class MossConversionManager {
    private static final ConcurrentHashMap<ResourceKey<Level>, MossConversionData> DATA = new ConcurrentHashMap<>();

    private MossConversionManager() {
    }

    public static void onLevelLoad(ServerLevel level) {
        DATA.put(level.dimension(), MossConversionData.get(level));
    }

    private static MossConversionData data(ServerLevel level) {
        return DATA.computeIfAbsent(level.dimension(), key -> MossConversionData.get(level));
    }

    public static void schedule(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.COBBLESTONE)) {
            return;
        }
        if (data(level).putIfAbsent(pos, level.getGameTime() + TenderGreenMossCropBlock.CONVERT_INTERVAL) != null) {
            return;
        }
        level.scheduleTick(pos, Blocks.COBBLESTONE, TenderGreenMossCropBlock.CONVERT_INTERVAL);
    }

    public static void scheduleAround(ServerLevel level, BlockPos origin) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = origin.relative(direction);
            if (!level.getBlockState(neighbor).is(Blocks.COBBLESTONE)) {
                continue;
            }
            if (level.getBlockState(neighbor.above()).is(ModBlocks.TENDER_GREEN_MOSS_CROP.get())) {
                continue;
            }
            if (TenderGreenMossCropBlock.canConvertConnected(level, neighbor)) {
                schedule(level, neighbor);
            }
        }
    }

    public static void convert(ServerLevel level, BlockPos pos) {
        MossConversionData data = data(level);
        data.remove(pos);
        if (!level.getBlockState(pos).is(Blocks.COBBLESTONE)) {
            return;
        }
        if (!TenderGreenMossCropBlock.canConvertConnected(level, pos)) {
            return;
        }
        level.setBlock(pos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3);
        scheduleAround(level, pos);
    }

    public static int remainingTicks(Level level, BlockPos pos) {
        MossConversionData data = DATA.get(level.dimension());
        if (data == null) {
            return -1;
        }
        Long deadline = data.get(pos);
        if (deadline == null) {
            return -1;
        }
        return (int) Math.max(0L, deadline - level.getGameTime());
    }
}
