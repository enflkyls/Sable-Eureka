package net.enflky.sable_ships.helm;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;


 //Thread-safe pilot shitty input flags written by network handlers and read by the physics tick.
public final class HelmInputState {

    public volatile boolean forward;
    public volatile boolean backward;
    public volatile boolean left;
    public volatile boolean right;
    public volatile boolean piloting;
    public volatile UUID pilotId;

    public void set(boolean forward, boolean backward, boolean left, boolean right, boolean piloting) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.piloting = piloting;
    }

    public boolean setFromPlayer(ServerPlayer player, boolean forward, boolean backward, boolean left, boolean right, boolean piloting) {
        UUID playerId = player.getUUID();
        UUID currentPilot = pilotId;
        if (piloting && currentPilot != null && !currentPilot.equals(playerId)) {
            return false;
        }

        set(forward, backward, left, right, piloting);
        pilotId = piloting ? playerId : null;
        return true;
    }

    public void clear() {
        set(false, false, false, false, false);
        pilotId = null;
    }

    public boolean anyMovementKey() {  //maybe future shitty thing
        return forward || backward || left || right;
    }
}
