package ca.spottedleaf.customenchants.enchantment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestEnchantment extends Enchant {

    public TestEnchantment() {
        super("test");
    }

    @Override
    public void enchant(ItemStack item){}

    @Override
    public boolean isEnchantable(ItemStack item){
        return item.getType() == Material.DIAMOND_SWORD;
    }

    @Override
    public int getRequiredLevel(){return 0;}

    @Override
    public String getEnchantmentFriendlyName() {return "Test Enchantment";}

}
