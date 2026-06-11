package net.enflky.sable_ships.client.input;

import net.enflky.sable_ships.client.HelmSnapshot;
import net.minecraft.core.BlockPos;

public final class ClientPilotSession {

    private static PilotInputController controller;
    private static BlockPos helmPos;

    private ClientPilotSession() {}

    public static boolean active() {
        return controller != null;
    }

    public static void syncFromServer(HelmSnapshot snapshot) {
        if (!snapshot.active()) {
            stop();
            return;
        }

        if (controller == null || !snapshot.helmPos().equals(helmPos)) {
            helmPos = snapshot.helmPos();
            controller = new PilotInputController(helmPos);
        }
    }

    public static void press(int keyCode) {
        if (controller != null) {
            controller.handleKeyPress(keyCode);
        }
    }

    public static void release(int keyCode) {
        if (controller != null) {
            controller.handleKeyRelease(keyCode);
        }
    }

    public static void updateMovementKeys(boolean forward, boolean backward, boolean left, boolean right) {
        if (controller == null) {
            return;
        }
        setKey(HelmKeys.FORWARD, forward);
        setKey(HelmKeys.BACKWARD, backward);
        setKey(HelmKeys.LEFT, left);
        setKey(HelmKeys.RIGHT, right);
    }

    public static void stop() {
        if (controller == null) {
            return;
        }
        controller.clearAndRelease();
        controller = null;
        helmPos = null;
    }

    private static void setKey(int keyCode, boolean pressed) {
        if (pressed) {
            controller.handleKeyPress(keyCode);
        } else {
            controller.handleKeyRelease(keyCode);
        }
    }
}
