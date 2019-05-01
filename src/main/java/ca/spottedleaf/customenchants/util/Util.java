package ca.spottedleaf.customenchants.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class Util {

    public static boolean isEmpty(final ItemStack item) {
        if (item == null) {
            return true;
        }
        final Material type = item.getType();

        return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR;
    }

    private static double square(final double x) {
        return x * x;
    }

    public static double distanceSquared(final Vector v1, final Location l1) {
        return square(v1.getX() - l1.getX()) + square(v1.getY() - l1.getY()) + square(v1.getZ() - l1.getZ());
    }

    public static double getMaxDistance(final LivingEntity player) {
        return Util.getMaxDistance(player, Bukkit.getViewDistance());
    }

    public static double getMaxDistance(final LivingEntity player, final int viewDistance) {
        /* the chunks around a player is square, and viewdistance is simply the number of chunks from one of the edges (directly along the x-axis or y-axis to the middle) */
        /* this excludes the center chunk */
        /* This means the width of the view distance square is = (viewdistance * 2 + 1) in chunks */

        /* Lines commented out are debug */
        /* They instead return a Vector, which when added to the player's eye location, */
        /* is the location just at the edge of the player's viewdistance or at the top or bottom of the world. */
        /* This vector is also a similar triangle compared to a player's looking direction, thus the magnitude */
        /* represents the maximum distance before going out of 'bounds' */

        final Location eyeLoc = player.getEyeLocation();

        final double locX = eyeLoc.getX();
        final double locY = eyeLoc.getY(); /* eye location */
        final double locZ = eyeLoc.getZ();

        final double pitch = player.getLocation().getPitch();
        double yaw = (player.getLocation().getYaw() + (90 + 360)) % 360.0;
        /* pitch > 0.0 -> looking down */
        /* pitch < 0.0 -> looking up */
        /* yaw is offset by -90.0 in minecraft for some reason, undo that... */

        /* These values signify the maximum distance on the x or z axis until out of bounds is reached */
        /* It depends on where the player is looking, see the code below */
        /* It is later used to clamp the looking vector  */
        final double maxXDistance;
        final double maxZDistance;

        final int chunkX = eyeLoc.getBlockX() >> 4;
        final int chunkZ = eyeLoc.getBlockZ() >> 4;

        /* If yaw in (0, 180) then the player is looking in a positive z direction */
        /* If yaw in (180, 360) then the player is looking in a negative z direction */

        if (yaw < 180.0) {
            final int topmostChunkZ = viewDistance + chunkZ;
            maxZDistance = (topmostChunkZ * 16) - locZ;
        } else {
            final int bottommostChunkZ = chunkZ - viewDistance;
            maxZDistance = locZ - (bottommostChunkZ * 16 + 16);
        }

        /* If yaw in (0, 90) or (270, 360) then the player is looking in a positive x direction */
        /* If yaw in (90, 270), then the player is looking in a negative x direction */
        if (yaw > 270 || yaw < 90) {
            final int leftmostChunkX = viewDistance + chunkX;
            maxXDistance = (leftmostChunkX * 16) - locX;
        } else {
            final int rightmostChunkX = chunkX - viewDistance;
            maxXDistance = locX - (rightmostChunkX * 16 + 16);
        }

        final Vector lookingDirection = eyeLoc.getDirection();

        final double triangHeight = pitch >= 0.0 ? locY : player.getWorld().getMaxHeight() - locY;
        if (triangHeight <= 0.0) {
            /* pitch >= 0 (looking down or straight ahead) && yloc <= 0 */
            /* or pitch <= 0 (looking up or straight ahead) && yloc >= max world height */
            return -1; /* There are no blocks in the void! */
            //return null;
        }

        /* Scale our vector such that its y is at either the top or bottom of the world */
        final double real = triangHeight / Math.abs(lookingDirection.getY());
        if (Double.isInfinite(real)) {
            /* looking straight ahead */
            /* Ignore pitch at this point it's useless */

            final double dx = maxXDistance / Math.abs(lookingDirection.getX());
            final double dz = maxZDistance / Math.abs(lookingDirection.getZ());

            /* If the component x or z is 0, then we don't want to use the max distance from that direction */
            /* However since we go for the smallest dx or dz value, div by zero sorts itself out */

            final double scale = dx > dz ? dz : dx;

            return scale;
            //return lookingDirection.multiply(scale);
        }

        /* However we need to clamp to max view distance */

        lookingDirection.multiply(real);

        /* y is clamped already */

        final double dx = Math.abs(lookingDirection.getX()) / maxXDistance;
        final double dz = Math.abs(lookingDirection.getZ()) / maxZDistance;

        if (dx <= 1.0 && dz <= 1.0) {
            /* This case occurs if the player is not looking into other chunks, that is, the player is looking at the sky. */
            /* With the sky being the area above the loaded chunks */
            /* This also happens on the opposite direction, that is, the ground. */
            return real;
            //return lookingDirection;
        }

        /* We want to scale the vector such that one component is just in bounds of the loaded chunks */
        /* So we use the largest of the two divisors, which should land one of the components at its corresponding max range */
        /* And given we're still a similar triangle after the scaling, we've found our max block given the looking vector. */
        final double scale = Math.max(dx, dz);
        //lookingDirection.multiply(1.0 / scale); /* no divide function */

        /* It's possible we're still in the void. */
        //if ((lookingDirection.getY() + locY) < 0.0) {
        if ((lookingDirection.getY() / scale + locY) < 0.0) {
            return -1;
            //return null;
        }

        return real / scale;
        //return lookingDirection;
    }

    private Util() {
        throw new RuntimeException();
    }
}