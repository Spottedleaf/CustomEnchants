package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.util.ProjectileData;
import ca.spottedleaf.customenchants.util.ProjectileManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ExplosiveProjectileEnchant extends Enchant implements Listener {

    public final ProjectileManager<ProjectileData.ProjectileHolder> projectileManager = new ProjectileManager<ProjectileData.ProjectileHolder>() {
        @Override
        protected void tickShooter(final LivingEntity shooter) {}

        @Override
        protected void tickProjectile(final Projectile projectile, final ProjectileData.ProjectileHolder data) {

        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onProjectileHit(final ProjectileHitEvent event) {
            this.removeProjectile(event.getEntity());
        }
    };

    public ExplosiveProjectileEnchant() {
        super("explosive_projectile", "Explosive");
    }

    @Override
    public void init() {
        this.projectileManager.start();
    }

    @Override
    public void shutdown() {
        this.projectileManager.stop();
    }
}
