package com.me.stupidfooditems.client;

import com.me.stupidfooditems.Stupidfooditems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;

import java.util.concurrent.atomic.AtomicReference;

public class StupidfooditemsClient implements ClientModInitializer {
    public record PlayerInputC2SPayload(PlayerInput input) implements CustomPayload {
        public static final Identifier INPUT_PACKET_PAYLOAD_ID = Identifier.of(Stupidfooditems.StupidFoods.MOD_ID, "input_packet");
        public static final CustomPayload.Id<PlayerInputC2SPayload> ID = new CustomPayload.Id<>(INPUT_PACKET_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, PlayerInputC2SPayload> CODEC = PacketCodec.tuple(
                PlayerInput.PACKET_CODEC, PlayerInputC2SPayload::input,
                PlayerInputC2SPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.hasStatusEffect(Stupidfooditems.StupidFoods.SLIPPAGE)) {
                // Add particle effects while moving
                if (client.player.getVelocity().horizontalLengthSquared() > 0.01) {
                    assert client.world != null;
                    client.world.addParticle(
                            ParticleTypes.ITEM_SNOWBALL,
                            client.player.getX(),
                            client.player.getY(),
                            client.player.getZ(),
                            0, 0.1, 0
                    );
                }
                //Define custom packet
                PlayerInputC2SPayload payload = new PlayerInputC2SPayload(PlayerInput.DEFAULT);
                // Send custom packet to the server
                ClientPlayNetworking.send(payload);
            }
        });
    }
}
