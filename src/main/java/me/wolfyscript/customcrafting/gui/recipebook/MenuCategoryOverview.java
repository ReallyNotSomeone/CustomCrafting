/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.recipebook;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeContainer;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;

public class MenuCategoryOverview extends CCWindow {

    private static final String BACK = "back";
    private final BukkitTask ingredientTask;
    private final BukkitTask containerTask;

    MenuCategoryOverview(ClusterRecipeBook cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeBook.CATEGORY_OVERVIEW.getKey(), 54, customCrafting);
        this.ingredientTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
            for (int i = 0; i < 37; i++) {
                Button<CCCache> btn = cluster.getButton("ingredient.container_" + i);
                if (btn instanceof ButtonContainerIngredient cBtn) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> cBtn.getTasks().removeIf(Supplier::get));
                }
            }
        }, 1, 25);
        this.containerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
            for (int i = 0; i < 45; i++) {
                Button<CCCache> mainContainerBtn = cluster.getButton("recipe_book.container_" + i);
                if (mainContainerBtn instanceof ButtonContainerRecipeBook cBtn) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> cBtn.getTasks().removeIf(Supplier::get));
                }
            }
        }, 1, 25);
    }

    public void reset() {
        this.containerTask.cancel();
        this.ingredientTask.cancel();
    }

    @Override
    public void onInit() {
        getButtonBuilder().action(BACK).state(state -> state.key(ClusterMain.BACK_BOTTOM).icon(Material.BARRIER).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            ButtonContainerIngredient.resetButtons(guiHandler);
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            guiHandler.openPreviousWindow();
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        var configHandler = customCrafting.getConfigHandler();
        var player = event.getPlayer();
        CCPlayerData playerStore = PlayerUtil.getStore(player);
        var recipeBookCache = event.getGuiHandler().getCustomCache().getRecipeBookCache();
        if (recipeBookCache.getSubFolder() > 0) {
            event.getGuiHandler().openWindow(ClusterRecipeBook.RECIPE_BOOK);
            return;
        }
        if (customCrafting.getConfigHandler().getConfig().isGUIDrawBackground()) {
            for (int i = 0; i < 9; i++) {
                event.setButton(i, playerStore.getDarkBackground());
            }
        } else {
            for (int i = 0; i < 45; i++) {
                event.setButton(i, ClusterMain.EMPTY);
            }
        }
        List<RecipeContainer> containers = recipeBookCache.getCategory() != null ? recipeBookCache.getCategory().getRecipeList(player, recipeBookCache.getCategoryFilter().orElse(null), recipeBookCache.getEliteCraftingTable()) : new ArrayList<>();
        int maxPages = containers.size() / 45 + (containers.size() % 45 > 0 ? 1 : 0);
        if (recipeBookCache.getPage() >= maxPages) {
            recipeBookCache.setPage(0);
        }
        for (int item = 0, i = 45 * recipeBookCache.getPage(); item < 45 && i < containers.size(); i++, item++) {
            ButtonContainerRecipeBook button = (ButtonContainerRecipeBook) getCluster().getButton("recipe_book.container_" + item);
            if (button != null) {
                button.setRecipeContainer(event.getGuiHandler(), containers.get(i));
                event.setButton(item, ButtonContainerRecipeBook.namespacedKey(item));
            }
        }
        if (configHandler.getRecipeBookConfig().getSortedCategories().size() > 1) {
            event.setButton(45, BACK);
        }
        if (recipeBookCache.getPage() != 0) {
            event.setButton(47, ClusterRecipeBook.PREVIOUS_PAGE);
        }
        event.setButton(49, ClusterRecipeBook.ITEM_CATEGORY);
        if (recipeBookCache.getPage() + 1 < maxPages) {
            event.setButton(51, ClusterRecipeBook.NEXT_PAGE);
        }
    }

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        ButtonContainerIngredient.removeTasks(guiHandler);
        ButtonContainerRecipeBook.resetButtons(guiHandler);
        guiHandler.getCustomCache().getRecipeBookCache().setEliteCraftingTable(null);
        return super.onClose(guiHandler, guiInventory, transaction);
    }
}
