package mr.lion303.assetsloadermod.mixin;

import com.mojang.blaze3d.platform.Window;
import mr.lion303.assetsloadermod.ResourceReloadState;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.loading.ForgeLoadingOverlay;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(value = ForgeLoadingOverlay.class, remap = false)
public abstract class ForgeLoadingOverlayMixin {
    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 58;
    private static final int PANEL_MARGIN = 8;
    private static final int TEXT_COLOR = 0xFF202020;
    private static final int PANEL_COLOR = 0x99FFFFFF;
    private static final int BAR_BACKGROUND = 0x55202020;
    private static final int BAR_FILL = 0xFF202020;

    @Shadow private long fadeOutStart;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private DisplayWindow displayWindow;
    @Shadow @Final private ReloadInstance reload;
    @Shadow @Final private ProgressMeter progress;
    @Shadow @Final private Consumer<Optional<Throwable>> onFinish;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void assetsLoaderMod$replaceForgeLoadingScreen(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ci.cancel();

        long now = Util.getMillis();
        float fadeOutTimer = this.fadeOutStart > -1L
                ? (float) (now - this.fadeOutStart) / 1000.0F
                : -1.0F;

        // Mantener intacta la finalización de la recarga de Forge.
        if (fadeOutTimer >= 2.0F) {
            this.minecraft.setOverlay(null);
            this.displayWindow.close();
            return;
        }

        if (this.fadeOutStart == -1L && this.reload.isDone()) {
            this.progress.complete();
            this.fadeOutStart = now;

            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.onFinish.accept(Optional.of(throwable));
            }

            Screen screen = this.minecraft.screen;
            if (screen != null) {
                screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            }
        }

        if (!ResourceReloadState.isGameReady()) {
            return;
        }

        int progressPercent = Math.max(0, Math.min(Math.round(this.reload.getActualProgress() * 100.0F), 100));
        int x = PANEL_MARGIN;
        int y = graphics.guiHeight() - PANEL_HEIGHT - PANEL_MARGIN;

        // No se dibuja el fondo opaco ni el logo de Mojang: el mundo queda visible detrás.
        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, PANEL_COLOR);
        graphics.drawString(this.minecraft.font, Component.literal("Cargando Assets"), x + 8, y + 7, TEXT_COLOR, false);

        int loaded = progressPercent >= 100 ? 1 : 0;
        graphics.drawString(this.minecraft.font, Component.literal(loaded + "/1 Assets cargados"), x + 8, y + 21, TEXT_COLOR, false);

        int barX = x + 8;
        int barY = y + 38;
        int barWidth = PANEL_WIDTH - 16;
        int barHeight = 8;
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, BAR_BACKGROUND);

        int filledWidth = Math.round(barWidth * (progressPercent / 100.0F));
        if (filledWidth > 0) {
            graphics.fill(barX, barY, barX + filledWidth, barY + barHeight, BAR_FILL);
        }
    }
}