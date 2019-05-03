package ca.spottedleaf.customenchants;

import ca.spottedleaf.customenchants.enchantment.Enchant;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentManager {

    // todo check enchant compatibility with other enchantments

    //Returns all enchantments valid for item
    public List<Enchant> getValidEnchantments(ItemStack item){
        List<Enchant> validEnchants = new ArrayList<>();
        for (Enchant enchant : Enchant.ENCHANTS) {
            if(enchant.isEnchantable(item))
                validEnchants.add(enchant);
        }
        return validEnchants;
    }

    //Returns all enchantments valid for item and given experience level
    public List<Enchant> getValidEnchantments(ItemStack item, int level){
        List<Enchant> validEnchants = new ArrayList<>();
        for (Enchant enchant : Enchant.ENCHANTS) {
            if(enchant.isEnchantable(item) && enchant.getRequiredLevel() < level)
                validEnchants.add(enchant);
        }
        return validEnchants;
    }

}
