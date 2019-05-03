package ca.spottedleaf.customenchants.enchantment;

import ca.spottedleaf.customenchants.tooltype.StandardToolTypes;
import ca.spottedleaf.customenchants.tooltype.ToolType;
import ca.spottedleaf.customenchants.tooltype.ToolTypeBuilder;
import ca.spottedleaf.customenchants.util.ProjectileData;
import ca.spottedleaf.customenchants.util.ProjectileManager;
import ca.spottedleaf.customenchants.util.Util;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class ControlledProjectileEnchant extends Enchant implements Listener {

    public final ControlledProjectilesManager controlledProjectiles;

    public ControlledProjectileEnchant() {
        super("controlled_projectile", "Controlled Projectile");
        this.controlledProjectiles = new ControlledProjectilesManager();
    }

    @Override
    public void onProjectileEnchant(final Projectile projectile, final PersistentDataContainer enchant) {
        final ProjectileSource source = projectile.getShooter();
        if (source instanceof LivingEntity) {
            this.controlledProjectiles.addProjectile(projectile, (LivingEntity)source,
                    new ControlledProjectilesManager.EnchantedProjectile(projectile, Math.min(projectile.getVelocity().length(), 0.9)));
        }
    }

    @Override
    public void onProjectileDisenchant(final Projectile projectile, final PersistentDataContainer enchant) {
        this.controlledProjectiles.removeProjectile(projectile);
    }

    @Override
    public void init() {
        this.controlledProjectiles.start();
        Bukkit.getPluginManager().registerEvents(this, PLUGIN);
    }

    @Override
    public void shutdown() {
        this.controlledProjectiles.stop();
        HandlerList.unregisterAll(this);
    }

    private static final ToolType TOOLS = new ToolTypeBuilder().add(StandardToolTypes.BOW, StandardToolTypes.ARROW).create();

    @Override
    public boolean isEnchantable(final ItemStack item) {
        return TOOLS.matches(item);
    }

    public static final class ControlledProjectilesManager extends ProjectileManager<ControlledProjectilesManager.EnchantedProjectile> implements Listener {

        private static void spawnParticlesForTarget(final World world, final Vector location, final BlockFace face, final int particles) {
            final BlockData particle = Material.REDSTONE_WIRE.createBlockData();
            ((RedstoneWire)particle).setPower(((RedstoneWire)particle).getMaximumPower());

            final double maxOffset = 0.1;
            if (face == null) {
                world.spawnParticle(Particle.FALLING_DUST, location.getX(), location.getY(), location.getZ(), particles, maxOffset, maxOffset, maxOffset,1.0,
                        particle, true);
                return;
            }

            // This is to prevent the particles from clipping into the block
            final Vector direction = face.getDirection();
            final double offsetx = maxOffset * direction.getX();
            final double offsety = maxOffset * direction.getY();
            final double offsetz = maxOffset * direction.getZ();

            world.spawnParticle(Particle.FALLING_DUST, location.getX() + 0.1 * offsetx, location.getY() + 0.1 * offsety, location.getZ() + 0.1 * offsetz,
                    particles, offsetx, offsety, offsetz, 1.0, particle, true);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onProjectileHit(final ProjectileHitEvent event) {
            this.removeProjectile(event.getEntity());
        }

        @Override
        protected void onProjectileRemoval(final Projectile projectile, final EnchantedProjectile data) {
            projectile.setGravity(true);
        }

        private RayTraceResult currentRaycast;
        private Vector currentRaycastLocation;
        private boolean spawnedTargetParticles;

        @Override
        protected void tickShooter(final LivingEntity shooter) {
            this.spawnedTargetParticles = false;
            final World world = shooter.getWorld();

            final Location start = shooter.getEyeLocation();
            final Vector direction = start.getDirection();

            final RayTraceResult traceResult = world.rayTrace(start, direction, Util.getMaxDistance(shooter), FluidCollisionMode.NEVER, true, 0.0,
                    (final Entity other) -> {
                        if (other.getUniqueId().equals(shooter.getUniqueId())) {
                            return false;
                        }

                        if (other instanceof Projectile && shooter.equals(((Projectile)other).getShooter())) {
                            return false;
                        }
                        return true;
                    });

            this.currentRaycast = traceResult;
            this.currentRaycastLocation = traceResult == null ? null : traceResult.getHitPosition();
        }

        @Override
        protected void tickProjectile(final Projectile projectile, final EnchantedProjectile data) {
            final World world = projectile.getWorld();
            final Location projectileLocation = projectile.getLocation();

            if (this.currentRaycastLocation == null) {
                projectile.setGravity(true);
                return; // out of range
            }

            projectile.setGravity(false);

            if (!this.spawnedTargetParticles) {
                this.spawnedTargetParticles = true;
                spawnParticlesForTarget(world, this.currentRaycastLocation, this.currentRaycast.getHitBlockFace(), 2);
            }

            final Vector optimalDirection = new Vector(
                    this.currentRaycastLocation.getX() - projectileLocation.getX(),
                    this.currentRaycastLocation.getY() - projectileLocation.getY(),
                    this.currentRaycastLocation.getZ() - projectileLocation.getZ()
            ).normalize();

            final Vector currentDirection = projectile.getVelocity().normalize();

            final Vector newAngle = currentDirection.add(optimalDirection.add(optimalDirection.multiply(0.19)));

            projectile.setVelocity(newAngle.multiply(data.speed));
        }

        static final class EnchantedProjectile implements ProjectileData {

            public final Projectile projectile;
            public final double speed;

            public EnchantedProjectile(final Projectile projectile, final double speed) {
                this.projectile = projectile;
                this.speed = speed;
            }

            @Override
            public Projectile getProjectile() {
                return this.projectile;
            }
        }
    }
}