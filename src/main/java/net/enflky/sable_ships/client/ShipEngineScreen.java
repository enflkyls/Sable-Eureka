package net.enflky.sable_ships.client;

import net.enflky.sable_ships.client.hud.HelmHudPalette;
import net.enflky.sable_ships.client.hud.HelmHudRenderer;
import net.enflky.sable_ships.config.SableShipsConfig;
import net.enflky.sable_ships.menu.ShipEngineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShipEngineScreen extends AbstractContainerScreen<ShipEngineMenu> {

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    public ShipEngineScreen(ShipEngineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = GUI_WIDTH;
        imageHeight = GUI_HEIGHT;
        inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {

        HelmHudRenderer.drawOuterFrame(graphics, leftPos, topPos, GUI_WIDTH, GUI_HEIGHT);
        HelmHudRenderer.drawBeveledPanel(graphics, leftPos + 68, topPos + 23, 40, 42, HelmHudPalette.PANEL);

        graphics.fill(leftPos + 79, topPos + 34 , leftPos + 99, topPos + 54, 0xFF0D1117);
        graphics.renderOutline(leftPos + 79, topPos + 34 , 18, 18, 0xFF6B7280);

        int flameHeight = menu.getBurnProgressPixels();
        if (flameHeight > 0) {
            int flameBottom = topPos + 59 ;
            int flameTop = flameBottom - flameHeight;
            graphics.fill(leftPos + 82, flameTop, leftPos + 94, flameBottom, 0xFFFF9F1C);
            graphics.fill(leftPos + 85, flameTop + 3, leftPos + 91, flameBottom, 0xFFFFD166);
        }

        graphics.fill(leftPos + 7, topPos + 74 , leftPos + GUI_WIDTH - 7, topPos + 75, HelmHudPalette.PANEL_MID);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (SableShipsConfig.DEBUG.get()){
            graphics.drawCenteredString(font, title, imageWidth / 2, 7, 0xFFFFFF);

            String status = menu.isBurning() ? "ACTIVE" : "INACTIVE";
            int statusColor = menu.isBurning() ? 0x55FF88 : HelmHudPalette.LABEL;
            graphics.drawString(font, Component.literal(status), 10, 24, statusColor, false);

            int seconds = menu.getBurnTime() / 20;
            graphics.drawString(font, Component.literal("Fuel: " + seconds + "s"), 10, 36, HelmHudPalette.VALUE, false);
            graphics.drawString(font, Component.literal(String.format("Boost: +%.1f thrust",
                    SableShipsConfig.SHIP_ENGINE_THRUST_BONUS.get())), 10, 50, HelmHudPalette.VALUE, false);
            graphics.drawString(font, Component.literal(String.format("+%.1f water cap",
                    SableShipsConfig.SHIP_ENGINE_WATER_SPEED_CAP_BONUS.get())), 10, 61, HelmHudPalette.VALUE, false);

            graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, HelmHudPalette.LABEL, false);
        }
    }
}
