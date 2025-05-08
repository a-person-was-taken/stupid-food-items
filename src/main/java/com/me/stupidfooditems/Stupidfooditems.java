package com.me.stupidfooditems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
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
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Function;

public class Stupidfooditems implements ModInitializer {
    @Override
    public void onInitialize() {
        StupidFoods.initialize();
    }
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

                if (Math.random() * 100 < (1.5 * amplifier)){
                    double slipStrength = 5 + (amplifier * 2);
                    double dir = (Math.random() * 2 - 1) * 180;
                    player.setVelocity(new Vec3d(Math.sin(dir) * slipStrength, slipStrength, Math.cos(dir) * slipStrength));
                    player.velocityModified = true;
                    player.networkHandler.sendPacket(
                            new TitleS2CPacket(Text.of("You slipped..."))
                    );                }

                return super.applyUpdateEffect(world, entity, amplifier);
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
                .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StupidFoods.SLIPPAGE, 60 * 20, 1), 1.0f))
                .build();

        public static final Item CIRCULAR_BUTTER_COOKIE = register("circular_butter_cookie", Item::new, new Item.Settings().food(new FoodComponent(2,3,true), BUTTER_COOKIE_CONSUMABLE_COMPONENT));
        public static final Item RECTANGULAR_BUTTER_COOKIE = register("rectangular_butter_cookie", Item::new, new Item.Settings().food(new FoodComponent(2,3,true), BUTTER_COOKIE_CONSUMABLE_COMPONENT));
        public static final Item TRIANGULAR_BUTTER_COOKIE = register("triangular_butter_cookie", Item::new, new Item.Settings().food(new FoodComponent(2,3,true), BUTTER_COOKIE_CONSUMABLE_COMPONENT));
        public static final Item UMBRELLA_BUTTER_COOKIE = register("umbrella_butter_cookie", Item::new, new Item.Settings().food(new FoodComponent(2,3,true), BUTTER_COOKIE_CONSUMABLE_COMPONENT));

        private static Object Settings;
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
                itemGroup.add(CIRCULAR_BUTTER_COOKIE);
                itemGroup.add(RECTANGULAR_BUTTER_COOKIE);
                itemGroup.add(TRIANGULAR_BUTTER_COOKIE);
                itemGroup.add(UMBRELLA_BUTTER_COOKIE);

                // Slippage Potions
                itemGroup.add(PotionContentsComponent.createStack(Items.POTION, RegistryEntry.of(SLIPPAGE_POTION)));
            });

        }

        public static final RegistryKey<ItemGroup> STUPID_FOOD_ITEM_GROUP =
                RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "stupid_food_group"));

        public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
                .icon(() -> new ItemStack(CIRCULAR_BUTTER_COOKIE))
                .displayName(Text.translatable("itemGroup." + MOD_ID))
                .build();
    }
}