package ca.spottedleaf.customenchants.util;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public final class ControlledProjectilesManager implements Runnable, Listener {

    private final Map<UUID, List<EnchantedProjectile>> PLAYER_PROJECTILES = new HashMap<>();
    private final Map<UUID, EnchantedProjectile> ENCHANTED_PROJECTILES = new HashMap<>();

    public void addProjectile(final LivingEntity shooter, final Projectile projectile, final double speed, final double maxRange) {
        final EnchantedProjectile enchantedProjectile = new EnchantedProjectile(projectile, speed, maxRange);
        if (ENCHANTED_PROJECTILES.putIfAbsent(projectile.getUniqueId(), enchantedProjectile) != null) {
            throw new IllegalStateException("Projectile " + projectile + " is already enchanted!");
        }
        PLAYER_PROJECTILES.computeIfAbsent(shooter.getUniqueId(), (final UUID key) -> {
            return new ArrayList<>();
        }).add(enchantedProjectile);
    }

    /**
     * @return {@code true} if the projectile was tracked, {@code false otherwise}
     */
    public boolean removeProjectile(final Projectile projectile) {
        final ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof LivingEntity)) {
            return false;
        }

        final UUID shooterUniqueId = ((LivingEntity)shooter).getUniqueId();

        final List<EnchantedProjectile> projectiles = PLAYER_PROJECTILES.get(shooterUniqueId);

        if (projectiles == null) {
            return false;
        }

        for (int i = 0, len = projectiles.size(); i < len; ++i) {
            if (projectiles.get(i).projectile.equals(projectile)) {
                // found our projectile
                projectiles.remove(i);
                if (len == 1) {
                    PLAYER_PROJECTILES.remove(shooterUniqueId);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void run() {
        // This is ran once every 2 ticks
        final Iterator<Map.Entry<UUID, List<EnchantedProjectile>>> entries = PLAYER_PROJECTILES.entrySet().iterator();

        while (entries.hasNext()) {
            final Map.Entry<UUID, List<EnchantedProjectile>> entry = entries.next();

            final UUID shooterUniqueId = entry.getKey();
            final List<EnchantedProjectile> projectiles = entry.getValue();

            final LivingEntity shooter = (LivingEntity) Bukkit.getEntity(shooterUniqueId);
            if (shooter == null) {
                entries.remove();
                continue;
            }

            final World world = shooter.getWorld();

            final Location start = shooter.getEyeLocation();
            final Vector direction = start.getDirection();


            final RayTraceResult traceResult = world.rayTrace(start, direction, Util.getMaxDistance(shooter),
                    FluidCollisionMode.NEVER, true, 0.0, (final Entity other) -> {
                if (other.getUniqueId().equals(shooterUniqueId)) {
                    return false;
                }

                for (final EnchantedProjectile enchantedProjectile : projectiles) {
                    if (enchantedProjectile.projectile.equals(other)) {
                        return false;
                    }
                }
                return true;
            });

            if (traceResult == null) {
                for (final EnchantedProjectile enchantedProjectile : projectiles) {
                    enchantedProjectile.projectile.setGravity(true);
                }
                continue;
            }

            final Vector hitLocation = traceResult.getHitPosition();

            final Iterator<EnchantedProjectile> projectilesIterator = entry.getValue().iterator();

            while (projectilesIterator.hasNext()) {
                final EnchantedProjectile value = projectilesIterator.next();

                final Projectile projectile = value.projectile;

                final Location projectileLocation = projectile.getLocation();

                if (!projectile.isValid() || !world.equals(projectileLocation.getWorld())) {
                    projectilesIterator.remove(); // we've moved worlds, invalidate projectile
                    if (projectiles.isEmpty()) {
                        entries.remove(); // out of projectiles for this shooter
                        break;
                    }
                }

                if (Util.distanceSquared(hitLocation, projectileLocation) > value.maxRangeSquared) {
                    projectile.setGravity(true);
                    continue; // out of range
                }
                projectile.setGravity(false);

                final Vector angle = new Vector(
                        hitLocation.getX() - projectileLocation.getX(),
                        hitLocation.getY() - projectileLocation.getY(),
                        hitLocation.getZ() - projectileLocation.getZ()
                ).normalize();

                final Vector currentAngle = projectile.getVelocity().normalize();

                final Vector newAngle = currentAngle.add(angle.add(angle.multiply(0.28)));

                projectile.setVelocity(newAngle.multiply(value.speed));
            }
        }
    }

    static final class EnchantedProjectile {

        public final Projectile projectile;
        public final double speed;
        public final double maxRangeSquared;

        public EnchantedProjectile(final Projectile projectile, final double speed, final double maxRange) {
            this.projectile = projectile;
            this.speed = speed;
            this.maxRangeSquared = maxRange * maxRange;
        }
    }
}