package net.enflky.sable_ships.client;

import net.enflky.sable_ships.client.hud.HelmHudPalette;
import net.enflky.sable_ships.client.hud.HelmHudRenderer;
import net.enflky.sable_ships.menu.ShipHelmMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShipHelmScreen extends AbstractContainerScreen<ShipHelmMenu> {

    private static final int GUI_WIDTH = 164;
    private static final int GUI_HEIGHT = 82;

    public ShipHelmScreen(ShipHelmMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = GUI_WIDTH;
        imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - GUI_WIDTH) / 2;
        int y = (height - GUI_HEIGHT) / 2;

        HelmHudRenderer.drawOuterFrame(graphics, x, y, GUI_WIDTH, GUI_HEIGHT);
        graphics.fill(x + 2, y + 18, x + GUI_WIDTH - 2, y + 19, HelmHudPalette.PANEL_MID);

        HelmHudRenderer.drawBeveledPanel(graphics, x + 4, y + 21, (GUI_WIDTH / 2) - 6, 24, HelmHudPalette.PANEL);
        HelmHudRenderer.drawBeveledPanel(graphics, x + GUI_WIDTH / 2 + 2, y + 21, (GUI_WIDTH / 2) - 6, 24, HelmHudPalette.PANEL);
        HelmHudRenderer.drawBeveledPanel(graphics, x + 4, y + 49, GUI_WIDTH - 8, 24, HelmHudPalette.PANEL);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        HelmSnapshot state = ClientHelmState.get();

        graphics.drawCenteredString(font, Component.translatable("menuGroup.sable_ships_lable.main"), imageWidth / 2, 6, 0xFFFFFF);

        graphics.drawString(font, Component.translatable("menuGroup.sable_ships_lable.speed"), 7, 24, HelmHudPalette.LABEL, false);
        graphics.drawString(font, String.format("%.1f m/s", state.speed()), 7, 33, HelmHudPalette.VALUE, false);

        int midX = imageWidth / 2 + 5;
        graphics.drawString(font, Component.translatable("menuGroup.sable_ships_lable.total.mass"), midX, 24, HelmHudPalette.LABEL, false);
        graphics.drawString(font, String.format("%.1f kpg", state.mass()), midX, 33, HelmHudPalette.VALUE, false);

        double yawDegrees = Math.toDegrees(state.yaw());
        if (yawDegrees < 0) {
            yawDegrees += 360.0;
        }

        graphics.drawString(font, Component.translatable("menuGroup.sable_ships_lable.status"), 7, 52, HelmHudPalette.LABEL, false);
        graphics.drawString(font, String.format("%.1f° | Thrust: %.0f", yawDegrees, state.thrustForce()), 7, 61, HelmHudPalette.VALUE, false);
    }
}
