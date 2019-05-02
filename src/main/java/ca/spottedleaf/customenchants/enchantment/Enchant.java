package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.CustomEnchants;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Enchant implements Keyed {

    public final CustomEnchants plugin = JavaPlugin.getPlugin(CustomEnchants.class);
    public final String id;
    public final NamespacedKey key;

    protected Enchant(final String id) {
        this.id = id;
        this.key = new NamespacedKey(this.plugin, this.id);
    }

    @Override
    public final NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public final int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        return obj == this;
    }

    public void init() {}

    public final boolean containsEnchantment(final PersistentDataHolder data) {
        return data.getPersistentDataContainer().has(this.key, PersistentDataType.TAG_CONTAINER);
    }

    public final PersistentDataContainer getData(final PersistentDataHolder dataHolder){
        return dataHolder.getPersistentDataContainer();
    }

    /**
     * Called after this projectile has been enchanted
     * @param shooter May be a LivingEntity or Block
     */
    public void onProjectileEnchant(final Projectile projectile, final Object shooter) {

    }

    public void onProjectileDisenchant(final Projectile projectile) {}

    public void enchant(ItemStack item){}

    public boolean isEnchantable(ItemStack item){
        return false;
    }

    public boolean isCompatible(final Enchant other) {
        return true;
    }

    public int getRequiredLevel() {return 0;}

    public String getEnchantmentFriendlyName() {return "";}

}