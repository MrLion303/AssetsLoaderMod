package mr.lion303.assetsloadermod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin {
    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 58;
    private static final int PANEL_MARGIN = 8;
    private static final int TEXT_COLOR = 0xFF202020;
    private static final int PANEL_COLOR = 0x99FFFFFF;
    private static final int BAR_BACKGROUND = 0x55202020;
    private static final int BAR_FILL = 0xFF202020;

    @Shadow
    private LevelLoadTracker loadTracker;

    @Shadow
    protected Minecraft minecraft;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void assetsLoaderMod$render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ci.cancel();

        ((LevelLoadingScreen) (Object) this).extractBackground(graphics, mouseX, mouseY, partialTick);

        float progress = Math.max(0.0F, Math.min(this.loadTracker.serverProgress(), 1.0F));
        int x = PANEL_MARGIN;
        int y = graphics.guiHeight() - PANEL_HEIGHT - PANEL_MARGIN;

        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, PANEL_COLOR);
        graphics.text(this.minecraft.font, Component.literal("Cargando Assets"), x + 8, y + 7, TEXT_COLOR, false);

        int loaded = progress >= 1.0F ? 1 : 0;
        graphics.text(this.minecraft.font, Component.literal(loaded + "/1 Assets cargados"), x + 8, y + 21, TEXT_COLOR, false);

        int barX = x + 8;
        int barY = y + 38;
        int barWidth = PANEL_WIDTH - 16;
        int barHeight = 8;

        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, BAR_BACKGROUND);
        int filledWidth = Math.round(barWidth * progress);
        if (filledWidth > 0) {
            graphics.fill(barX, barY, barX + filledWidth, barY + barHeight, BAR_FILL);
        }
    }
}
