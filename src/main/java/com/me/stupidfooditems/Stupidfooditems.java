package com.me.stupidfooditems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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
        StupidFoods.initialize();
    }
    static String MOD_ID = "stupidfooditems";
    public static class StupidFoods {

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

            public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
                // Your effect logic here
                BlockPos pos = entity.getBlockPos().down();
                BlockState state = entity.getWorld().getBlockState(pos);

                // Skip if already on slippery blocks
                if (state.getBlock().getSlipperiness() >= 0.98f) return true;

                double multiplier = 1.0 + (0.1 * (amplifier + 1));

                // Apply slippery physics
                Vec3d velocity = entity.getVelocity();
                if (velocity.horizontalLengthSquared() > 0.0001) {
                    Vec3d newVelocity = velocity.multiply(multiplier, 1.0, multiplier);

                    // Add slight downward force for more slide effect
                    if (entity.isOnGround()) {
                        newVelocity = newVelocity.add(0, -0.02, 0);
                    }

                    entity.setVelocity(newVelocity);
                    entity.velocityModified = true;
                }
                return true;
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

        public static final Item BUTTER_COOKIE = register("butter_cookie", Item::new, new Item.Settings().food(new FoodComponent.Builder().build()));

        public static void initialize() {
            // Register the item group
            Registry.register(Registries.ITEM_GROUP, STUPID_FOOD_ITEM_GROUP, CUSTOM_ITEM_GROUP);

            // Add items to the group
            ItemGroupEvents.modifyEntriesEvent(STUPID_FOOD_ITEM_GROUP).register(itemGroup -> {
                itemGroup.add(BUTTER_COOKIE);
            });

        }

        // ✅ Use MOD_ID instead of stupidfooditems
        public static final RegistryKey<ItemGroup> STUPID_FOOD_ITEM_GROUP =
                RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "stupid_food_group"));

        public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
                .icon(() -> new ItemStack(BUTTER_COOKIE))
                .displayName(Text.translatable("itemGroup." + MOD_ID))
                .build();
    }
}