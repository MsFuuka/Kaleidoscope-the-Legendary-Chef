package com.MsFuuka.kaleidoscope_the_legendary_chef.network;

import com.MsFuuka.kaleidoscope_the_legendary_chef.KaleidoscopeTheLegendaryChef;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTheLegendaryChef.MODID, "pillar_of_pinnacle_ice"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    private static int id = 0;

    public static void register() {
        INSTANCE.messageBuilder(PillarOfPinnacleIcePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PillarOfPinnacleIcePacket::encode)
                .decoder(PillarOfPinnacleIcePacket::new)
                .consumerMainThread(PillarOfPinnacleIcePacket::handle)
                .add();
        INSTANCE.messageBuilder(MalletStatePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(MalletStatePacket::encode)
                .decoder(MalletStatePacket::new)
                .consumerMainThread(MalletStatePacket::handle)
                .add();
    }
}
