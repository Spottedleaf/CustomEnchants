package ca.spottedleaf.customenchants.enchantment;

import org.bukkit.inventory.ItemStack;

public class Enchant {

    public void init() {}

    public void enchant(ItemStack item){}

    public boolean isEnchantable(ItemStack item){return false;}

    public int getRequiredLevel(){return 0;}

    public String getEnchantmentName() {return "";}
}