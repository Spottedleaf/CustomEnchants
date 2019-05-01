package ca.spottedleaf.customenchants.enchantment;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantmentTable implements Listener {

    private EnchantmentManager enchantmentManager;

    public CustomEnchantmentTable(EnchantmentManager enchantmentManager) {
        this.enchantmentManager = enchantmentManager;
    }

    @EventHandler
    public void onBlockRightClick(PlayerInteractEvent event){
       if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
               event.getHand().equals(EquipmentSlot.HAND) &&
               event.getClickedBlock().getType().equals(Material.IRON_BLOCK) && surroundedByObsidian(event.getClickedBlock())) {
           event.getPlayer().sendMessage("You clicked a table");
            // ENCHANTMENT TABLE GUI
           showEnchantmentTableGUI(event.getPlayer());
        }
    }

    private boolean surroundedByObsidian(Block clickedBlock) {
        Location location = clickedBlock.getLocation();
        int locx = location.getBlockX();
        int locy = location.getBlockY();
        int locz = location.getBlockZ();
        if(clickedBlock.getWorld().getBlockAt((locx + 1), locy, locz).getType().equals(Material.OBSIDIAN) &&
                clickedBlock.getWorld().getBlockAt((locx - 1), locy, locz).getType().equals(Material.OBSIDIAN) &&
                clickedBlock.getWorld().getBlockAt(locx, locy, (locz + 1)).getType().equals(Material.OBSIDIAN) &&
                clickedBlock.getWorld().getBlockAt(locx, locy, (locz - 1)).getType().equals(Material.OBSIDIAN)){
            return true;
        }
        return false;
    }

    private void showEnchantmentTableGUI(Player player){
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        for (Enchant enchant: enchantmentManager.getValidEnchantments(itemStack)) {
            player.sendMessage("You can enchant with " + enchant.getEnchantmentName());
        }
    }

}
