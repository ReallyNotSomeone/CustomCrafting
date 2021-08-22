package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

public class CraftingRecipeShaped extends AbstractRecipeShaped<CraftingRecipeShaped, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapedRecipe> {

    public CraftingRecipeShaped(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    public CraftingRecipeShaped(NamespacedKey key) {
        super(key, 3, new AdvancedRecipeSettings());
    }

    public CraftingRecipeShaped(CraftingRecipeShaped craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeShaped> getRecipeType() {
        return RecipeType.CRAFTING_SHAPED;
    }

    @Override
    public CraftingRecipeShaped clone() {
        return new CraftingRecipeShaped(this);
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe getVanillaRecipe() {
        if (!getResult().isEmpty() && !ingredients.isEmpty()) {
            var recipe = new org.bukkit.inventory.ShapedRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            recipe.shape(getShape());
            mappedIngredients.forEach((character, items) -> recipe.setIngredient(character, new RecipeChoice.ExactChoice(items.getChoices().parallelStream().map(CustomItem::create).distinct().toList())));
            recipe.setGroup(getGroup());
            return recipe;
        }
        return null;
    }
}