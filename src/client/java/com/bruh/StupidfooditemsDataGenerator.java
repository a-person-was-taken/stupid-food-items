package com.bruh;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StupidfooditemsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(StupidRecipeProvider::new);
		pack.addProvider(StupidItemTagProvider::new);
		pack.addProvider(StupidBlockLootTableProvider::new);
	}

	static class StupidBlockLootTableProvider extends FabricBlockLootTableProvider {
		protected StupidBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generate() {
			addDrop(Stupidfooditems.StupidBlocks.COOKIE_BLOCK);
		}
	}

	static class StupidItemTagProvider extends FabricTagProvider<Item> {
		public StupidItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, RegistryKeys.ITEM, registriesFuture);
		}

		final TagKey<Item> BUTTER_COOKIES = TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies"));

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			getOrCreateTagBuilder(BUTTER_COOKIES)
					.add(Stupidfooditems.StupidFoods.BUTTER_COOKIE)
					.add(Stupidfooditems.StupidFoods.RECTANGULAR_BUTTER_COOKIE)
					.add(Stupidfooditems.StupidFoods.STAR_BUTTER_COOKIE)
					.add(Stupidfooditems.StupidFoods.TRIANGULAR_BUTTER_COOKIE)
					.add(Stupidfooditems.StupidFoods.UMBRELLA_BUTTER_COOKIE)
					.setReplace(true);
		}
	}

	static class StupidRecipeProvider extends FabricRecipeProvider {
		public StupidRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
			return new RecipeGenerator(registryLookup, exporter) {
				@Override
				public void generate() {
					//Shapeless recipes
					createShapeless(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.BUTTER_COOKIE) // You can also specify an int to produce more than one
							.input(Items.WHEAT, 2) // You can also specify an int to require more than one, or a tag to accept multiple things
							.input(Items.SUGAR)
							// Create an advancement that gives you the recipe
							.criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
							.criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
							.offerTo(exporter);
					createShapeless(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.BOSS_COOKIE)
							.input(Items.WITHER_SKELETON_SKULL)
							.input(Stupidfooditems.StupidFoods.NUKE_COOKIE)
							// Create an advancement that gives you the recipe
							.criterion(hasItem(Items.WITHER_SKELETON_SKULL), conditionsFromItem(Items.WITHER_SKELETON_SKULL))
							.criterion(hasItem(Stupidfooditems.StupidFoods.NUKE_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.NUKE_COOKIE))
							.offerTo(exporter);
					createShapeless(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.DISGUSTING_COOKIE)
							.input(Items.SLIME_BLOCK)
							.input(Items.ROTTEN_FLESH)
							.input(TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies")))
							// Create an advancement that gives you the recipe
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.criterion(hasItem(Items.SLIME_BALL), conditionsFromItem(Items.SLIME_BALL))
							.criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH))
							.offerTo(exporter);
					createShapeless(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.HONEY_COOKIE)
							.input(Items.HONEY_BOTTLE)
							.input(TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies")))
							// Create an advancement that gives you the recipe
							.criterion(hasItem(Items.HONEY_BOTTLE), conditionsFromItem(Items.HONEY_BOTTLE))
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.offerTo(exporter);

					//Shaped recipes
					createShaped(RecipeCategory.COMBAT, Stupidfooditems.StupidFoods.COOKIE_BOX)
							.pattern("lcl")
							.pattern("cic")
							.pattern("lcl")
							.input('l', Items.LAPIS_LAZULI)
							.input('i', Items.IRON_INGOT)
							.input('c', TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies"))) // 'c' means "any butter cookie"
							.group("cookie_box") // Put it in a group called "cookie_box" - groups are shown in one slot in the recipe book
							.criterion(hasItem(Items.LAPIS_LAZULI), conditionsFromItem(Items.LAPIS_LAZULI))
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.offerTo(exporter);

					createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.IRON_COOKIE)
							.pattern("bib")
							.pattern("ici")
							.pattern("bib")
							.input('c', TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies")))
							.input('i', Items.IRON_INGOT)
							.input('b', Items.IRON_BLOCK)
							.criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
							.criterion(hasItem(Items.IRON_BLOCK), conditionsFromItem(Items.IRON_BLOCK))
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.offerTo(exporter);
					createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.GOLDEN_COOKIE)
							.pattern("gbg")
							.pattern("bcb")
							.pattern("gbg")
							.input('c', TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies")))
							.input('g', Items.GOLD_INGOT)
							.input('b', Items.GOLD_BLOCK)
							.criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
							.criterion(hasItem(Items.GOLD_BLOCK), conditionsFromItem(Items.GOLD_BLOCK))
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.offerTo(exporter);
					createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.DIAMOND_COOKIE)
							.pattern("bdb")
							.pattern("dcd")
							.pattern("bdb")
							.input('c', TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies")))
							.input('d', Items.DIAMOND)
							.input('b', Items.DIAMOND_BLOCK)
							.criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
							.criterion(hasItem(Items.DIAMOND_BLOCK), conditionsFromItem(Items.DIAMOND_BLOCK))
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.offerTo(exporter);
					createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.NETHERITE_COOKIE)
							.pattern("ana")
							.pattern("ncn")
							.pattern("ana")
							.input('c', TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies")))
							.input('n', Items.NETHERITE_INGOT)
							.input('a', Items.ANCIENT_DEBRIS)
							.criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
							.criterion(hasItem(Items.DIAMOND_BLOCK), conditionsFromItem(Items.DIAMOND_BLOCK))
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.offerTo(exporter);
					createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.NUKE_COOKIE)
							.pattern("ttt")
							.pattern("tct")
							.pattern("ttt")
							.input('c', Stupidfooditems.StupidFoods.TNT_COOKIE)
							.input('t', Items.TNT)
							.criterion(hasItem(Items.TNT), conditionsFromItem(Items.TNT))
							.criterion(hasItem(Stupidfooditems.StupidFoods.DISGUSTING_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.DISGUSTING_COOKIE))
							.offerTo(exporter);
					createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.TNT_COOKIE)
							.pattern("ttt")
							.pattern("tct")
							.pattern("ttt")
							.input('c', TagKey.of(RegistryKeys.ITEM, Identifier.of(Stupidfooditems.MOD_ID, "butter_cookies")))
							.input('t', Items.TNT)
							.criterion(hasItem(Items.TNT), conditionsFromItem(Items.TNT))
							.criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
							.offerTo(exporter);
					createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.WONDERFUL_COOKIE)
							.pattern("ndn")
							.pattern("dgd")
							.pattern("ndn")
							.input('g', Stupidfooditems.StupidFoods.GOLDEN_COOKIE)
							.input('n', Items.NETHERITE_BLOCK)
							.input('d', Items.DIAMOND_BLOCK)
							.criterion(hasItem(Items.DIAMOND_BLOCK), conditionsFromItem(Items.DIAMOND_BLOCK))
							.criterion(hasItem(Stupidfooditems.StupidFoods.GOLDEN_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.GOLDEN_COOKIE))
							.offerTo(exporter);

					//Furnace Recipes
					offerSmelting(
							List.of(Stupidfooditems.StupidFoods.BUTTER_COOKIE), // Inputs
							RecipeCategory.FOOD, // Category
							Stupidfooditems.StupidFoods.BURNT_COOKIE, // Output
							2.5f, // Experience
							300, // Cooking time
							"regular_to_burnt_cookie" // group
					);
					offerSmelting(
							List.of(Stupidfooditems.StupidFoods.BURNT_COOKIE), // Inputs
							RecipeCategory.FOOD, // Category
							Stupidfooditems.StupidFoods.LAVA_COOKIE, // Output
							5f, // Experience
							300, // Cooking time
							"burnt_to_lava_cookie" // group
					);
				}
			};
		}

		@Override
		public String getName() {
			return "StupidRecipeProvider";
		}
	}
}
