package com.bruh;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;

public class StupidfooditemsClient implements ClientModInitializer {
	private static final Identifier TINT_SCREEN_LAYER = Identifier.of(Stupidfooditems.MOD_ID, "tint-screen-layer");

	@Override
	public void onInitializeClient() {
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, TINT_SCREEN_LAYER, StupidfooditemsClient::render));
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
			client.world.addParticleClient(
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

	public static void render(DrawContext context, RenderTickCounter tickCounter) {
		int color = 0xf581427e;
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null && client.player.hasStatusEffect(Stupidfooditems.StupidEffects.StupidFastEffectClass.STUPID_FAST)) {
			// Draw full-screen overlay
			context.fill(0, 0,
					context.getScaledWindowWidth(),
					context.getScaledWindowHeight(),
					color);
		}
	}
}
