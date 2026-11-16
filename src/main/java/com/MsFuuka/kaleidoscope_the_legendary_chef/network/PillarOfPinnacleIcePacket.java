package com.MsFuuka.kaleidoscope_the_legendary_chef.network;

import java.util.function.Supplier;

import com.MsFuuka.kaleidoscope_the_legendary_chef.block.PillarOfPinnacleIceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class PillarOfPinnacleIcePacket {
    private final BlockPos pos;
    private final float yRot;

    public PillarOfPinnacleIcePacket(BlockPos pos, float yRot) {
        this.pos = pos;
        this.yRot = yRot;
    }

    public PillarOfPinnacleIcePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.yRot = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeFloat(this.yRot);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            Level level = player.level();
            if (level.isClientSide()) {
                return;
            }
            PillarOfPinnacleIceBlock.placeFrom(level, this.pos, this.yRot);
        });
        context.setPacketHandled(true);
    }
}
