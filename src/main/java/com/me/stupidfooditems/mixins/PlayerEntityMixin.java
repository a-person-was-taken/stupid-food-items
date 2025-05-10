package com.me.stupidfooditems.mixins;

import com.me.stupidfooditems.Stupidfooditems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private int lastEatTime = 0;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        System.out.println("PLAYER MIXIN LOADED!");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack activeItem = player.getActiveItem();

        // Check if player is eating food
        if (activeItem.isOf(Stupidfooditems.StupidFoods.NUKE_COOKIE)) {
            if (player.getItemUseTime() == 1 && lastEatTime > 1) {
                // Food was just consumed!
                player.sendMessage(Text.of(player.getName().getString() +
                        " ate " + activeItem.getItem().getName().getString()), false);
                // Trigger your custom logic here
                World world = player.getWorld();
                TntEntity tnt = new TntEntity(EntityType.TNT, world);
                tnt.setPosition(player.getPos().add(0, 50, 0));
            }
            lastEatTime = player.getItemUseTime();
        } else {
            lastEatTime = 0;
        }
    }
}
