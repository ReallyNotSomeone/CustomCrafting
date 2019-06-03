package me.wolfyscript.customcrafting.recipes.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShapelessCraftRecipe extends ShapelessRecipe implements CraftingRecipe {

    private boolean permission;
    private boolean advancedWorkbench;
    private boolean exactMeta;
    private RecipePriority priority;

    private CraftConfig config;
    private String id;
    private String group;
    private CustomItem result;
    private HashMap<Character, ArrayList<CustomItem>> ingredients;
    private WolfyUtilities api;

    public ShapelessCraftRecipe(CraftConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
        this.permission = config.needPerm();
        this.advancedWorkbench = config.needWorkbench();
        this.group = config.getGroup();
        this.priority = config.getPriority();
        this.api = CustomCrafting.getApi();
        this.exactMeta = config.isExactMeta();
        load();
    }

    @Override
    public void load() {
        for (Character itemKey : getIngredients().keySet()) {
            List<CustomItem> items = getIngredients().get(itemKey);
            List<Material> materials = new ArrayList<>();
            items.forEach(itemStack -> materials.add(itemStack.getType()));
            this.addIngredient(new RecipeChoice.MaterialChoice(materials));
        }
    }

    @Override
    public void save() {

    }

    @Override
    public boolean check(List<List<ItemStack>> matrix) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for (List<ItemStack> items : matrix) {
            for (ItemStack itemStack : items) {
                if (itemStack == null) {
                    continue;
                }
                checkIngredient(allKeys, usedKeys, itemStack);
            }
        }
        return usedKeys.containsAll(getIngredients().keySet());
    }

    private CustomItem checkIngredient(List<Character> allKeys, List<Character> usedKeys, ItemStack item) {
        for (Character key : allKeys) {
            if (!usedKeys.contains(key)) {
                for (CustomItem ingredient : ingredients.get(key)) {
                    if (item.getType().equals(ingredient.getType()) && item.getAmount() >= ingredient.getAmount()) {
                        if (exactMeta || ingredient.hasItemMeta()) {
                            if (ingredient.hasItemMeta() && !item.hasItemMeta()) {
                                continue;
                            }else if(!ingredient.hasItemMeta() && item.hasItemMeta()){
                                continue;
                            }
                            if (!item.getItemMeta().equals(ingredient.getItemMeta())) {
                                continue;
                            }
                        }
                        usedKeys.add(key);
                        return ingredient.clone();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<ItemStack> removeMatrix(List<List<ItemStack>> matrix, CraftingInventory inventory, boolean small, int totalAmount) {
        List<ItemStack> replacements = new ArrayList<>();
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for (int i = 0; i < inventory.getMatrix().length; i++) {
            ItemStack input = inventory.getMatrix()[i];
            if (input != null) {
                CustomItem item = checkIngredient(allKeys, usedKeys, input);
                if (item != null) {
                    if (item.getMaxStackSize() > 1) {
                        int amount = input.getAmount() - item.getAmount() * totalAmount;
                        input.setAmount(amount);
                        if (item.hasReplacement()) {
                            ItemStack replacement = item.getReplacement();
                            replacement.setAmount(replacement.getAmount() * totalAmount);
                            replacements.add(replacement);
                        }
                    } else {
                        if (item.getMaxStackSize() > 1) {
                            int amount = input.getAmount() - item.getAmount() * totalAmount;
                            input.setAmount(amount);
                            if (item.hasReplacement()) {
                                ItemStack replacement = item.getReplacement();
                                replacement.setAmount(replacement.getAmount() * totalAmount);
                                //TODO: CHECK
                                if (ItemUtils.hasInventorySpace(inventory, replacement)) {
                                    inventory.addItem(replacement);
                                } else {
                                    inventory.getLocation().getWorld().dropItemNaturally(inventory.getLocation().add(0.5, 1.0, 0.5), replacement);
                                }
                            }
                        } else {
                            if (item.hasConfig()) {
                                if (item.hasReplacement()) {
                                    ItemStack replace = item.getReplacement();
                                    input.setType(replace.getType());
                                    input.setItemMeta(replace.getItemMeta());
                                    input.setData(replace.getData());
                                    input.setAmount(replace.getAmount());
                                } else if (item.getDurabilityCost() != 0) {
                                    ItemMeta itemMeta = input.getItemMeta();
                                    if (itemMeta instanceof Damageable) {
                                        ((Damageable) itemMeta).setDamage(((Damageable) itemMeta).getDamage() + item.getDurabilityCost());
                                    }
                                    input.setItemMeta(itemMeta);
                                } else {
                                    input.setAmount(0);
                                }
                            } else {
                                if (input.getType().equals(Material.LAVA_BUCKET) || input.getType().equals(Material.LAVA_BUCKET)) {
                                    input.setType(Material.BUCKET);
                                } else {
                                    input.setAmount(0);
                                }
                            }
                        }
                    }
                    //TEST FOR BUCKETS AND OTHER ITEMS!?
                }
            }
        }
        return replacements;
    }

    @Override
    public int getAmountCraftable(List<List<ItemStack>> matrix) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        int totalAmount = -1;
        for (List<ItemStack> items : matrix) {
            for (ItemStack itemStack : items) {
                if (itemStack != null) {
                    ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
                    if (result != null) {
                        int possible = itemStack.getAmount() / result.getAmount();
                        if (possible < totalAmount || totalAmount == -1)
                            totalAmount = possible;
                    }
                }
            }
        }
        return totalAmount;
    }

    public void setIngredients(HashMap<Character, ArrayList<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public HashMap<Character, ArrayList<CustomItem>> getIngredients() {
        return ingredients;
    }

    public void setResult(ItemStack result) {
        this.result = new CustomItem(result);
    }

    @Override
    public void setAdvancedWorkbench(boolean advancedWorkbench) {
        this.advancedWorkbench = advancedWorkbench;
    }

    @Override
    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    public CustomItem getCustomResult() {
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean needsPermission() {
        return permission;
    }

    @Override
    public boolean needsAdvancedWorkbench() {
        return advancedWorkbench;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    public CraftConfig getConfig() {
        return config;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }
}
