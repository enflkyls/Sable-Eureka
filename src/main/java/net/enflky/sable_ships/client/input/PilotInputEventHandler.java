package net.enflky.sable_ships.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.lwjgl.glfw.GLFW;

public final class PilotInputEventHandler {

    @SubscribeEvent
    public void onKey(InputEvent.Key event) {
        if (!ClientPilotSession.active()) {
            return;
        }

        int action = event.getAction();
        int key = event.getKey();
        if (key == HelmKeys.ESCAPE && action == GLFW.GLFW_PRESS) {
            ClientPilotSession.stop();
            return;
        }

        if (action == GLFW.GLFW_PRESS) {
            ClientPilotSession.press(key);
        } else if (action == GLFW.GLFW_RELEASE) {
            ClientPilotSession.release(key);
        }
    }

    @SubscribeEvent
    public void onMovementInput(MovementInputUpdateEvent event) {
        if (!ClientPilotSession.active()) {
            return;
        }

        Input input = event.getInput();
        if (isDown(HelmKeys.FORWARD) || isDown(HelmKeys.BACKWARD)) {
            input.forwardImpulse = 0.0F;
            input.up = false;
            input.down = false;
        }
        if (isDown(HelmKeys.LEFT) || isDown(HelmKeys.RIGHT)) {
            input.leftImpulse = 0.0F;
            input.left = false;
            input.right = false;
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (!ClientPilotSession.active()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.screen != null || !minecraft.player.isAlive()) {
            ClientPilotSession.stop();
            return;
        }

        ClientPilotSession.updateMovementKeys(
                isDown(HelmKeys.FORWARD),
                isDown(HelmKeys.BACKWARD),
                isDown(HelmKeys.LEFT),
                isDown(HelmKeys.RIGHT)
        );
    }

    private static boolean isDown(int keyCode) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getWindow() == null) {
            return false;
        }
        return InputConstants.isKeyDown(minecraft.getWindow().getWindow(), keyCode);
    }
}
