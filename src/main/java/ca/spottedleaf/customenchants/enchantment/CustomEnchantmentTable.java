package ca.spottedleaf.customenchants.enchantment;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CustomEnchantmentTable implements Listener {

    private EnchantmentManager enchantmentManager;
    private Plugin plugin;

    public CustomEnchantmentTable(Plugin plugin, EnchantmentManager enchantmentManager) {
        this.enchantmentManager = enchantmentManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockRightClick(PlayerInteractEvent event){
       if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
               event.getHand().equals(EquipmentSlot.HAND) &&
               event.getClickedBlock().getType().equals(Material.IRON_BLOCK) && surroundedByObsidian(event.getClickedBlock())) {
            // ENCHANTMENT TABLE GUI
           showEnchantmentTableGUI(event.getPlayer(), new ItemStack(Material.AIR), 0);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        if(event.getClickedInventory().getHolder() instanceof EnchantmentInventoryHolder &&
            event.getSlot() == 10){
            ItemStack cursor = event.getCursor().clone();
            Bukkit.getScheduler().runTask(plugin ,()->updateEnchantmentGUI(event.getClickedInventory(), cursor, (Player) event.getWhoClicked()));
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

    private void updateEnchantmentGUI(Inventory inventory, ItemStack cursor, Player whoClicked){
        ItemStack itemStack = cursor;
        if(itemStack == null)
            return;
        List<Enchant> enchants = enchantmentManager.getValidEnchantments(itemStack);

        // Clears Enchantment  slots

        int[] enchantmentSlots = {4, 5, 6, 7, 8,
                                  13, 14, 15, 16,
                                  22, 23, 24};

        for (int slot : enchantmentSlots)
            inventory.setItem(slot, new ItemStack(Material.AIR));

        for (int i = 0; i < enchants.size(); i++){
            ItemStack book = new ItemStack(Material.BOOK);
            ItemMeta bookMeta = book.getItemMeta();
            bookMeta.setDisplayName(enchants.get(i).getEnchantmentFriendlyName());
            book.setItemMeta(bookMeta);
            inventory.setItem((4 + Math.floorDiv(i, 5) + Math.floorMod(i, 5)), book);
        }
        whoClicked.updateInventory();
    }

    private void showEnchantmentTableGUI(Player player, ItemStack itemStack, int page){
        for (Enchant enchant: enchantmentManager.getValidEnchantments(itemStack)) {
            player.sendMessage("You can enchant with " + enchant.getEnchantmentFriendlyName());
        }

        Inventory enchantmentInventory = Bukkit.createInventory(new EnchantmentInventoryHolder(), 27);

        int[] obsidianSlots = {0, 1, 2, 9, 11, 18, 19, 20};
        int[] paperSlots = {3, 12, 21};
        int prevDyeSlot = 25;
        int nextDyeSlot = 26;

        //Set obsidian slots
        ItemStack obsidian = new ItemStack(Material.OBSIDIAN);
        for (int slot : obsidianSlots)
            enchantmentInventory.setItem(slot, obsidian);

        //Set Paper slots
        ItemStack paper = new ItemStack(Material.PAPER);
        for (int slot : paperSlots)
            enchantmentInventory.setItem(slot, paper);

        //Set Dye Prev/Next
        //TODO

        //Show enchantments in book form
        List<Enchant> enchants = enchantmentManager.getValidEnchantments(itemStack);
        for (int i = 0; i < enchants.size(); i++){
            //BOOK
            ItemStack book = new ItemStack(Material.BOOK);
            ItemMeta bookMeta = book.getItemMeta();
            bookMeta.setDisplayName(enchants.get(i).getEnchantmentFriendlyName());
            book.setItemMeta(bookMeta);
            enchantmentInventory.setItem((4 + Math.floorDiv(i, 5) + Math.floorMod(i, 5)), book);
        }

        player.openInventory(enchantmentInventory);
    }

}
