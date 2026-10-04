package mr.lion303.assetsloadermod.mixin;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public abstract class ForgeLoadingOverlayMixin {
    private static final int ANCHO_PANEL = 176;
    private static final int ALTO_PANEL = 58;
    private static final int MARGEN_PANEL = 8;
    private static final int COLOR_TEXTO = 0xFF202020;
    private static final int COLOR_PANEL = 0x99FFFFFF;
    private static final int COLOR_FONDO_BARRA = 0x55202020;
    private static final int COLOR_RELLENO_BARRA = 0xFF202020;

    @Shadow private long fadeOutStart;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private ReloadInstance reload;
    @Shadow @Final private Consumer<Optional<Throwable>> onFinish;

    @Unique private volatile int assetsTotales = -1;
    @Unique private volatile boolean contandoAssets = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void reemplazarPantallaDeCarga(GuiGraphics graficos, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ci.cancel();

        long ahora = Util.getMillis();

        if (this.fadeOutStart > -1L && (float) (ahora - this.fadeOutStart) / 1000.0F >= 1.0F) {
            this.minecraft.setOverlay(null);
            return;
        }

        if (this.fadeOutStart == -1L && this.reload.isDone()) {
            this.fadeOutStart = ahora;

            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable error) {
                this.onFinish.accept(Optional.of(error));
            }

            Screen pantalla = this.minecraft.screen;
            if (pantalla != null) {
                pantalla.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            }
        }

        float progreso = Math.max(0.0F, Math.min(this.reload.getActualProgress(), 1.0F));

        if (this.assetsTotales < 0 && !this.contandoAssets) {
            this.contandoAssets = true;
            CompletableFuture.supplyAsync(() -> {
                try {
                    return this.minecraft.getResourceManager()
                            .listResources("", ubicacion -> true)
                            .size();
                } catch (RuntimeException error) {
                    return -1;
                }
            }).thenAccept(total -> {
                this.assetsTotales = total;
                this.contandoAssets = false;
            });
        }

        int total = Math.max(0, this.assetsTotales);
        int cargados = this.assetsTotales < 0
                ? 0
                : (this.reload.isDone() ? total : Math.min(total, Math.round(total * progreso)));
        int porcentaje = Math.round(progreso * 100.0F);

        int x = MARGEN_PANEL;
        int y = graficos.guiHeight() - ALTO_PANEL - MARGEN_PANEL;

        graficos.fill(x, y, x + ANCHO_PANEL, y + ALTO_PANEL, COLOR_PANEL);
        graficos.drawString(this.minecraft.font, Component.literal("Cargando Assets"), x + 8, y + 7, COLOR_TEXTO, false);
        String textoActivos = this.assetsTotales < 0
                ? "Contando Assets..."
                : cargados + "/" + total + " Assets cargados";
        graficos.drawString(this.minecraft.font, Component.literal(textoActivos), x + 8, y + 21, COLOR_TEXTO, false);

        int barraX = x + 8;
        int barraY = y + 38;
        int barraAncho = ANCHO_PANEL - 16;
        int barraAlto = 8;

        graficos.fill(barraX, barraY, barraX + barraAncho, barraY + barraAlto, COLOR_FONDO_BARRA);

        int anchoRelleno = Math.round(barraAncho * (porcentaje / 100.0F));
        if (anchoRelleno > 0) {
            graficos.fill(barraX, barraY, barraX + anchoRelleno, barraY + barraAlto, COLOR_RELLENO_BARRA);
        }
    }
}
