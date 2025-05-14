package com.me.stupidfooditems.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class StupidfooditemsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(StupidRecipeProvider::new);
    }
}

public class StupidRecipeProvider extends FabricRecipeProvider {
    public StupidRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

    @Override
	protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
		return new RecipeGenerator(registryLookup, exporter) {
			@Override
			public void generate() {
				RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

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
		            .input(ItemTags.BUTTER_COOKIES)
                    // Create an advancement that gives you the recipe
		            .criterion(hasItem(ItemTags.BUTTER_COOKIES), conditionsFromItem(ItemTags.BUTTER_COOKIES))
                    .criterion(hasItem(Items.SLIME_BALL), conditionsFromItem(Items.SLIME_BALL))
                    .criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH))
		            .offerTo(exporter);
                createShapeless(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.HONEY_COOKIE)
		            .input(Items.HONEY_BOTTLE)
		            .input(ItemTags.BUTTER_COOKIES)
                    // Create an advancement that gives you the recipe
		            .criterion(hasItem(Items.HONEY_BOTTLE), conditionsFromItem(Items.HONEY_BOTTLE))
                    .criterion(hasItem(ItemTags.BUTTER_COOKIES), conditionsFromItem(ItemTags.BUTTER_COOKIES))
		            .offerTo(exporter);

                //Shaped recipes
                createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.COOKIE_BOX)
		            .pattern("lcl")
		            .pattern("cic")
                    .pattern("lcl")
	            	.input('l', Items.LAPIS_LAZULI) 
                    .input('i', Items.IRON_INGOT)
                    .input('c', ItemTags.BUTTER_COOKIES) // 'c' means "any butter cookie"
		            .group("cookie_box") // Put it in a group called "cookie_box" - groups are shown in one slot in the recipe book
            		.criterion(hasItem(Items.LAPIS_LAZULI), conditionsFromItem(Items.LAPIS_LAZULI))
                    .criterion(hasItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE), conditionsFromItem(Stupidfooditems.StupidFoods.BUTTER_COOKIE))
	            	.offerTo(exporter);
                createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.WONDERFUL_COOKIE)
		            .pattern("gbg")
		            .pattern("bcb")
                    .pattern("gbg")
	            	.input('c', ItemTags.BUTTER_COOKIES) 
                    .input('g', Items.GOLD_INGOT)
                    .input('b', Items.GOLD_BLOCK)
            		.criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
            		.criterion(hasItem(Items.GOLD_BLOCK), conditionsFromItem(Items.GOLD_BLOCK))
                    .criterion(hasItem(ItemTags.BUTTER_COOKIES), conditionsFromItem(ItemTags.BUTTER_COOKIES))
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
	            	.input('c', ItemTags.BUTTER_COOKIES) 
                    .input('t', Items.TNT)
            		.criterion(hasItem(Items.TNT), conditionsFromItem(Items.TNT))
                    .criterion(hasItem(ItemTags.BUTTER_COOKIES), conditionsFromItem(ItemTags.BUTTER_COOKIES))
	            	.offerTo(exporter);
                createShaped(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.WONDERFUL_COOKIE)
		            .pattern("ndn")
		            .pattern("dgd")
                    .pattern("ndn")
	            	.input('g', Stupidfooditems.StupidFoods.GOLDEN_COOKIE) 
                    .input('n', Items.NETHERITE_BLOCK)
                    .input('d', Items.DIAMOND_BLOCK)
            		.criterion(hasItem(Items.DIAMOND_BLOCK), conditionsFromItem(Items.DIAMOND_BLOCK))
                    .criterion(hasItem(StupidFoodItems.StupidFoods.GOLDEN_COOKIE), conditionsFromItem(StupidFoodItems.StupidFoods.GOLDEN_COOKIE))
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
