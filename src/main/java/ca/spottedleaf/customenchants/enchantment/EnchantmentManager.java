package ca.spottedleaf.customenchants.enchantment;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentManager {
    
    private List<Enchant> enchantments;
    
    public EnchantmentManager(List<Enchant> enchantments){
        this.enchantments = enchantments;
    }

    // todo check enchant compatibility with other enchantments

    //Returns all enchantments valid for item
    public List<Enchant> getValidEnchantments(ItemStack item){
        List<Enchant> validEnchants = new ArrayList<Enchant>();
        for (Enchant enchant: enchantments) {
            if(enchant.isEnchantable(item))
                validEnchants.add(enchant);
        }
        return validEnchants;
    }

    //Returns all enchantments valid for item and given experience level
    public List<Enchant> getValidEnchantments(ItemStack item, int level){
        List<Enchant> validEnchants = new ArrayList<Enchant>();
        for (Enchant enchant: enchantments) {
            if(enchant.isEnchantable(item) && enchant.getRequiredLevel() < level)
                validEnchants.add(enchant);
        }
        return validEnchants;
    }

}
