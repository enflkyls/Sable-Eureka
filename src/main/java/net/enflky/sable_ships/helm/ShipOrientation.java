package net.enflky.sable_ships.helm;

import net.minecraft.core.Direction;
import org.joml.Quaterniondc;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public final class ShipOrientation {

    private static final Vector3d[] FACING_VECTORS = new Vector3d[6];

    static {
        FACING_VECTORS[Direction.NORTH.ordinal()] = new Vector3d(0, 0, -1);
        FACING_VECTORS[Direction.SOUTH.ordinal()] = new Vector3d(0, 0, +1);
        FACING_VECTORS[Direction.EAST.ordinal()] = new Vector3d(+1, 0, 0);
        FACING_VECTORS[Direction.WEST.ordinal()] = new Vector3d(-1, 0, 0);
    }

    private ShipOrientation() {}

    public static double computeYaw(Quaterniondc orientation, Vector3d scratchForward) {
        orientation.transform(new Vector3d(0, 0, -1), scratchForward);
        return Math.atan2(scratchForward.x(), scratchForward.z());
    }

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

    public static Vector3d projectOntoPlane(Vector3d vec, Vector3d planeNormal, Vector3d out) {
        double dot = vec.dot(planeNormal);
        return out.set(vec).fma(-dot, planeNormal);
    }
}
