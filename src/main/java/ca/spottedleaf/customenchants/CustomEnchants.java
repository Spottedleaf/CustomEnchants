package ca.spottedleaf.customenchants;

import ca.spottedleaf.customenchants.enchantment.CustomEnchantmentTable;
import ca.spottedleaf.customenchants.enchantment.Enchant;
import ca.spottedleaf.customenchants.enchantment.EnchantmentManager;
import ca.spottedleaf.customenchants.enchantment.TestEnchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class CustomEnchants extends JavaPlugin {

    private EnchantmentManager enchantmentManager;

    @Override
    public void onEnable(){
        ArrayList<Enchant> enchantments = new ArrayList<Enchant>();
        enchantments.add(new TestEnchantment());
        enchantmentManager = new EnchantmentManager(enchantments);
        this.getServer().getPluginManager().registerEvents(new CustomEnchantmentTable(this, enchantmentManager), this);
    }


}