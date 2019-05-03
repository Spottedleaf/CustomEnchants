package ca.spottedleaf.customenchants.listener;

import ca.spottedleaf.customenchants.CustomEnchants;
import ca.spottedleaf.customenchants.enchantment.Enchant;
import ca.spottedleaf.customenchants.tooltype.StandardToolTypes;
import ca.spottedleaf.customenchants.util.EnchantData;
import ca.spottedleaf.customenchants.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public final class EnchantmentListener implements Listener {

    private static final CustomEnchants PLUGIN = JavaPlugin.getPlugin(CustomEnchants.class);

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArrowPickup(final PlayerPickupArrowEvent event) {
        final Item item = event.getItem();
        final AbstractArrow arrow = event.getArrow();

        final PersistentDataContainer enchantData = EnchantData.getEnchantmentData(arrow);

        if (enchantData == null) {
            return;
        }

        final PersistentDataContainer storedEnchantments = EnchantData.getEnchantments(enchantData, EnchantData.EnchantDataType.STORED_ITEM);

        if (storedEnchantments == null) {
            return;
        }

        final ItemStack arrowItem = item.getItemStack();

        if (Util.isEmpty(arrowItem)) {
            return;
        }

        final ItemMeta arrowMeta = arrowItem.getItemMeta();

        final PersistentDataContainer arrowEnchantData = EnchantData.getOrCreateEnchantmentData(arrowMeta);

        EnchantData.setEnchantments(arrowEnchantData, storedEnchantments, EnchantData.EnchantDataType.ACTIVE);
        EnchantData.setEnchantmentData(arrowMeta, arrowEnchantData);

        EnchantData.forEachEnchantment(storedEnchantments, (final Enchant enchant, final PersistentDataContainer serialized) -> {
            enchant.onItemEnchant(arrowMeta, serialized);
        });

        arrowItem.setItemMeta(arrowMeta);
        item.setItemStack(arrowItem);
    }

    private final Long2ObjectOpenHashMap<ItemStack> dispenserItems = new Long2ObjectOpenHashMap<>();

    {
        // debug
        Bukkit.getScheduler().runTaskTimer(PLUGIN, () -> {
            if (!EnchantmentListener.this.dispenserItems.isEmpty()) {
                PLUGIN.getLogger().severe(EnchantmentListener.this.dispenserItems.toString());
            }
        }, 0L, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDispense(final BlockDispenseEvent event) {
        if (event instanceof BlockDispenseArmorEvent) {
            return;
        }

        final ItemStack item = event.getItem();

        if (!StandardToolTypes.PROJECTILES.matches(item)) {
            return;
        }

        final Block block = event.getBlock();

        final long key = Util.getBlockKey(block.getX(), block.getY(), block.getZ());

        if (this.dispenserItems.putIfAbsent(key, item.clone()) != null) {
            // debug
            throw new IllegalStateException("dispenser: " + block + ", last item: " + this.dispenserItems.get(key) + ", curr item: " + item);
        }

        // projectile launch event is called after this
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();

        final ProjectileSource source = projectile.getShooter();
        if (!(source instanceof BlockProjectileSource)) {
            return;
        }

        final Block block = ((BlockProjectileSource)source).getBlock();

        final long key = Util.getBlockKey(block.getX(), block.getY(), block.getZ());

        final ItemStack item = this.dispenserItems.remove(key);

        if (item == null) {
            return;
        }

        final PersistentDataContainer activeEnchantments = EnchantData.getEnchantments(item.getItemMeta(), EnchantData.EnchantDataType.ACTIVE);
        if (activeEnchantments == null) {
            return;
        }

        final PersistentDataContainer projectileEnchantData = EnchantData.getOrCreateEnchantmentData(projectile);

        EnchantData.setEnchantments(projectileEnchantData, activeEnchantments, EnchantData.EnchantDataType.ACTIVE);
        EnchantData.setEnchantments(projectileEnchantData, activeEnchantments, EnchantData.EnchantDataType.STORED_ITEM);

        EnchantData.setEnchantmentData(projectile, projectileEnchantData);

        EnchantData.forEachEnchantment(projectileEnchantData, (final Enchant enchant, final PersistentDataContainer serialized) -> {
            enchant.onProjectileEnchant(projectile, serialized);
        });

    }

}
