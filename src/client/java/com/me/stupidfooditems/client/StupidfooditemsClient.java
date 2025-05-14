package com.me.stupidfooditems.client;

import com.me.stupidfooditems.SimpleConfig;
import com.me.stupidfooditems.Stupidfooditems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class StupidfooditemsClient implements ClientModInitializer {
    private static final Identifier TINT_SCREEN_LAYER = Identifier.of(Stupidfooditems.MOD_ID, "tint-screen-layer");    

    @Override
    public void onInitializeClient() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, TINT_SCREEN_LAYER, HudRenderingEntrypoint::render));
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

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
		int color = 0xFFFFFF7E; // White
		int targetColor = 0x0000007E; // Black

		// You can use the Util.getMeasuringTimeMs() function to get the current time in milliseconds.
		// Divide by 1000 to get seconds.
		double currentTime = Util.getMeasuringTimeMs() / 1000.0;

		// "lerp" simply means "linear interpolation", which is a fancy way of saying "blend".
		float lerpedAmount = MathHelper.abs(MathHelper.sin((float) currentTime));
		int lerpedColor = ColorHelper.lerp(lerpedAmount, color, targetColor);

		// Draw a square with the lerped color.
		// x1, x2, y1, y2, z, color
		context.fill(0, 0, 1920, 1080, 0, lerpedColor); //Assuming the screen size is 1080p
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
