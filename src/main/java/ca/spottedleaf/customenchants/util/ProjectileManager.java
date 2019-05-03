package ca.spottedleaf.customenchants.util;

import ca.spottedleaf.customenchants.CustomEnchants;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class ProjectileManager<T extends ProjectileData> implements Runnable, Listener {

    private final Map<UUID, List<T>> PLAYER_PROJECTILES = new HashMap<>();
    private final Map<UUID, T> ENCHANTED_PROJECTILES = new HashMap<>();
    private final CustomEnchants plugin = CustomEnchants.getPlugin(CustomEnchants.class);

    private BukkitTask repeatTask;

    public void start() {
        this.repeatTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this, 1L, 1L);
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (this.repeatTask != null) {
            this.repeatTask.cancel();
        }

        for (final T projectile : ENCHANTED_PROJECTILES.values()) {
            this.onProjectileRemoval(projectile.getProjectile(), projectile);
        }

        PLAYER_PROJECTILES.clear();
        ENCHANTED_PROJECTILES.clear();
    }

    public void addProjectile(final Projectile projectile, final LivingEntity shooter, final T data) {
        if (ENCHANTED_PROJECTILES.putIfAbsent(projectile.getUniqueId(), data) != null) {
            throw new IllegalStateException("Projectile " + projectile + " is already enchanted!");
        }
        PLAYER_PROJECTILES.computeIfAbsent(shooter.getUniqueId(), (final UUID key) -> {
            return new ArrayList<>();
        }).add(data);
        this.onProjectileAddition(projectile, data);
    }

    public boolean removeProjectile(final Projectile projectile) {
        final ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof LivingEntity)) {
            return false;
        }

        final UUID shooterUniqueId = ((LivingEntity)shooter).getUniqueId();

        final T data = ENCHANTED_PROJECTILES.get(projectile.getUniqueId());

        final List<T> projectileData = PLAYER_PROJECTILES.get(shooterUniqueId);

        if (projectileData == null || data == null) {
            return false;
        }

        for (int i = 0, len = projectileData.size(); i < len; ++i) {
            if (projectileData.get(i) == data) {
                // found our projectile

                this.onProjectileRemoval(projectile, data);

                if (len == 1) {
                    PLAYER_PROJECTILES.remove(shooterUniqueId);
                    return true;
                }

                projectileData.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public final void run() {
        final Iterator<Map.Entry<UUID, List<T>>> entries = PLAYER_PROJECTILES.entrySet().iterator();

        while (entries.hasNext()) {
            final Map.Entry<UUID, List<T>> entry = entries.next();

            final UUID shooterUniqueId = entry.getKey();
            final List<T> projectiles = entry.getValue();

            final LivingEntity shooter = (LivingEntity)Bukkit.getEntity(shooterUniqueId);
            if (shooter == null) {
                for (final T data : entry.getValue()) {
                    this.onProjectileRemoval(data.getProjectile(), data);
                }
                entries.remove();
                continue;
            }

            this.tickShooter(shooter);

            final World world = shooter.getWorld();

            final Iterator<T> projectilesIterator = entry.getValue().iterator();

            while (projectilesIterator.hasNext()) {
                final T value = projectilesIterator.next();

                final Projectile projectile = value.getProjectile();

                if (!projectile.isValid() || !world.equals(projectile.getWorld())) {
                    this.onProjectileRemoval(projectile, value);
                    projectilesIterator.remove(); // we've moved worlds, invalidate projectile
                    if (projectiles.isEmpty()) {
                        entries.remove(); // out of projectiles for this shooter
                        break;
                    }
                }

                this.tickProjectile(projectile, value);
            }
        }
    }

    /**
     * Called after addition
     */
    protected void onProjectileAddition(final Projectile projectile, final T data) {

    }

    /**
     * Called before removal
     */
    protected void onProjectileRemoval(final Projectile projectile, final T data) {

    }

    /**
     * Called before ticking a shooter's projectiles
     */
    protected abstract void tickShooter(final LivingEntity shooter);

    protected abstract void tickProjectile(final Projectile projectile, final T data);

}
