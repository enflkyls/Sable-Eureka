package net.enflky.sable_ships.content;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.enflky.sable_ships.SableShips;
import net.enflky.sable_ships.SableShipsBlocks;
import net.enflky.sable_ships.helm.GyroscopeController;
import net.enflky.sable_ships.helm.HelmInputState;
import net.enflky.sable_ships.helm.HelmTuning;
import net.enflky.sable_ships.helm.PilotStateManager;
import net.enflky.sable_ships.helm.PropulsionController;
import net.enflky.sable_ships.helm.ShipOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

//BIGGEST SHIT CODE it was hard to do this
public class ShipHelmBlockEntity extends BlockEntity implements BlockEntitySubLevelActor {

    private final HelmTuning tuning = new HelmTuning();
    private final HelmInputState input = new HelmInputState();
    private final GyroscopeController gyroscope = new GyroscopeController();
    private final PropulsionController propulsion = new PropulsionController();
    private final PilotStateManager pilotStateManager = new PilotStateManager();
    private final Vector3d velocityScratch = new Vector3d();
    private final Vector3d yawScratch = new Vector3d();

    private double lastMass;

    public ShipHelmBlockEntity(BlockPos pos, BlockState state) {
        super(SableShipsBlocks.SHIP_HELM_ENTITY.get(), pos, state);
    }

    public HelmTuning tuning() {
        return tuning;
    }

    public HelmInputState input() {
        return input;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tuning.save(tag); //i need to delete compound and make it public not compound driven
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tuning.load(tag);
    }

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        lastMass = subLevel.getMassTracker().getMass();

        gyroscope.tick(subLevel, handle, tuning, timeStep, lastMass);
        if (input.piloting) {
            propulsion.tick(subLevel, handle, getBlockState().getValue(ShipHelmBlock.FACING),
                    tuning, input, timeStep, lastMass);
        }

        if (level instanceof ServerLevel serverLevel) {
            handle.getLinearVelocity(velocityScratch);
            double yaw = ShipOrientation.computeYaw(subLevel.logicalPose().orientation(), yawScratch);
            pilotStateManager.tick(serverLevel, getBlockPos(), input, velocityScratch, yaw, lastMass, tuning);
        }

        if (tuning.debug) {
            SableShips.LOGGER.info("[ShipHelm] mass={} piloting={} fwd={} bwd={} L={} R={}",
                    lastMass, input.piloting, input.forward, input.backward, input.left, input.right);
        }
    }
}
