package com.me.stupidfooditems.data.provider;

import com.me.stupidfooditems.Stupidfooditems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StupidRecipeProvider extends FabricRecipeProvider {
    public StupidRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture){
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return null;
    }

    public void generate(RecipeExporter exporter){
        ShapelessRecipeJsonBuilder.create(new RegistryEntryLookup<Item>() {
                    @Override
                    public Optional<RegistryEntry.Reference<Item>> getOptional(RegistryKey<Item> key) {
                        return Optional.empty();
                    }

                    @Override
                    public Optional<RegistryEntryList.Named<Item>> getOptional(TagKey<Item> tag) {
                        return Optional.empty();
                    }
                }, RecipeCategory.FOOD, Stupidfooditems.StupidFoods.HONEY_COOKIE, 1)
                .input(Stupidfooditems.StupidFoods.BUTTER_COOKIE)
                .offerTo(exporter);
    }

    @Override
    public String getName() {
        return "stupidrecipeprovider";
    }
}
