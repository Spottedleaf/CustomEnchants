package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.util.ProjectileData;
import ca.spottedleaf.customenchants.util.ProjectileManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class ExplosiveProjectileEnchant extends Enchant {

    public final ProjectileManager<ProjectileData.ProjectileHolder> projectileManager = new ProjectileManager<ProjectileData.ProjectileHolder>() {
        @Override
        protected void tickShooter(final LivingEntity shooter) {}

        @Override
        protected void tickProjectile(final Projectile projectile, final ProjectileData.ProjectileHolder data) {

        }
    };

    public ExplosiveProjectileEnchant() {
        super("explosive");
    }

    public void init() {
        this.projectileManager.start();
    }
}
