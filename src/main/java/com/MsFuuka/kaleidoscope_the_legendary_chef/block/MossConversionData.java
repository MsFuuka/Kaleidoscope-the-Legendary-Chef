package com.MsFuuka.kaleidoscope_the_legendary_chef.block;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class MossConversionData extends SavedData {
    private final ConcurrentHashMap<BlockPos, Long> deadlines = new ConcurrentHashMap<>();

    public static MossConversionData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(MossConversionData::load, MossConversionData::new,
                "kaleidoscope_moss_conversions");
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag entries = new CompoundTag();
        deadlines.forEach((pos, deadline) -> entries.putLong(Long.toString(pos.asLong()), deadline));
        tag.put("deadlines", entries);
        return tag;
    }

    public static MossConversionData load(CompoundTag tag) {
        MossConversionData data = new MossConversionData();
        CompoundTag entries = tag.getCompound("deadlines");
        for (String key : entries.getAllKeys()) {
            data.deadlines.put(BlockPos.of(Long.parseLong(key)), entries.getLong(key));
        }
        return data;
    }

    public Long putIfAbsent(BlockPos pos, long deadline) {
        Long previous = deadlines.putIfAbsent(pos, deadline);
        if (previous == null) {
            setDirty();
        }
        return previous;
    }

    public void remove(BlockPos pos) {
        if (deadlines.remove(pos) != null) {
            setDirty();
        }
    }

    public Long get(BlockPos pos) {
        return deadlines.get(pos);
    }
}
