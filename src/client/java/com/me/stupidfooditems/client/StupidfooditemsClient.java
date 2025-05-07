package com.me.stupidfooditems.client;

import com.me.stupidfooditems.Stupidfooditems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.particle.ParticleTypes;

public class StupidfooditemsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // After all client events
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
            }
        });
    }
}
