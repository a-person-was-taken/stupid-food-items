package com.me.stupidfooditems.client;

import com.me.stupidfooditems.Stupidfooditems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class StupidfooditemsDataGenerator implements DataGeneratorEntrypoint {

    public static class StupidRecipeProvider extends FabricRecipeProvider {
        public StupidRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
            return new RecipeGenerator(registryLookup, exporter) {
                @Override
                public void generate() {
                    createShapeless(RecipeCategory.FOOD, Stupidfooditems.StupidFoods.HONEY_COOKIE) // You can also specify an int to produce more than one
                            .input(Stupidfooditems.StupidFoods.BUTTER_COOKIE) // You can also specify an int to require more than one, or a tag to accept multiple things
                            .input(Items.HONEY_BLOCK)
                            // Create an advancement that gives you the recipe
                            .criterion(hasItem(Items.HONEY_BLOCK), conditionsFromItem(Items.HONEY_BLOCK))
                            .offerTo(exporter);
                }
            };
        }

        @Override
        public String getName() {
            return "StupidRecipeProvider";
        }
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(StupidRecipeProvider::new);
    }
}
