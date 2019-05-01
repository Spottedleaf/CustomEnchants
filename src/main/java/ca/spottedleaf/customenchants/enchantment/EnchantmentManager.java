package ca.spottedleaf.customenchants.enchantment;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantmentManager {
    
    private ArrayList<Enchant> enchantments;
    
    public EnchantmentManager(ArrayList<Enchant> enchantments){
        this.enchantments = enchantments;
    }

    //Returns all enchantments valid for item
    public ArrayList<Enchant> getValidEnchantments(ItemStack item){
        ArrayList<Enchant> validEnchants = new ArrayList<Enchant>();
        for (Enchant enchant: enchantments) {
            if(enchant.isEnchantable(item))
                validEnchants.add(enchant);
        }
        return validEnchants;
    }

    //Returns all enchantments valid for item and given experience level
    public ArrayList<Enchant> getValidEnchantments(ItemStack item, int level){
        ArrayList<Enchant> validEnchants = new ArrayList<Enchant>();
        for (Enchant enchant: enchantments) {
            if(enchant.isEnchantable(item) && enchant.getRequiredLevel() < level)
                validEnchants.add(enchant);
        }
        return validEnchants;
    }

}
