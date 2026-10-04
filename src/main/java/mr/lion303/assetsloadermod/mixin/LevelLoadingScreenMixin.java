package mr.lion303.assetsloadermod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import org.spongepowered.asm.mixin.Final;
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
    private static final int PANEL_COLOR = 0xD9FFFFFF;
    private static final int BAR_BACKGROUND = 0x66202020;
    private static final int BAR_FILL = 0xFF202020;

    @Shadow @Final
    private StoringChunkProgressListener progressListener;

    @Shadow
    private boolean done;

    @Shadow @Final
    protected Minecraft minecraft;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void assetsLoaderMod$render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int diameter = this.progressListener.getDiameter();
        int total = diameter * diameter;
        int progress = Math.max(0, Math.min(this.progressListener.getProgress(), total));

        if (total > 0 && progress >= total) {
            this.done = true;
            this.minecraft.setScreen(null);
            ci.cancel();
            return;
        }

        ci.cancel();

        int x = PANEL_MARGIN;
        int y = graphics.guiHeight() - PANEL_HEIGHT - PANEL_MARGIN;

        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), PANEL_COLOR);
        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, PANEL_COLOR);

        graphics.drawString(this.minecraft.font, Component.literal("Cargando Assets"), x + 8, y + 7, TEXT_COLOR, false);

        int loaded = progress > 0 ? 1 : 0;
        graphics.drawString(this.minecraft.font, Component.literal(loaded + "/1 Assets cargados"), x + 8, y + 21, TEXT_COLOR, false);

        int barX = x + 8;
        int barY = y + 38;
        int barWidth = PANEL_WIDTH - 16;
        int barHeight = 8;

        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, BAR_BACKGROUND);

        float percentage = total > 0 ? (float) progress / (float) total : 0.0F;
        int filledWidth = Math.round(barWidth * percentage);
        if (filledWidth > 0) {
            graphics.fill(barX, barY, barX + filledWidth, barY + barHeight, BAR_FILL);
        }
    }
}
