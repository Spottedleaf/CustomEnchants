package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.CustomEnchants;
import ca.spottedleaf.customenchants.util.EnchantData;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

public abstract class Enchant implements Keyed {

    protected static final CustomEnchants PLUGIN = JavaPlugin.getPlugin(CustomEnchants.class);

    private static final Map<String, Enchant> ENCHANTMENTS_BY_ID = new HashMap<>();
    private static final List<Enchant> ENCHANTMENT_LIST = new ArrayList<>();

    public static final Map<String, Enchant> ENCHANTS_BY_ID = Collections.unmodifiableMap(ENCHANTMENTS_BY_ID);
    public static final List<Enchant> ENCHANTS = Collections.unmodifiableList(ENCHANTMENT_LIST);

    public final String id;
    public final String friendlyName;
    public final NamespacedKey key;

    protected Enchant(final String id, final String friendlyName) {
        if (ENCHANTMENTS_BY_ID.putIfAbsent(id, this) != null) {
            throw new IllegalStateException("Enchantment '" + id + "' is already registered!");
        }
        ENCHANTMENT_LIST.add(this);

        this.id = id;
        this.friendlyName = friendlyName;
        this.key = new NamespacedKey(PLUGIN, this.id);
    }

    public static Enchant getEnchantmentById(final String id) {
        return ENCHANTMENTS_BY_ID.get(id);
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
    public void shutdown() {}

    /**
     * Called after the projectile has been enchanted
     */
    public void onProjectileEnchant(final Projectile projectile, final PersistentDataContainer enchant) {}

    /**
     * Called after the projectile is disenchanted
     */
    public void onProjectileDisenchant(final Projectile projectile, final PersistentDataContainer enchant) {}

    public void onItemEnchant(final ItemMeta meta, final PersistentDataContainer enchant) {}

    public void onItemDisenchant(final ItemMeta meta, final PersistentDataContainer enchant) {}

    public final void enchant(final PersistentDataHolder holder, final EnchantData.EnchantDataType type, final Consumer<PersistentDataContainer> consumer) {
        final PersistentDataContainer enchantData = EnchantData.getOrCreateEnchantmentData(holder);
        final PersistentDataContainer enchantments = EnchantData.getOrCreateEnchantments(enchantData, type);

        if (type == EnchantData.EnchantDataType.ACTIVE) {
            final PersistentDataContainer previousSerialized = enchantments.get(this.key, PersistentDataType.TAG_CONTAINER);

            if (previousSerialized != null) {
                if (holder instanceof Projectile) {
                    this.onProjectileDisenchant((Projectile)holder, previousSerialized);
                } else if (holder instanceof ItemMeta) {
                    this.onItemDisenchant((ItemMeta)holder, previousSerialized);
                }
            }
        }

        final PersistentDataContainer newSerialized = enchantments.getAdapterContext().newPersistentDataContainer();

        consumer.accept(newSerialized);

        enchantments.set(this.key, PersistentDataType.TAG_CONTAINER, newSerialized);
        EnchantData.setEnchantments(enchantData, enchantments, type);
        EnchantData.setEnchantmentData(holder, enchantData);

        if (type == EnchantData.EnchantDataType.ACTIVE) {
            if (holder instanceof Projectile) {
                this.onProjectileEnchant((Projectile)holder, newSerialized);
            } else if (holder instanceof ItemMeta) {
                this.onItemEnchant((ItemMeta)holder, newSerialized);
            }
        }
    }

    /**
     * @return {@code true} if the holder was previously enchanted
     */
    public final boolean removeEnchantment(final PersistentDataHolder holder, final EnchantData.EnchantDataType type) {
        final PersistentDataContainer enchantData = EnchantData.getEnchantmentData(holder);
        if (enchantData == null) {
            return false;
        }

        PersistentDataContainer enchantments = EnchantData.getEnchantments(enchantData, type);
        if (enchantments == null) {
            return false;
        }

        PersistentDataContainer enchant = enchantments.get(this.key, PersistentDataType.TAG_CONTAINER);

        if (enchant == null) {
            return false;
        }

        enchantments.remove(this.key);
        EnchantData.setEnchantments(enchantData, enchantments, type);
        EnchantData.setEnchantmentData(holder, enchantData);

        if (type == EnchantData.EnchantDataType.ACTIVE) {
            if (holder instanceof Projectile) {
                this.onProjectileDisenchant((Projectile)holder, enchant);
            } else if (holder instanceof ItemMeta) {
                this.onItemDisenchant((ItemMeta)holder, enchant);
            }
        }

        return true;
    }

    public boolean isEnchantable(final ItemStack item) {
        return false;
    }

    public boolean isCompatible(final Enchant other) {
        return true;
    }

    public int getRequiredLevel() {
        return 0;
    }
}