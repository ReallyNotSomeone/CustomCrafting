package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CauldronPreCookEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private int cookingTime;
    private boolean dropItems;
    private final Block cauldron;
    private final Player player;
    private CustomRecipeCauldron recipe;

    public CauldronPreCookEvent(CustomRecipeCauldron recipe, Player player, Block cauldron) {
        this.dropItems = recipe.dropItems();
        this.recipe = recipe;
        this.cookingTime = recipe.getCookingTime();
        this.player = player;
        this.cauldron = cauldron;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public CustomRecipeCauldron getRecipe() {
        return recipe;
    }

    public void setRecipe(CustomRecipeCauldron recipe) {
        this.recipe = recipe;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getCauldron() {
        return cauldron;
    }
}
