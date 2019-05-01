package ca.spottedleaf.customenchants.enchantment;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomEnchantmentTable implements Listener {

    @EventHandler
    public void onBlockRightClick(PlayerInteractEvent event){
       if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                event.getClickedBlock().getType().equals(Material.IRON_BLOCK) && surroundedByObsidian(event.getClickedBlock())) {
           event.getPlayer().sendMessage("You clicked a table");
            // ENCHANTMENT TABLE GUI
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


}
