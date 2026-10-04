package mr.lion303.assetsloadermod.mixin;

import mr.lion303.assetsloadermod.ResourceReloadState;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "setScreen", at = @At("TAIL"))
    private void assetsLoaderMod$markGameReady(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen) {
            ResourceReloadState.markGameReady();
        }
    }
}
