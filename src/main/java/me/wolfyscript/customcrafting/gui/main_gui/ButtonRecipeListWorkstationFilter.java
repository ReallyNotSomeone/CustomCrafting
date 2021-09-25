package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

import java.util.*;

public class ButtonRecipeListWorkstationFilter extends ActionButton<CCCache> {

    static final String KEY = "workstation_filter";

    private static final LinkedHashMap<RecipeType<?>, ItemStack> FILTERS = new LinkedHashMap<>();
    private static final List<RecipeType<?>> FILTER_KEYS;

    static {
        FILTERS.put(null, new ItemStack(Material.COMPASS));
        FILTERS.put(RecipeType.CRAFTING_SHAPED, new ItemStack(Material.CRAFTING_TABLE));
        FILTERS.put(RecipeType.CRAFTING_SHAPELESS, new ItemStack(Material.CRAFTING_TABLE));
        FILTERS.put(RecipeType.FURNACE, new ItemStack(Material.FURNACE));
        FILTERS.put(RecipeType.BLAST_FURNACE, new ItemStack(Material.BLAST_FURNACE));
        FILTERS.put(RecipeType.SMOKER, new ItemStack(Material.SMOKER));
        FILTERS.put(RecipeType.CAMPFIRE, new ItemStack(Material.CAMPFIRE));
        FILTERS.put(RecipeType.SMITHING, new ItemStack(Material.SMITHING_TABLE));
        FILTERS.put(RecipeType.STONECUTTER, new ItemStack(Material.STONECUTTER));
        FILTERS.put(RecipeType.ELITE_CRAFTING_SHAPED, new ItemStack(Material.CRAFTING_TABLE));
        FILTERS.put(RecipeType.ELITE_CRAFTING_SHAPELESS, new ItemStack(Material.CRAFTING_TABLE));
        FILTER_KEYS = new ArrayList<>(FILTERS.keySet());
    }

    ButtonRecipeListWorkstationFilter() {
        super(KEY, Material.COMPASS, (cache, guiHandler, player, guiInventory, i, event) -> {
            var currentType = cache.getRecipeList().getFilterType();
            if (event instanceof InventoryClickEvent clickEvent) {
                var nextIndex = FILTER_KEYS.indexOf(currentType);
                if (clickEvent.isLeftClick()) {
                    if (++nextIndex >= FILTER_KEYS.size()) {
                        nextIndex = 0;
                    }
                } else if (--nextIndex < 0) {
                    nextIndex = FILTER_KEYS.size() - 1;
                }
                cache.getRecipeList().setFilterType(FILTER_KEYS.get(nextIndex));
            }
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            RecipeType<?> type = cache.getRecipeList().getFilterType();
            values.put("%type%", type != null ? type.getType().toString().replace("CRAFTING", "").replace("_CRAFTING", "").replace("_", " ") : "ALL");
            itemStack.setType(FILTERS.get(type).getType());
            return itemStack;
        });
    }


}
