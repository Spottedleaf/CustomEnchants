package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.tooltype.StandardToolTypes;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class TestEnchantment extends Enchant implements Listener {

    private static final NamespacedKey LEVEL_KEY = new NamespacedKey(PLUGIN, "level");

    public TestEnchantment() {
        super("test", "Test");
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, PLUGIN);
    }

    @Override
    public boolean isEnchantable(final ItemStack item){
        return StandardToolTypes.SWORD.matches(item);
    }
}