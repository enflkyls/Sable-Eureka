package net.enflky.sable_ships.client.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Reusable beveled panel, key grid, and compass drawing shared by overlay and screen UIs.
 */
public final class HelmHudRenderer {

    private HelmHudRenderer() {}

    public static void drawBeveledPanel(GuiGraphics graphics, int x, int y, int width, int height, int fillColor) {
        graphics.fill(x, y, x + width, y + height, fillColor);
        graphics.fill(x, y, x + width, y + 1, HelmHudPalette.PANEL_DARK);
        graphics.fill(x, y, x + 1, y + height, HelmHudPalette.PANEL_DARK);
        graphics.fill(x + width - 1, y, x + width, y + height, HelmHudPalette.PANEL_MID);
        graphics.fill(x, y + height - 1, x + width, y + height, HelmHudPalette.PANEL_MID);
    }

    public static void drawOuterFrame(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, HelmHudPalette.BG);
        graphics.fill(x, y, x + width, y + 2, HelmHudPalette.BORDER_LIGHT);
        graphics.fill(x, y, x + 2, y + height, HelmHudPalette.BORDER_LIGHT);
        graphics.fill(x + width - 2, y, x + width, y + height, HelmHudPalette.BORDER_DARK);
        graphics.fill(x, y + height - 2, x + width, y + height, HelmHudPalette.BORDER_DARK);
    }

    public static void drawKey(GuiGraphics graphics, Font font, int x, int y, int width, int height,
                               String label, boolean pressed) {
        graphics.fill(x, y, x + width, y + height, pressed ? HelmHudPalette.KEY_ON_BG : HelmHudPalette.KEY_OFF_BG);
        int border = pressed ? HelmHudPalette.KEY_ON : HelmHudPalette.KEY_BORDER;
        graphics.fill(x, y, x + width, y + 1, border);
        graphics.fill(x, y + height - 1, x + width, y + height, border);
        graphics.fill(x, y, x + 1, y + height, border);
        graphics.fill(x + width - 1, y, x + width, y + height, border);
        int textX = x + (width - font.width(label)) / 2;
        int textY = y + (height - font.lineHeight) / 2;
        graphics.drawString(font, label, textX, textY, pressed ? HelmHudPalette.KEY_ON : HelmHudPalette.HINT, false);
    }

    public static void drawWasdGrid(GuiGraphics graphics, Font font, int x, int y,
                                    boolean forward, boolean backward, boolean left, boolean right) {
        int keyWidth = 13;
        int keyHeight = 11;
        int gap = 2;
        drawKey(graphics, font, x + keyWidth + gap, y, keyWidth, keyHeight, "W", forward);
        drawKey(graphics, font, x, y + keyHeight + gap, keyWidth, keyHeight, "A", left);
        drawKey(graphics, font, x + keyWidth + gap, y + keyHeight + gap, keyWidth, keyHeight, "S", backward);
        drawKey(graphics, font, x + (keyWidth + gap) * 2, y + keyHeight + gap, keyWidth, keyHeight, "D", right);
    }

    /**
     * Simple 2D compass; yaw=0 points south (+Z) on the ship.
     */
    public static void drawCompass(GuiGraphics graphics, Font font, int centerX, int centerY,
                                   int radius, double yaw) {
        graphics.fill(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 0x99050510);

        for (int i = 0; i < 32; i++) {
            double angle = 2 * Math.PI * i / 32.0;
            int px = centerX + (int) (radius * Math.cos(angle));
            int py = centerY + (int) (radius * Math.sin(angle));
            graphics.fill(px, py, px + 1, py + 1, 0xFF2A6080);
        }

        int labelRadius = radius - 5;
        drawCompassLabel(graphics, font, centerX, centerY, labelRadius, -Math.PI / 2, "N", 0xFFFF4444);
        drawCompassLabel(graphics, font, centerX, centerY, labelRadius, Math.PI / 2, "S", 0xFF8B8B8B);
        drawCompassLabel(graphics, font, centerX, centerY, labelRadius, 0, "E", 0xFF8B8B8B);
        drawCompassLabel(graphics, font, centerX, centerY, labelRadius, Math.PI, "W", 0xFF8B8B8B);

        double arrow = yaw + Math.PI / 2.0;
        int arrowRadius = radius - 7;
        int arrowX = centerX + (int) (arrowRadius * Math.cos(arrow));
        int arrowY = centerY + (int) (arrowRadius * Math.sin(arrow));
        drawLine(graphics, centerX, centerY, arrowX, arrowY, 0xFF88CCFF);
        graphics.fill(arrowX - 1, arrowY - 1, arrowX + 2, arrowY + 2, 0xFF88CCFF);
    }

    private static void drawCompassLabel(GuiGraphics graphics, Font font, int centerX, int centerY,
                                         int radius, double angle, String label, int color) {
        int labelX = centerX + (int) (radius * Math.cos(angle)) - font.width(label) / 2;
        int labelY = centerY + (int) (radius * Math.sin(angle)) - font.lineHeight / 2;
        graphics.drawString(font, label, labelX, labelY, color, false);
    }

    private static void drawLine(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            graphics.fill(x0, y0, x0 + 2, y0 + 2, color);
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
}
