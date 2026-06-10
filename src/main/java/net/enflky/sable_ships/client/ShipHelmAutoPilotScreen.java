package net.enflky.sable_ships.client;

import dev.ryanhcode.sable.mixinhelpers.camera.new_camera_types.SableCameraTypes;
import net.enflky.sable_ships.client.input.HelmKeys;
import net.enflky.sable_ships.client.input.PilotInputController;
import net.enflky.sable_ships.content.HelmMenuCooldown;
import net.enflky.sable_ships.menu.ShipHelmMenu;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class ShipHelmAutoPilotScreen extends AbstractContainerScreen<ShipHelmMenu> {

    private final PilotInputController inputController;
    private boolean released;

    public ShipHelmAutoPilotScreen(ShipHelmMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 0;
        imageHeight = 0;
        inputController = new PilotInputController(menu.blockPos);
    }

    @Override
    protected void init() {
        super.init();
        released = false;
        if (minecraft != null) {
            long window = minecraft.getWindow().getWindow();
            minecraft.options.setCameraType(SableCameraTypes.SUB_LEVEL_VIEW);
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
        inputController.sendPilotingStart();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            onClose();
            return true;
        }
        return false;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft != null && minecraft.player != null) {
            long window = minecraft.getWindow().getWindow();
            double[] cursorX = new double[1];
            double[] cursorY = new double[1];
            GLFW.glfwGetCursorPos(window, cursorX, cursorY);

            double deltaX = cursorX[0];
            double deltaY = cursorY[0];
            if (deltaX != 0 || deltaY != 0) {
                double sensitivity = minecraft.options.sensitivity().get() * 0.6 + 0.2;
                double scaledSensitivity = sensitivity * sensitivity * sensitivity * 8.0;
                minecraft.player.turn(deltaX * scaledSensitivity, deltaY * scaledSensitivity);
                GLFW.glfwSetCursorPos(window, 0, 0);
            }
        }

        int textY = height - 60;
        graphics.drawCenteredString(font, Component.translatable("menuGroup.sable_ships_lable.pilotcontrol1"), width / 2, textY, 0x55FF55);
        graphics.drawCenteredString(font, Component.translatable("menuGroup.sable_ships_lable.pilotcontrol2"), width / 2, textY + 12, 0xAAAAAA);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null
                && keyCode == minecraft.options.keyTogglePerspective.getKey().getValue()) {
            minecraft.options.setCameraType(minecraft.options.getCameraType().cycle());
            return true;
        }

        if (keyCode == HelmKeys.ESCAPE) {
            onClose();
            return true;
        }

        inputController.handleKeyPress(keyCode);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        inputController.handleKeyRelease(keyCode);
        return true;
    }

    @Override
    public void onClose() {
        if (!released) {
            released = true;
            inputController.clearAndRelease();
            if (minecraft != null) {
                long window = minecraft.getWindow().getWindow();
                minecraft.options.setCameraType(CameraType.FIRST_PERSON);
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                GLFW.glfwSetCursorPos(window,
                        minecraft.getWindow().getScreenWidth() / 2.0,
                        minecraft.getWindow().getScreenHeight() / 2.0);
                if (minecraft.player != null && minecraft.level != null) {
                    HelmMenuCooldown.markClosed(minecraft.player.getUUID(), minecraft.level.getGameTime());
                }
            }
        }
        super.onClose();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    }
}
