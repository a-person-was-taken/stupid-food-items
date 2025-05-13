package com.me.stupidfooditems.mixin;

import com.me.stupidfooditems.Stupidfooditems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Unique
    private int foodEatProgress;

    @Unique
    private int nukeTime;
    @Unique
    private int tntTime;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        World world = player.getWorld();

        // Only run on server side
        if (world.isClient()) return;

        // Check if the player is a ServerPlayerEntity before casting
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        if (nukeTime > 0) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        TntEntity tnt = new TntEntity(
                                world,
                                player.getX() + x,
                                player.getY() + 63 + y,
                                player.getZ() + z,
                                null
                        );
                        tnt.setFuse(85);
                        world.spawnEntity(tnt);
                    }
                }
            }
            if (nukeTime == 60 * 20){
                serverPlayer.networkHandler.sendPacket(
                        new TitleS2CPacket(Text.of("§4§lNUKE ACTIVATED"))
                );
            }
            nukeTime--;
        }

        if (tntTime > 0) {
            if (tntTime % (20 * 10) == 0) {
                world.spawnEntity(new TntEntity(EntityType.TNT, world));
            }
            tntTime--;
        }

        ItemStack activeItem = player.getActiveItem();
        if (!activeItem.contains(DataComponentTypes.FOOD)) {
            foodEatProgress = 0;
            return;
        }

        if (player.getItemUseTime() >= 31 && foodEatProgress > 0) {
            if (activeItem.isOf(Stupidfooditems.StupidFoods.NUKE_COOKIE)) {
                nukeTime = 60 * 20;
            }
            if (activeItem.isOf(Stupidfooditems.StupidFoods.WONDERFUL_COOKIE)) {
                this.grantAllAdvancements(serverPlayer);
            }
            if (activeItem.isOf(Stupidfooditems.StupidFoods.TNT_COOKIE)) {
                tntTime = 60 * 20 * 60 * 24;
            }
        }
        foodEatProgress = player.getItemUseTime();
    }

    @Unique
    private void grantAllAdvancements(ServerPlayerEntity player) {
        ServerAdvancementLoader advancementLoader = player.server.getAdvancementLoader();

        // Get all advancement entries
        for (AdvancementEntry entry : advancementLoader.getAdvancements()) {
            // Skip if already completed
            if (player.getAdvancementTracker().getProgress(entry).isDone()) {
                continue;
            }

            // Grant all criteria
            Advancement advancement = entry.value();
            for (String criterion : advancement.criteria().keySet()) {
                player.getAdvancementTracker().grantCriterion(entry, criterion);
            }
        }
    }
}

