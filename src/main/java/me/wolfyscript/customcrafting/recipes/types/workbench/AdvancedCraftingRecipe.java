package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public abstract class AdvancedCraftingRecipe extends CraftingRecipe<AdvancedCraftingRecipe> {

    public AdvancedCraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public AdvancedCraftingRecipe() {
        super();
    }

    public AdvancedCraftingRecipe(AdvancedCraftingRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    @Override
    public RecipeType<AdvancedCraftingRecipe> getRecipeType() {
        return Types.WORKBENCH;
    }
}
