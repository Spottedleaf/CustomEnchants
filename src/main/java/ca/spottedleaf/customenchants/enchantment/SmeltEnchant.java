package ca.spottedleaf.customenchants.enchantment;

import net.minecraft.server.v1_14_R1.CraftingManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SmeltEnchant extends Enchant implements Listener {

    private NamespacedKey SMELT_ORE_KEY;
    private Plugin plugin;
    private List<CookingRecipe>  cookingRecipes;

    public SmeltEnchant(Plugin plugin) {
        super("smelt_ore");
        SMELT_ORE_KEY = super.getKey();
        this.plugin = plugin;
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        cookingRecipes = new ArrayList<>();
        while (recipeIterator.hasNext()){
            Recipe r = recipeIterator.next();
            if (r instanceof CookingRecipe)
                cookingRecipes.add((CookingRecipe) r);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack playerItem = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = playerItem.getItemMeta();
        //int data = itemMeta.getPersistentDataContainer().get(SMELT_ORE_KEY, PersistentDataType.INTEGER);
        //TODO check persistent data for enchantment
        //if(data == 1){
            event.setDropItems(false);
            List<CookingRecipe> recipes = new ArrayList<>();
            List<ItemStack> drops = (List<ItemStack>) block.getDrops();
            for (ItemStack drop : drops) {
                boolean dropFlag = true;
                for (CookingRecipe r : cookingRecipes) {
                    List<Material> materialChoices = ((RecipeChoice.MaterialChoice) r.getInputChoice()).getChoices();
                    boolean recipeFlag = false;
                    for (Material material : materialChoices){
                        if(drop.getType() == material) {
                            recipeFlag = true;
                            break;
                        }
                    }
                    if(recipeFlag){
                        recipes.add(r);
                        break;
                    }
                }
            }
            List<ItemStack> dropsFiltered = drops;
            for (CookingRecipe r : recipes) {
                dropsFiltered.add(r.getResult());
                for (ItemStack drop : drops){
                    if(r.getInputChoice().test(drop)){
                        dropsFiltered.remove(drop);
                    }
                }
            }


            Location blockLocation = block.getLocation();
            World blockWorld = block.getWorld();
            for (ItemStack item : dropsFiltered)
                blockWorld.dropItem(blockLocation, item);

            player.sendMessage("Dropped " + dropsFiltered.size() + " items");
        //}

    }
}
