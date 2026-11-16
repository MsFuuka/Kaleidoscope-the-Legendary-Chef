package com.MsFuuka.kaleidoscope_the_legendary_chef.network;

import java.util.function.Supplier;

import com.MsFuuka.kaleidoscope_the_legendary_chef.item.FrozenMalletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class MalletStatePacket {
    private final int stacks;
    private final int damage;

    public MalletStatePacket(int stacks, int damage) {
        this.stacks = stacks;
        this.damage = damage;
    }

    public MalletStatePacket(FriendlyByteBuf buf) {
        this.stacks = buf.readVarInt();
        this.damage = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.stacks);
        buf.writeVarInt(this.damage);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null) {
                return;
            }
            ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
            if (stack.getItem() instanceof FrozenMalletItem) {
                FrozenMalletItem.setSpeedStacks(stack, this.stacks);
                stack.setDamageValue(this.damage);
            }
        });
        context.setPacketHandled(true);
    }
}
