package net.enflky.sable_ships.helm;

import net.minecraft.core.Direction;
import org.joml.Quaterniondc;
import org.joml.Quaterniond;
import org.joml.Vector3d;

/**
 * Converts between block facing, ship body orientation, and world-space movement axes. I done it beacuse it works this wayyy
 * This is for more performance to not initiliaze thingys (i hate sable api)
 */
public final class ShipOrientation {

    private static final Vector3d[] FACING_VECTORS = new Vector3d[6];

    static { //random values worked yay
        FACING_VECTORS[Direction.NORTH.ordinal()] = new Vector3d(0, 0, -1);
        FACING_VECTORS[Direction.SOUTH.ordinal()] = new Vector3d(0, 0, +1);
        FACING_VECTORS[Direction.EAST.ordinal()] = new Vector3d(+1, 0, 0);
        FACING_VECTORS[Direction.WEST.ordinal()] = new Vector3d(-1, 0, 0);
    }

    private ShipOrientation() {}

    /**
     * Calculates horizontal aka (Y) ship yaw from body orientation (0 = south/+Z).
     */
    public static double computeYaw(Quaterniondc orientation, Vector3d scratchForward) {
        orientation.transform(new Vector3d(0, 0, -1), scratchForward);
        return Math.atan2(scratchForward.x(), scratchForward.z());
    }

    /**
     * Resolves the helm blocks facing into a world-space forward vector for propulsion, For to make it
     * accounting for ship roll/pitch via projection onto the horizontal plane. Some sable shit going here
     */
    public static void computeWorldForward(
            Quaterniondc orientation,
            Direction blockFacing,
            Vector3d scratchUp,
            Vector3d scratchDefaultForward,
            Vector3d scratchFacing,
            Vector3d scratchProjFacing,
            Vector3d scratchProjDefault,
            Vector3d outWorldForward
    ) {
        orientation.transform(new Vector3d(0, 1, 0), scratchUp);
        orientation.transform(new Vector3d(0, 0, -1), scratchDefaultForward);

        Vector3d facingWorld = facingVector(blockFacing);
        scratchFacing.set(facingWorld);

        projectOntoPlane(scratchFacing, scratchUp, scratchProjFacing).normalize();
        projectOntoPlane(scratchDefaultForward, scratchUp, scratchProjDefault).normalize();

        Quaterniond yawOffset = new Quaterniond().rotationTo(scratchProjDefault, scratchProjFacing);
        yawOffset.transform(scratchDefaultForward, outWorldForward).normalize().negate();
    }

    public static Vector3d facingVector(Direction facing) {
        Vector3d vector = FACING_VECTORS[facing.ordinal()];
        return vector != null ? vector : FACING_VECTORS[Direction.NORTH.ordinal()];
    }

    /**
     * Projects vec to the plane that is normal is  planeNormal writing into out. What a mean what ever nobody reads this
     */
    public static Vector3d projectOntoPlane(Vector3d vec, Vector3d planeNormal, Vector3d out) {
        double dot = vec.dot(planeNormal);
        return out.set(vec).fma(-dot, planeNormal);
    }
}
