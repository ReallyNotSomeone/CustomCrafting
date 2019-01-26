package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShapedCraftRecipe extends ShapedRecipe implements CraftingRecipe {

    boolean permission;
    boolean advancedWorkbench;

    CraftConfig config;
    String id;
    String group;
    ItemStack result;
    HashMap<Character, HashMap<ItemStack, List<String>>> ingredients;
    String[] shape;
    String shapeLine;

    public ShapedCraftRecipe(CraftConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.shape = config.getShape();
        this.ingredients = config.getIngredients();
        this.group = config.getGroup();
    }

    public void load() {
        shape(shape);
        for (Character itemKey : ingredients.keySet()) {
            Set<ItemStack> items = ingredients.get(itemKey).keySet();
            List<Material> materials = new ArrayList<>();
            items.forEach(itemStack -> materials.add(itemStack.getType()));
            setIngredient(itemKey, new RecipeChoice.MaterialChoice(materials));
        }
        if (!this.group.isEmpty()) {
            setGroup(group);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String row : shape) {
            for (Character letter : row.toCharArray()) {
                if (!letter.equals(' ')) {
                    stringBuilder.append(letter);
                }
            }
        }
        shapeLine = stringBuilder.toString();
    }

    public boolean check(ItemStack[] matrix) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack itemStack : matrix) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                items.add(itemStack);
            }
        }
        int index = 0;
        for (Character letter : shapeLine.toCharArray()) {
            if (!letter.equals(' ')) {
                boolean contains = false;
                for (ItemStack itemStack : ingredients.get(letter).keySet()) {
                    //TODO: SPACE FOR EXTRA DATA CHECK!
                    if (items.get(index).getAmount() >= itemStack.getAmount() && items.get(index).isSimilar(itemStack)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    return false;
                }
            }
            index++;
        }
        return true;
    }


    public String getId() {
        return id;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean needsPermission() {
        return permission;
    }

    public boolean needsAdvancedWorkbench() {
        return advancedWorkbench;
    }

    public CraftConfig getConfig() {
        return config;
    }
}
