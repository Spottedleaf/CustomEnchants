package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.tooltype.StandardToolTypes;
import ca.spottedleaf.customenchants.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public class SmeltEnchant extends Enchant implements Listener {

    private final EnumMap<Material, Material> transformMap = new EnumMap<>(Material.class);

    public SmeltEnchant() {
        super("smelt_ore", "Smelt Ore");
    }

    @Override
    public void init() {
        final Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();

        while (recipeIterator.hasNext()) {
            final Recipe recipe = recipeIterator.next();
            if (recipe instanceof CookingRecipe) {
                final CookingRecipe cookingRecipe = (CookingRecipe)recipe;
                final List<Material> choices = ((RecipeChoice.MaterialChoice) (cookingRecipe.getInputChoice())).getChoices();
                final Material output = cookingRecipe.getResult().getType();

                for (final Material choice : choices) {
                    this.transformMap.put(choice, output);
                }
            }
        }
        Bukkit.getPluginManager().registerEvents(this, PLUGIN);
    }

    @Override
    public boolean isEnchantable(final ItemStack item) {
        return StandardToolTypes.GENERIC_TOOL.matches(item);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(final BlockDropItemEvent event) {
        final Player player = event.getPlayer();
        final ItemStack mainHand = player.getInventory().getItemInMainHand();

        if (false && !Util.isEmpty(mainHand) && this.getEnchantment(mainHand.getItemMeta(), EnchantData.EnchantDataType.ACTIVE) != null) { // TODO add in this check later
            return;
        }

        for (final Item droppedEntity : event.getItems()) {
            final ItemStack droppedItem = droppedEntity.getItemStack();
            final Material toMap = this.transformMap.get(droppedItem.getType());

            if (toMap == null) {
                continue;
            }

            droppedItem.setType(toMap);
            droppedEntity.setItemStack(droppedItem);
        }
    }
}
