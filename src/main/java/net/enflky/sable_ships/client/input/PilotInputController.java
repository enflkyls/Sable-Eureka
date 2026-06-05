package net.enflky.sable_ships.client.input;

import net.enflky.sable_ships.network.HelmInputPacket;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;


 //Tracks WASD pilot input and sends some shit consolidated packets to the server.
 //Seperated from screen beacuse there is some rendering shit this is easier.

public final class PilotInputController {

    private final BlockPos helmPos;
    private boolean forward;
    private boolean backward;
    private boolean left;
    private boolean right;

    public PilotInputController(BlockPos helmPos) {
        this.helmPos = helmPos;
    }

    public boolean handleKeyPress(int keyCode) {
        return applyKeyState(keyCode, true);
    }

    public boolean handleKeyRelease(int keyCode) {
        return applyKeyState(keyCode, false);
    }

    public void clearAndRelease() {
        forward = backward = left = right = false;
        send(false);
    }

    public void sendPilotingStart() {
        send(true);
    }

    private boolean applyKeyState(int keyCode, boolean pressed) {
        boolean changed = switch (keyCode) {
            case HelmKeys.FORWARD -> { boolean prev = forward; forward = pressed; yield prev != forward; }
            case HelmKeys.BACKWARD -> { boolean prev = backward; backward = pressed; yield prev != backward; }
            case HelmKeys.LEFT -> { boolean prev = left; left = pressed; yield prev != left; }
            case HelmKeys.RIGHT -> { boolean prev = right; right = pressed; yield prev != right; }
            default -> false;
        };
        if (changed) {
            send(true);
        }
        return changed;
    }

    private void send(boolean piloting) {
        PacketDistributor.sendToServer(new HelmInputPacket(
                helmPos, forward, backward, left, right, piloting
        ));
    }
}
