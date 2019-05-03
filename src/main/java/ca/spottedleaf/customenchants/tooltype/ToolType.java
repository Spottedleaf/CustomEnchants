package ca.spottedleaf.customenchants.tooltype;

import org.bukkit.inventory.ItemStack;

/**
 * Interface for defining a set of tools. Each tool type must recognize {@code null} as being invalid by
 * returning {@code false}.
 */
@FunctionalInterface
public interface ToolType {

    /**
     * Returns whether the specified item matches this type.
     * @param item The item to check against
     * @return Whether the specified item matches this type.
     */
    boolean matches(final ItemStack item);
}