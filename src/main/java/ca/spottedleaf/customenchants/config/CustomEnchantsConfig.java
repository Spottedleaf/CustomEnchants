package ca.spottedleaf.customenchants.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class CustomEnchantsConfig {

    public List<String> disabledEnchantments;

    public CustomEnchantsConfig() {

    }

    public void load(final FileConfiguration config) {
        this.disabledEnchantments = config.getStringList("disabled-enchants");
    }

}
