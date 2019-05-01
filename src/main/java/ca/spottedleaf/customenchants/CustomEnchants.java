package ca.spottedleaf.customenchants;

import ca.spottedleaf.customenchants.enchantment.CustomEnchantmentTable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomEnchants extends JavaPlugin {

    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(new CustomEnchantmentTable(), this);
    }
}