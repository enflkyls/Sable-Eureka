package net.enflky.sable_ships.content;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.SableShipsBlockEntityTypes;
import net.enflky.sable_ships.config.SableShipsConfig;
import net.enflky.sable_ships.content.engine.ShipEngineBlockEntity;
import net.enflky.sable_ships.helm.GyroscopeController;
import net.enflky.sable_ships.helm.HelmInputState;
import net.enflky.sable_ships.helm.HelmPhysicsSettings;
import net.enflky.sable_ships.helm.PilotStateManager;
import net.enflky.sable_ships.helm.PropulsionController;
import net.enflky.sable_ships.helm.ShipOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

public class ShipHelmBlockEntity extends BlockEntity implements BlockEntitySubLevelActor {

    private final HelmInputState input = new HelmInputState();
    private final GyroscopeController gyroscope = new GyroscopeController();
    private final PropulsionController propulsion = new PropulsionController();
    private final PilotStateManager pilotStateManager = new PilotStateManager();
    private final Vector3d velocityScratch = new Vector3d();
    private final Vector3d yawScratch = new Vector3d();

    private double lastMass;
    private int cachedActiveEngineCount;
    private int engineScanCooldown;

    public ShipHelmBlockEntity(BlockPos pos, BlockState state) {
        super(SableShipsBlockEntityTypes.SHIP_HELM.get(), pos, state);
    }

    public HelmInputState input() {
        return input;
    }

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        lastMass = subLevel.getMassTracker().getMass();
        HelmPhysicsSettings settings = prepareSettings(subLevel);

        gyroscope.tick(subLevel, handle, settings, timeStep, lastMass);
        if (input.piloting) {
            propulsion.tick(subLevel, handle, getBlockState().getValue(ShipHelmBlock.FACING),
                    settings, input, timeStep, lastMass);
        }

        if (level instanceof ServerLevel serverLevel) {
            handle.getLinearVelocity(velocityScratch);
            double yaw = ShipOrientation.computeYaw(subLevel.logicalPose().orientation(), yawScratch);
            pilotStateManager.tick(serverLevel, getBlockPos(), input, velocityScratch, yaw, lastMass,
                    settings);
        }

        if (SableShipsConfig.DEBUG.get()) {
            SableShips.LOGGER.debug("[ShipHelm] mass={} piloting={} fwd={} bwd={} L={} R={}",
                    lastMass, input.piloting, input.forward, input.backward, input.left, input.right);
        }
    }

    private HelmPhysicsSettings prepareSettings(ServerSubLevel subLevel) {
        if (engineScanCooldown-- <= 0) {
            cachedActiveEngineCount = countPoweredEngines(subLevel);
            engineScanCooldown = SableShipsConfig.SHIP_ENGINE_SCAN_INTERVAL_TICKS.get();
        }

        return HelmPhysicsSettings.fromConfig(cachedActiveEngineCount);
    }

    private int countPoweredEngines(ServerSubLevel subLevel) {
        int maxEngines = SableShipsConfig.SHIP_ENGINE_MAX_STACKING_ENGINES.get();
        if (maxEngines <= 0 || level == null) {
            return 0;
        }

        int count = 0;
        BoundingBox3ic bounds = subLevel.getPlot().getBoundingBox();
        for (int x = bounds.minX(); x <= bounds.maxX() && count < maxEngines; x++) {
            for (int y = bounds.minY(); y <= bounds.maxY() && count < maxEngines; y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ() && count < maxEngines; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.getBlockEntity(pos) instanceof ShipEngineBlockEntity engine && engine.isBurning()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
