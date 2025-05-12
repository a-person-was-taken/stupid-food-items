package com.me.stupidfooditems.client;

import com.me.stupidfooditems.SimpleConfig;
import com.me.stupidfooditems.Stupidfooditems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class StupidfooditemsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (!SimpleConfig.PARTICLE_EFFECTS_ENABLED) return;
            if (client.player.getVelocity().horizontalLengthSquared() < 0.01) return;

            // Snowball particles for slippage effect
            if (client.player.hasStatusEffect(Stupidfooditems.StupidEffects.SlippageEffectClass.SLIPPAGE)) {
                spawnParticle(client, ParticleTypes.ITEM_SNOWBALL);
            }

            // Honey particles for sticky legs effect
            if (client.player.hasStatusEffect(Stupidfooditems.StupidEffects.StickyLegsEffectClass.STICKY_LEGS)) {
                spawnParticle(client, ParticleTypes.FALLING_HONEY);
            }
        });
    }

    private void spawnParticle(MinecraftClient client, ParticleEffect effect) {
        if (client.world != null) {
            assert client.player != null;
            client.world.addParticle(
                    effect,
                    client.player.getX(),
                    client.player.getY(),
                    client.player.getZ(),
                    0,  // velocity X
                    0.1, // velocity Y
                    0   // velocity Z
            );
        }
    }
}