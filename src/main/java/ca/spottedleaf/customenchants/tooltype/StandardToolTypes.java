package ca.spottedleaf.customenchants.tooltype;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Simple implementations of {@link ToolType}.
 *
 * @see ToolType
 * @see ToolTypeBuilder
 */
public enum StandardToolTypes implements ToolType {

    /* Bow */
    BOW(Material.BOW, Material.CROSSBOW),

    /* Arrow types */
    ARROW(Material.ARROW, Material.SPECTRAL_ARROW, Material.TIPPED_ARROW),

    /* Items which can become projectiles */
    PROJECTILES(Material.ARROW, Material.SPECTRAL_ARROW, Material.TIPPED_ARROW,
            Material.EXPERIENCE_BOTTLE, Material.EGG, Material.SNOWBALL, Material.SPLASH_POTION, Material.LINGERING_POTION,
            Material.FIRE_CHARGE, Material.TRIDENT, Material.ENDER_PEARL, Material.ENDER_EYE, Material.FIREWORK_ROCKET),


    /* Hoe */
    HOE(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.DIAMOND_HOE, Material.GOLDEN_HOE),

    /* Armour */
    BOOTS(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.GOLDEN_BOOTS),
    CHESTPLATE(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.GOLDEN_CHESTPLATE),
    HELMET(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.GOLDEN_HELMET),
    LEGGINGS(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.GOLDEN_LEGGINGS),

    /**
     * Consists of {@link #BOOTS}, {@link #CHESTPLATE}, {@link #HELMET}, and {@link #LEGGINGS}.
     */
    GENERIC_ARMOUR(StandardToolTypes.BOOTS, StandardToolTypes.CHESTPLATE, StandardToolTypes.HELMET, StandardToolTypes.LEGGINGS),

    /* Tools (Sword, Pickaxe, Axe, Spade) */
    AXE(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.DIAMOND_AXE, Material.GOLDEN_AXE),
    PICKAXE(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.GOLDEN_PICKAXE),
    SPADE(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.DIAMOND_SHOVEL, Material.GOLDEN_SHOVEL),
    SWORD(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.GOLDEN_SWORD),

    /**
     * Consists of {@link #AXE}, {@link #PICKAXE}, {@link #SPADE}.
     */
    GENERIC_TOOL(StandardToolTypes.AXE, StandardToolTypes.PICKAXE, StandardToolTypes.SPADE),

    /**
     * Consists of {@link #ARROW}, {@link #BOW}, {@link #HOE}, {@link #SWORD}, {@link #GENERIC_ARMOUR}, {@link #GENERIC_TOOL}.
     */
    ALL_TOOLS(StandardToolTypes.ARROW, StandardToolTypes.BOW, StandardToolTypes.HOE, StandardToolTypes.SWORD, StandardToolTypes.GENERIC_ARMOUR, StandardToolTypes.GENERIC_TOOL);

    final EnumSet<Material> matchTo;

    private StandardToolTypes(final StandardToolTypes... types) {
        final EnumSet<Material> ret = EnumSet.noneOf(Material.class);
        for (StandardToolTypes type : types) {
            ret.addAll(type.matchTo);
        }
        this.matchTo = ret;
    }

    private StandardToolTypes(final Material... match) {
        this.matchTo = EnumSet.copyOf(Arrays.asList(match));
    }

    @Override
    public boolean matches(final ItemStack item) {
        if (item == null) {
            return false;
        }

        final Material type = item.getType();
        return this.matchTo.contains(type);
    }
}