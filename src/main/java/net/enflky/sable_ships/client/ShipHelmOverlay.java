package net.enflky.sable_ships.client;

import net.enflky.sable_ships.client.hud.HelmHudPalette;
import net.enflky.sable_ships.client.hud.HelmHudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;


//HUD shown while piloting for the 1 iqs
//
public class ShipHelmOverlay {

    private static final int PAD = 10;
    private static final int HUD_WIDTH = 170;
    private static final int HUD_HEIGHT = 120;
    private static final int COMPASS_RADIUS = 22;

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Pre event) {
        if (!ClientHelmState.shouldRenderHud()) {
            return;
        }
        HelmSnapshot state = ClientHelmState.get();

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null && !(minecraft.screen instanceof ShipHelmAutoPilotScreen)) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        var font = minecraft.font;
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int hudX = PAD;
        int hudY = screenHeight - HUD_HEIGHT - PAD;

        HelmHudRenderer.drawOuterFrame(graphics, hudX, hudY, HUD_WIDTH, HUD_HEIGHT);
        graphics.drawCenteredString(font, "Ship Helm", hudX + HUD_WIDTH / 2, hudY + 5, HelmHudPalette.TITLE);
        graphics.fill(hudX + 2, hudY + 15, hudX + HUD_WIDTH - 2, hudY + 16, HelmHudPalette.PANEL_MID);

        int blockY = hudY + 18;
        int blockHeight = 22;
        int leftBlockWidth = (HUD_WIDTH / 2) - 6;

        HelmHudRenderer.drawBeveledPanel(graphics, hudX + 4, blockY, leftBlockWidth, blockHeight, HelmHudPalette.PANEL);
        graphics.drawString(font, "SPEED", hudX + 7, blockY + 3, HelmHudPalette.LABEL, false);
        graphics.drawString(font, String.format("%.1f m/s", state.speed()), hudX + 7, blockY + 12, HelmHudPalette.VALUE, false);

        int rightBlockX = hudX + HUD_WIDTH / 2 + 2;
        HelmHudRenderer.drawBeveledPanel(graphics, rightBlockX, blockY, leftBlockWidth, blockHeight, HelmHudPalette.PANEL);
        graphics.drawString(font, "TOTAL MASS", rightBlockX + 3, blockY + 3, HelmHudPalette.LABEL, false);
        graphics.drawString(font, String.format("%.1f kpg", state.mass()), rightBlockX + 3, blockY + 12, HelmHudPalette.VALUE, false);

        int headingY = blockY + blockHeight + 2;
        HelmHudRenderer.drawBeveledPanel(graphics, hudX + 4, headingY, HUD_WIDTH - 8, blockHeight, HelmHudPalette.PANEL);
        double yawDegrees = Math.toDegrees(state.yaw());
        if (yawDegrees < 0) {
            yawDegrees += 360.0;
        }
        graphics.drawString(font, "HEADING", hudX + 7, headingY + 3, HelmHudPalette.LABEL, false);
        graphics.drawString(font, String.format("%.1f° Thrust:%.0f TurnForce:%.0f",
                        yawDegrees, state.thrustForce(), state.turnForce()),
                hudX + 7, headingY + 12, HelmHudPalette.VALUE, false);

        int separatorY = headingY + blockHeight + 4;
        graphics.fill(hudX + 2, separatorY, hudX + HUD_WIDTH - 2, separatorY + 1, HelmHudPalette.PANEL_MID);

        int keyGridY = separatorY + 4;
        HelmHudRenderer.drawWasdGrid(graphics, font, hudX + 4, keyGridY,
                state.forward(), state.backward(), state.left(), state.right());

        int compassX = hudX + HUD_WIDTH - COMPASS_RADIUS - 10;
        int compassY = keyGridY + COMPASS_RADIUS + 2;
        HelmHudRenderer.drawCompass(graphics, font, compassX, compassY, COMPASS_RADIUS, state.yaw());

        graphics.drawString(font, "[ESC] Release", hudX + 5, hudY + HUD_HEIGHT - 11, HelmHudPalette.HINT, false);
    }
}
