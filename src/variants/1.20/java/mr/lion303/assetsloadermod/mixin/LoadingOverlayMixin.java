package mr.lion303.assetsloadermod.mixin;

import mr.lion303.assetsloadermod.ResourceReloadState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Shadow @Final private ReloadInstance reload;
    @Unique private int assetsLoaderMod$totalAssets = -1;

    @Inject(method = "render", at = @At("TAIL"))
    private void assetsLoaderMod$render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!ResourceReloadState.isGameReady()) {
            return;
        }

        float reloadProgress = Math.max(0.0F, Math.min(this.reload.getActualProgress(), 1.0F));
        if (this.assetsLoaderMod$totalAssets < 0) {
            try {
                this.assetsLoaderMod$totalAssets = minecraft.getResourceManager()
                        .listResources("", location -> true).size();
            } catch (RuntimeException ignored) {
                this.assetsLoaderMod$totalAssets = 0;
            }
        }

        int total = Math.max(0, this.assetsLoaderMod$totalAssets);
        int loaded = this.reload.isDone() ? total : Math.min(total, Math.round(total * reloadProgress));
        int progress = Math.round(reloadProgress * 100.0F);
        int x = PANEL_MARGIN;
        int y = graphics.guiHeight() - PANEL_HEIGHT - PANEL_MARGIN;

        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, PANEL_COLOR);
        graphics.drawString(minecraft.font, Component.literal("Cargando Assets"), x + 8, y + 7, TEXT_COLOR, false);
        graphics.drawString(minecraft.font, Component.literal(loaded + "/" + total + " Assets cargados"), x + 8, y + 21, TEXT_COLOR, false);

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
