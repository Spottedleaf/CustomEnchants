package ca.spottedleaf.customenchants;

import ca.spottedleaf.customenchants.config.CustomEnchantsConfig;
import ca.spottedleaf.customenchants.enchantment.CustomEnchantmentTable;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomEnchants extends JavaPlugin {

    public final CustomEnchantsConfig config = new CustomEnchantsConfig();

    @Override
    public void onLoad() {
        this.config.load(this.getConfig());
    }

    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(new CustomEnchantmentTable(), this);
    }

    @Override
    public void onDisable() {}
}