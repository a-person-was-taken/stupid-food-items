package com.me.stupidfooditems;

import com.me.stupidfooditems.client.StupidfooditemsClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Function;

public class Stupidfooditems implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(StupidfooditemsClient.PlayerInputC2SPayload.ID, StupidfooditemsClient.PlayerInputC2SPayload.CODEC);
        StupidFoods.initialize();
    }
    private static final float BASE_SLIPPERINESS = 0.6f;
    private static final float MIN_VELOCITY = 0.003f; // Minecraft's stopping threshold
    public static class StupidFoods {
        public static String MOD_ID = "stupidfooditems";
        // Define the effect first
        public static final RegistryEntry<StatusEffect> SLIPPAGE =
                Registry.registerReference(
                        Registries.STATUS_EFFECT,
                        Identifier.of(MOD_ID, "slippage"),
                        new SlippageEffect()
                );

        // Then define the potion that uses the effect
        public static final Potion SLIPPAGE_POTION =
                Registry.register(
                        Registries.POTION,
                        Identifier.of(MOD_ID, "slippage"),
                        new Potion("slippage",
                                new StatusEffectInstance(
                                        SLIPPAGE,
                                        3600,
                                        0)));

        public static class SlippageEffect extends StatusEffect {
            protected SlippageEffect() {
                super(StatusEffectCategory.HARMFUL, 0x75dfff);
            }

            @Override
            public boolean canApplyUpdateEffect(int duration, int amplifier) {
                return true;
            }

            public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
                // Your effect logic here
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                if (!player.isOnGround()) return true;

                // Get block slipperiness and effect level
                BlockPos pos = player.getBlockPos().down();
                float blockSlipperiness = player.getWorld().getBlockState(pos).getBlock().getSlipperiness();

                // Calculate effective slipperiness
                float slipperiness = blockSlipperiness <= BASE_SLIPPERINESS ?
                        BASE_SLIPPERINESS + (0.08f * (amplifier + 1)) :
                        blockSlipperiness;

                Vec3d velocity = player.getVelocity();
                Vec3d movementInput = getMovementInput();

                // Only apply acceleration if there's movement input
                if (movementInput.lengthSquared() > 1.0E-4f) {
                    // Acceleration force (weaker than vanilla)
                    float acceleration = 0.08f * (1.0f - (slipperiness - BASE_SLIPPERINESS));
                    velocity = velocity.add(movementInput.multiply(acceleration));
                } else {
                    // Deceleration - stronger at higher slipperiness
                    float deceleration = 0.98f - (slipperiness * 0.1f);
                    velocity = velocity.multiply(deceleration, 1.0, deceleration);
                }

                // Apply friction (main physics change)
                float friction = 0.91f * slipperiness;
                velocity = new Vec3d(
                        velocity.x * friction,
                        velocity.y,
                        velocity.z * friction
                );

                // Stop very small movements
                if (Math.abs(velocity.x) < MIN_VELOCITY) velocity = new Vec3d(0, velocity.y, velocity.z);
                if (Math.abs(velocity.z) < MIN_VELOCITY) velocity = new Vec3d(velocity.x, velocity.y, 0);

                player.setVelocity(velocity);
                player.velocityModified = true;
                player.sendMessage(Text.of("Vel X: " + velocity.x + ", Vel Y: " + velocity.y + ", Vel Z: " + velocity.z), false);
                return super.applyUpdateEffect(world, entity, amplifier);
            }
            Vec3d getMovementInput(){
                return new Vec3d(1,1,1);
            }
        }

        public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, String key, Formatting color) {
            tooltip.add(Text.translatable(key).formatted(color));
        }

        public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
            Item item = itemFactory.apply(settings.registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, item);
            return item;
        }

        public static final ConsumableComponent BUTTER_COOKIE_CONSUMABLE_COMPONENT = ConsumableComponents.food()
                // The duration is in ticks, 20 ticks = 1 second
                .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.POISON, 6 * 20, 1), 1.0f))
                .build();

        public static final Item BUTTER_COOKIE = register("butter_cookie", Item::new, new Item.Settings().food(new FoodComponent(2,3,true)));
        private static Object Settings;
        public static final PotionItem SLIPPAGE_POTION_ITEM = register("potion_of_slippage");
        public static void initialize() {
            // Register the potion
            FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
                builder.registerPotionRecipe(
                        // Input potion.
                        Potions.AWKWARD,
                        // Ingredient
                        Items.ICE,
                        // Output potion.
                        Registries.POTION.getEntry(SLIPPAGE_POTION)
                );
            });
            // Register the item group
            Registry.register(Registries.ITEM_GROUP, STUPID_FOOD_ITEM_GROUP, CUSTOM_ITEM_GROUP);

            // Add items to the group
            ItemGroupEvents.modifyEntriesEvent(STUPID_FOOD_ITEM_GROUP).register(itemGroup -> {
                itemGroup.add(BUTTER_COOKIE);
                itemGroup.add(SLIPPAGE_POTION_ITEM);
            });

        }

        public static final RegistryKey<ItemGroup> STUPID_FOOD_ITEM_GROUP =
                RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "stupid_food_group"));

        public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
                .icon(() -> new ItemStack(BUTTER_COOKIE))
                .displayName(Text.translatable("itemGroup." + MOD_ID))
                .build();
    }
}