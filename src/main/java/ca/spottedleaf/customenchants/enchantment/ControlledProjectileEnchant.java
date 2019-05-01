package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.CustomEnchants;
import ca.spottedleaf.customenchants.util.ControlledProjectilesManager;
import org.bukkit.Bukkit;

public class ControlledProjectileEnchant extends Enchant {

    public final ControlledProjectilesManager controlledProjectiles;

    private final CustomEnchants plugin;

    public ControlledProjectileEnchant(final CustomEnchants plugin) {
        this.plugin = plugin;
        this.controlledProjectiles = new ControlledProjectilesManager();
    }

    @Override
    public void init() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, this.controlledProjectiles, 1L, 2L);
        Bukkit.getPluginManager().registerEvents(this.controlledProjectiles, this.plugin);
    }
}