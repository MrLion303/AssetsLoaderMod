package mr.lion303.assetsloadermod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 58;
    private static final int PANEL_MARGIN = 8;
    private static final int TEXT_COLOR = 0xFF202020;
    private static final int PANEL_COLOR = 0x99FFFFFF;
    private static final int BAR_BACKGROUND = 0x55202020;
    private static final int BAR_FILL = 0xFF202020;

    @Shadow @Final
    private ReloadInstance reload;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void assetsLoaderMod$render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();

        // Mantener la pantalla original al iniciar el juego y al cargar un mundo.
        if (minecraft.level == null) {
            return;
        }

        ci.cancel();

        int progress = Math.max(0, Math.min(Math.round(this.reload.getActualProgress() * 100.0F), 100));
        int x = PANEL_MARGIN;
        int y = graphics.guiHeight() - PANEL_HEIGHT - PANEL_MARGIN;

        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, PANEL_COLOR);
        graphics.drawString(minecraft.font, Component.literal("Cargando Assets"), x + 8, y + 7, TEXT_COLOR, false);

        int loaded = progress >= 100 ? 1 : 0;
        graphics.drawString(minecraft.font, Component.literal(loaded + "/1 Assets cargados"), x + 8, y + 21, TEXT_COLOR, false);

        int barX = x + 8;
        int barY = y + 38;
        int barWidth = PANEL_WIDTH - 16;
        int barHeight = 8;

        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, BAR_BACKGROUND);
        int filledWidth = Math.round(barWidth * (progress / 100.0F));
        if (filledWidth > 0) {
            graphics.fill(barX, barY, barX + filledWidth, barY + barHeight, BAR_FILL);
        }
    }
}
