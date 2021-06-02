package fr.mosca421.worldprotector.network;

import fr.mosca421.worldprotector.WorldProtector;
import fr.mosca421.worldprotector.network.message.RegionSyncMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String version = "1.0";

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(WorldProtector.MODID, "network"), () -> version, it -> it.equals(version), it -> it.equals(version));

    public static void init(){
        channel.registerMessage(0, RegionSyncMessage.class, RegionSyncMessage::encode, RegionSyncMessage::decode, RegionSyncMessage::handle);
    }

    // TODO: Events for creating regions etc and then send messages?
    public static void sendTo(Object message, PlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }
}
