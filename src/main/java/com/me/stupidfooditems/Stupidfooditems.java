package com.me.stupidfooditems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.component.type.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.*;
import net.minecraft.item.*;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

import static com.me.stupidfooditems.Stupidfooditems.StupidFoods.StupidFoodComponents.*;

public class Stupidfooditems implements ModInitializer {
    @Override
    public void onInitialize() {
        SimpleConfig.initialize();

        // Then register potions
        StupidPotions.SLIPPAGE_POTION = Registry.register(
                Registries.POTION,
                Identifier.of(MOD_ID, "slippage"),
                new Potion("slippage",
                        new StatusEffectInstance(
                                StupidEffects.SlippageEffectClass.SLIPPAGE,
                                3600,
                                0)));

        StupidPotions.STICKY_LEGS_POTION = Registry.register(
                Registries.POTION,
                Identifier.of(MOD_ID, "sticky_legs"),
                new Potion("sticky_legs",
                        new StatusEffectInstance(
                                StupidEffects.StickyLegsEffectClass.STICKY_LEGS,
                                3600,
                                0)));

        // Then initialize foods
        StupidFoods.initialize();
    }
    public static String MOD_ID = "stupidfooditems";
    public static class StupidEffects {
        public static class SlippageEffectClass {
            public static final RegistryEntry<StatusEffect> SLIPPAGE =
                    Registry.registerReference(
                            Registries.STATUS_EFFECT,
                            Identifier.of(MOD_ID, "slippage"),
                            new SlippageEffect()
                    );

            public static class SlippageEffect extends StatusEffect {
                protected SlippageEffect() {
                    super(StatusEffectCategory.HARMFUL, 0x75dfff);
                }

                @Override
                public boolean canApplyUpdateEffect(int duration, int amplifier) {
                    return true;
                }

                public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
                    // Check if the entity is a ServerPlayerEntity before casting
                    if (!(entity instanceof ServerPlayerEntity player)) return true;

                    Vec3d velocity = player.getVelocity();
                    double speed = Math.sqrt(Math.pow(velocity.x, 2) + Math.pow(velocity.y, 2) + Math.pow(velocity.z, 2));
                    if (!player.isOnGround() || speed == 0) return true;
                    double chance = Math.random() * 100;
                    if (chance < (0.5 * (amplifier + 1))) {
                        double slipStrength = 5 + (amplifier * 2);
                        double randYaw = (Math.random() * 2 - 1) * 180;
                        double randPitch = Math.abs(Math.random()) * 89.9;
                        player.setVelocity(new Vec3d(Math.cos(randYaw) * Math.cos(randPitch) * slipStrength, Math.sin(randPitch) * slipStrength, Math.sin(randYaw) * Math.cos(randPitch) * slipStrength));
                        player.velocityModified = true;
                        player.networkHandler.sendPacket(
                                new TitleS2CPacket(Text.of("You slipped..."))
                        );
                    }
                    chance = Math.random() * 100;
                    if (chance < 0.25 * amplifier) {
                        player.dropItem(player.getActiveItem(),true,false);
                    }
                    return super.applyUpdateEffect(world, entity, amplifier);
                }
            }
        }
        public static class StickyLegsEffectClass {
            public static final RegistryEntry<StatusEffect> STICKY_LEGS =
                    Registry.registerReference(
                            Registries.STATUS_EFFECT,
                            Identifier.of(MOD_ID, "sticky_legs"),
                            new StickyLegsEffect()
                    );

            public static class StickyLegsEffect extends StatusEffect {
                protected StickyLegsEffect() {
                    super(StatusEffectCategory.HARMFUL,0xffc014);
                }

                @Override
                public boolean canApplyUpdateEffect(int duration, int amplifier) {
                    return true;
                }

                public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
                    // Check if the entity is a ServerPlayerEntity before casting
                    if (!(entity instanceof ServerPlayerEntity player)) return true;

