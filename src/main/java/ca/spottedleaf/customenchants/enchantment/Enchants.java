package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.CustomEnchants;
import org.bukkit.plugin.java.JavaPlugin;

public final class Enchants {

    private static final CustomEnchants PLUGIN = JavaPlugin.getPlugin(CustomEnchants.class);

    public static final ControlledProjectileEnchant CONTROLLED_PROJECTILE_ENCHANT = new ControlledProjectileEnchant();

}