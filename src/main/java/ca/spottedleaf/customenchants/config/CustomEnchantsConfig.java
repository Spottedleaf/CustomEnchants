package ca.spottedleaf.customenchants.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class CustomEnchantsConfig {

    public Set<String> disabledEnchantments;

    public void load(final FileConfiguration config) {
        this.disabledEnchantments = new HashSet<>(config.getStringList("disabled-enchants"));
    }

}