                    Vec3d playerVelocity = player.getVelocity();
                    double friction = ((1 - 0.15 * (amplifier + 1)) < 0 ? 0 : (1 - 0.15 * (amplifier + 1)));
                    if(playerVelocity.y > 0) {
                        player.setVelocity(new Vec3d(playerVelocity.x * friction, -playerVelocity.y, playerVelocity.z * friction));
                    }
                    else {
                        player.setVelocity(new Vec3d(playerVelocity.x * friction, playerVelocity.y, playerVelocity.z * friction));
                    }
                    player.velocityModified = true;
                    return super.applyUpdateEffect(world, entity, amplifier);
                }
            }
        }
    }

    public static class StupidPotions {
        public static Potion SLIPPAGE_POTION;
        public static Potion STICKY_LEGS_POTION;
    }

    public static class StupidFoods {

        public static void initialize() {

            // Register the potions
            FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> builder.registerPotionRecipe(
                    // Input potion.
                    Potions.AWKWARD,
                    // Ingredient
                    Items.ICE,
                    // Output potion.
                    Registries.POTION.getEntry(StupidPotions.SLIPPAGE_POTION)
            ));
            FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> builder.registerPotionRecipe(
                    // Input potion.
                    Potions.AWKWARD,
                    // Ingredient
                    Items.HONEY_BLOCK,
                    // Output potion.
                    Registries.POTION.getEntry(StupidPotions.STICKY_LEGS_POTION)
            ));
            // Register the item group
            Registry.register(Registries.ITEM_GROUP, STUPID_FOOD_ITEM_GROUP, CUSTOM_ITEM_GROUP);

            // Add items to the group
            ItemGroupEvents.modifyEntriesEvent(STUPID_FOOD_ITEM_GROUP).register(itemGroup -> {
                itemGroup.add(BUTTER_COOKIE);
                itemGroup.add(RECTANGULAR_BUTTER_COOKIE);
                itemGroup.add(TRIANGULAR_BUTTER_COOKIE);
                itemGroup.add(STAR_BUTTER_COOKIE);
                itemGroup.add(UMBRELLA_BUTTER_COOKIE);
                itemGroup.add(HONEY_COOKIE);
                itemGroup.add(DISGUSTING_COOKIE);
                itemGroup.add(NUKE_COOKIE);
                itemGroup.add(WONDERFUL_COOKIE);
                itemGroup.add(TNT_COOKIE);
                itemGroup.add(BOSS_COOKIE);

                // Potions
                itemGroup.add(PotionContentsComponent.createStack(Items.POTION, RegistryEntry.of(StupidPotions.SLIPPAGE_POTION)));
                itemGroup.add(PotionContentsComponent.createStack(Items.POTION, RegistryEntry.of(StupidPotions.STICKY_LEGS_POTION)));

            });

        }

        public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
            Item item = itemFactory.apply(settings.registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, item);
            return item;
        }

        public static class StupidFoodComponents {

            public static final FoodComponent BUTTER_COOKIE_FOOD_COMPONENT = new FoodComponent(2,3,true);
            public static final ConsumableComponent BUTTER_COOKIE_CONSUMABLE_COMPONENT = ConsumableComponents.food()
                    // The duration is in ticks, 20 ticks = 1 second
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StupidEffects.SlippageEffectClass.SLIPPAGE, 60 * 20, 1)))
                    .build();

            public static final ConsumableComponent HONEY_COOKIE_CONSUMABLE_COMPONENT = ConsumableComponents.food()
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StupidEffects.StickyLegsEffectClass.STICKY_LEGS, 60 * 20, 1),0.9f))
                    .build();

            public static final FoodComponent DISGUSTING_COOKIE_FOOD_COMPONENT = new FoodComponent(1, 0, true);
            public static final ConsumableComponent DISGUSTING_COOKIE_CONSUMABLE_COMPONENT = ConsumableComponents.food()
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * 20, 9), 1.0f))
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60 * 20, 9),1.0f))
                    .build();

            public static final ConsumableComponent WONDERFUL_COOKIE_CONSUMABLE_COMPONENT = ConsumableComponents.food()
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 60 * 2, 4), 1.0f))
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 30, 2), 1.0f))
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 60 * 5, 1), 1.0f))
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 60 * 5, 1), 1.0f))
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 60 * 5, 5), 1.0f))
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 60 * 5, 1), 1.0f))
                    .build();

            public static final ConsumableComponent GOLDEN_COOKIE = ConsumableComponents.food()
                    .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 10, 1), 1.0f))
        }

        public static final Item BUTTER_COOKIE = register("butter_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, BUTTER_COOKIE_CONSUMABLE_COMPONENT));
        public static final Item RECTANGULAR_BUTTER_COOKIE = register("rectangular_butter_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, BUTTER_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.UNCOMMON));
        public static final Item TRIANGULAR_BUTTER_COOKIE = register("triangular_butter_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, BUTTER_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.UNCOMMON));
        public static final Item STAR_BUTTER_COOKIE = register("star_butter_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, BUTTER_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.UNCOMMON));
        public static final Item UMBRELLA_BUTTER_COOKIE = register("umbrella_butter_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, BUTTER_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.UNCOMMON));
        public static final Item HONEY_COOKIE = register("honey_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, HONEY_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.RARE));
        public static final Item DISGUSTING_COOKIE = register("disgusting_cookie", Item::new, new Item.Settings().food(DISGUSTING_COOKIE_FOOD_COMPONENT, DISGUSTING_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.RARE));
        public static final Item NUKE_COOKIE = register("nuke_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT).rarity(Rarity.EPIC));
        public static final Item WONDERFUL_COOKIE = register("wonderful_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, WONDERFUL_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.EPIC));
        public static final Item TNT_COOKIE = register("tnt_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT, BUTTER_COOKIE_CONSUMABLE_COMPONENT).rarity(Rarity.RARE));
        public static final Item BOSS_COOKIE = register("boss_cookie", Item::new, new Item.Settings().food(BUTTER_COOKIE_FOOD_COMPONENT).rarity(Rarity.EPIC));

        public static final RegistryKey<ItemGroup> STUPID_FOOD_ITEM_GROUP =
                RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "stupid_food_group"));

        public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
                .icon(() -> new ItemStack(BUTTER_COOKIE))
                .displayName(Text.translatable("itemGroup." + MOD_ID))
                .build();
    }
}
