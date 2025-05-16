package com.bruh.mixin;

import com.bruh.Stupidfooditems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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
                        tnt.setFuse(100);
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
                TntEntity tnt = new TntEntity(
                        world,
                        player.getX(),
                        player.getY() + 1,
                        player.getZ(),
                        null
                );
                world.spawnEntity(tnt);
            }
            tntTime--;
        }

        ItemStack activeItem = player.getActiveItem();
        if (!activeItem.contains(DataComponentTypes.FOOD)) {
            foodEatProgress = 0;
            return;
        }

        //Detects when a player right-clicks an item
        if (player.getItemUseTime() > 0) {
            if(activeItem.isOf(Stupidfooditems.StupidFoods.COOKIE_BOX)){
                List<Item> common_cookies = List.of(
                        Stupidfooditems.StupidFoods.STAR_BUTTER_COOKIE,
                        Stupidfooditems.StupidFoods.RECTANGULAR_BUTTER_COOKIE,
                        Stupidfooditems.StupidFoods.TRIANGULAR_BUTTER_COOKIE,
                        Stupidfooditems.StupidFoods.UMBRELLA_BUTTER_COOKIE,
                        Stupidfooditems.StupidFoods.BUTTER_COOKIE,
                        Stupidfooditems.StupidFoods.IRON_COOKIE
                );
                List<Item> rare_cookies = List.of(
                        Stupidfooditems.StupidFoods.BURNT_COOKIE,
                        Stupidfooditems.StupidFoods.DISGUSTING_COOKIE,
                        Stupidfooditems.StupidFoods.HONEY_COOKIE,
                        Stupidfooditems.StupidFoods.TNT_COOKIE,
                        Stupidfooditems.StupidFoods.GOLDEN_COOKIE
                );
                List<Item> epic_cookies = List.of(
                        Stupidfooditems.StupidFoods.LAVA_COOKIE,
                        Stupidfooditems.StupidFoods.NUKE_COOKIE,
                        Stupidfooditems.StupidFoods.WONDERFUL_COOKIE,
                        Stupidfooditems.StupidFoods.BOSS_COOKIE,
                        Stupidfooditems.Stupidfoods.DIAMOND_COOKIE,
                        Stupidfooditems.StupidFoods.NETHERITE_COOKIE
                );
                for (int i = 0; i < 4; i++) {
                    double rand = Math.random();
                    if (rand < 0.6) {
                        player.giveItemStack(new ItemStack(common_cookies.get((int) Math.floor(Math.random() * common_cookies.size())), 1));
                    } else if (rand > 0.6 && rand < 0.9) {
                        player.giveItemStack(new ItemStack(rare_cookies.get((int) Math.floor(Math.random() * rare_cookies.size())), 1));
                    }
                    else {
                        player.giveItemStack(new ItemStack(epic_cookies.get((int) Math.floor(Math.random() * epic_cookies.size())), 1));
                    }
                }
            }
        }

        //Detects when a player eats a food item
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
            if (activeItem.isOf(Stupidfooditems.StupidFoods.BOSS_COOKIE)) {
                for (int i = 0; i < 5; i++) {
                    spawnEntity(EntityType.ENDER_DRAGON, world, SpawnReason.COMMAND, player.getPos().add(0, 32, 0), 0.0f, 0.0f);
                    spawnEntity(EntityType.WITHER, world, SpawnReason.COMMAND, player.getPos().add(0, 16, 0), 90.0f, 90.0f);
                    spawnEntity(EntityType.ELDER_GUARDIAN, world, SpawnReason.NATURAL, player.getPos().add(0, 4, 0), 180.0f, 180.0f);
                    spawnEntity(EntityType.WARDEN, world, SpawnReason.COMMAND, player.getPos().add(0, 2, 0), 270.0f, 270.0f);
                }
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

    @Unique
    private void spawnEntity(EntityType entityType, World world, SpawnReason reason, Vec3d pos, float yaw, float pitch){
        Entity entity = entityType.create(world, reason);
        assert entity != null;
        entity.refreshPositionAndAngles(pos, yaw, pitch);
        world.spawnEntity(entity);
    }
}
