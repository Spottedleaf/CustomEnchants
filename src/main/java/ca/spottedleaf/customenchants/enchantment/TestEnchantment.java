package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.tooltype.StandardToolTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class TestEnchantment extends Enchant implements Listener {

    private static final NamespacedKey LEVEL_KEY = new NamespacedKey(PLUGIN, "level");

    public TestEnchantment() {
        super("test", "Test");
    }

    @Override
    public boolean isEnchantable(final ItemStack item){
        return StandardToolTypes.SWORD.matches(item);
    }
}