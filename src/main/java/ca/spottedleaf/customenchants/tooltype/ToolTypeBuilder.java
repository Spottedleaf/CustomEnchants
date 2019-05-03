package ca.spottedleaf.customenchants.tooltype;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.EnumSet;

public class ToolTypeBuilder {

    private final EnumSet<Material> materials = EnumSet.noneOf(Material.class);

    public ToolTypeBuilder() {}

    public ToolTypeBuilder add(final Material material) {
        materials.add(material);
        return this;
    }

    public ToolTypeBuilder add(final Material... materials) {
        this.materials.addAll(Arrays.asList(materials));
        return this;
    }

    public ToolTypeBuilder add(final StandardToolTypes type) {
        this.materials.addAll(type.matchTo);
        return this;
    }

    public ToolTypeBuilder add(final StandardToolTypes... types) {
        for (final StandardToolTypes type : types) {
            this.materials.addAll(type.matchTo);
        }
        return this;
    }

    public ToolType create() {
        return new ToolType() {

            final EnumSet<Material> MATCH_TO = ToolTypeBuilder.this.materials.clone();

            @Override
            public boolean matches(final ItemStack item) {
                return item != null && MATCH_TO.contains(item.getType());
            }
        };
    }

}