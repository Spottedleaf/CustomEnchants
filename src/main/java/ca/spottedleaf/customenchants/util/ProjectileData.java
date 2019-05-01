package ca.spottedleaf.customenchants.util;

import org.bukkit.entity.Projectile;

public interface ProjectileData {

    Projectile getProjectile();

    public static final class ProjectileHolder implements ProjectileData {

        public final Projectile projectile;

        public ProjectileHolder(final Projectile projectile) {
            this.projectile = projectile;
        }

        @Override
        public final Projectile getProjectile() {
            return this.projectile;
        }
    }

}
