package ca.spottedleaf.customenchants;

import ca.spottedleaf.customenchants.config.CustomEnchantsConfig;
import ca.spottedleaf.customenchants.enchantment.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CustomEnchants extends JavaPlugin {

    public final CustomEnchantsConfig config = new CustomEnchantsConfig();
    private EnchantmentManager enchantmentManager;

    @Override
    public void onLoad() {
        this.config.load(this.getConfig());
    }

    @Override
    public void onEnable(){
        List<Enchant> enchantments = new ArrayList<Enchant>();
        enchantments.add(new TestEnchantment());
        enchantmentManager = new EnchantmentManager(enchantments);
        this.getServer().getPluginManager().registerEvents(new CustomEnchantmentTable(this, enchantmentManager), this);
        this.getServer().getPluginManager().registerEvents(new SmeltEnchant(this), this);
    }

    @Override
    public void onDisable() {}
}