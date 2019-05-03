package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.CustomEnchants;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

public final class EnchantData {

    private static final CustomEnchants PLUGIN = JavaPlugin.getPlugin(CustomEnchants.class);

    public static final int VERSION = 0;
    public static final NamespacedKey VERSION_KEY = new NamespacedKey(PLUGIN, "version");
    public static final NamespacedKey ROOT_KEY = new NamespacedKey(PLUGIN, "enchants");

    public static enum EnchantDataType {
        ACTIVE("active"),
        STORED_ITEM("stored_item");

        public final NamespacedKey key;

        EnchantDataType(final String id) {
            this.key = new NamespacedKey(PLUGIN, id);
        }
    }

    public static PersistentDataContainer getEnchantmentData(final PersistentDataHolder holder) {
        final PersistentDataContainer ret =  holder.getPersistentDataContainer().get(ROOT_KEY, PersistentDataType.TAG_CONTAINER);

        if (ret == null) {
            return null;
        }

        final Integer version = ret.get(VERSION_KEY, PersistentDataType.INTEGER);

        if (version == null || version > VERSION) {
            throw new IllegalStateException("Mismatching versions (we are " + VERSION + ", serialized " + version + ")! Please update CustomEnchants");
        }

        return ret;
    }

    public static PersistentDataContainer getOrCreateEnchantmentData(final PersistentDataHolder holder) {
        PersistentDataContainer ret = EnchantData.getEnchantmentData(holder);
        if (ret != null) {
            return ret;
        }

        ret = holder.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
        ret.set(VERSION_KEY, PersistentDataType.INTEGER, VERSION);

        return ret;
    }

    public static void setEnchantmentData(final PersistentDataHolder holder, final PersistentDataContainer data) {
        holder.getPersistentDataContainer().set(ROOT_KEY, PersistentDataType.TAG_CONTAINER, data);
    }

    public static PersistentDataContainer getEnchantments(final PersistentDataContainer enchantData, final EnchantDataType type) {
        return enchantData.get(type.key, PersistentDataType.TAG_CONTAINER);
    }

    public static void removeEnchantments(final PersistentDataContainer enchantData, final EnchantDataType type) {
        enchantData.remove(type.key);
    }

    public static PersistentDataContainer getEnchantments(final PersistentDataHolder holder, final EnchantDataType type) {
        final PersistentDataContainer enchantData = EnchantData.getEnchantmentData(holder);

        if (enchantData == null) {
            return null;
        }

        return EnchantData.getEnchantments(enchantData, type);
    }

    public static PersistentDataContainer getOrCreateEnchantments(final PersistentDataContainer enchantData, final EnchantDataType type) {
        final PersistentDataContainer ret = EnchantData.getEnchantments(enchantData, type);
        return ret != null ? ret : enchantData.getAdapterContext().newPersistentDataContainer();
    }

    public static void setEnchantments(final PersistentDataContainer enchantData, final PersistentDataContainer enchantments, final EnchantDataType type) {
        enchantData.set(type.key, PersistentDataType.TAG_CONTAINER, enchantments);
    }

    public static void forEachEnchantment(final PersistentDataHolder holder, final EnchantDataType type,
                                          final BiConsumer<Enchant, PersistentDataContainer> consumer) {
        final PersistentDataContainer enchantments = EnchantData.getEnchantments(holder, type);

        if (enchantments == null) {
            return;
        }

        EnchantData.forEachEnchantment(enchantments, consumer);
    }

    public static void forEachEnchantment(final PersistentDataContainer enchantments, final BiConsumer<Enchant, PersistentDataContainer> consumer) {
        for (final Enchant enchant : Enchant.ENCHANTS) {
            final PersistentDataContainer enchantSerialized = enchantments.get(enchant.key, PersistentDataType.TAG_CONTAINER);
            if (enchantSerialized == null) {
                continue;
            }
            consumer.accept(enchant, enchantSerialized);
        }
    }

    private EnchantData() {
        throw new RuntimeException();
    }
}
