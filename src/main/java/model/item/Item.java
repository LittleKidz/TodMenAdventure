package model.item;

import model.entity.Entity;
import model.interfaces.Applicable;

/**
 * Base class for all item types in the game.
 * Every item can be applied to an entity through the Applicable interface.
 */
public abstract class Item implements Applicable {

    /** Item name shown in the UI. */
    protected String name;

    /**
     * Creates an item with the given name.
     *
     * @param name Item name
     */
    public Item(String name) {
        this.name = name;
    }

    /**
     * Returns the item name.
     *
     * @return Item name
     */
    public String getName() {
        return name;
    }

    /**
     * Applies the item's effect to the target entity. Each subclass overrides this because effects differ.
     *
     * @param target Entity to receive the effect
     */
    @Override
    public abstract void apply(Entity target);

    /**
     * Returns a description of the item's effect for display in the UI.
     *
     * @return Effect description text
     */
    @Override
    public abstract String getEffectDescription();
}
