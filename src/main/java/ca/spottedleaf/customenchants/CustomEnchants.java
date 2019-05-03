package ca.spottedleaf.customenchants;

import ca.spottedleaf.customenchants.config.CustomEnchantsConfig;
import ca.spottedleaf.customenchants.enchantment.Enchants;
import ca.spottedleaf.customenchants.inventory.CustomEnchantmentTable;
import ca.spottedleaf.customenchants.enchantment.Enchant;
import ca.spottedleaf.customenchants.listener.EnchantmentListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomEnchants extends JavaPlugin {

    public final CustomEnchantsConfig config = new CustomEnchantsConfig();
    public final EnchantmentManager enchantmentManager = new EnchantmentManager();

    @Override
    public void onLoad() {
        this.config.load(this.getConfig());
        Enchants.init();
    }

    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(new CustomEnchantmentTable(this, enchantmentManager), this);
        Bukkit.getPluginManager().registerEvents(new EnchantmentListener(), this);
        Enchant.ENCHANTS.forEach(Enchant::init);
    }

    @Override
    public void onDisable() {
        Enchant.ENCHANTS.forEach(Enchant::shutdown);
    }
}