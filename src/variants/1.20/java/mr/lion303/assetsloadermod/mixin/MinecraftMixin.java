package mr.lion303.assetsloadermod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Unique
    private static boolean assetsLoaderMod$hasReachedTitleScreen;

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void assetsLoaderMod$markGameReady(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen) {
            assetsLoaderMod$hasReachedTitleScreen = true;
        }
    }

    @Unique
    public static boolean assetsLoaderMod$isGameReady() {
        return assetsLoaderMod$hasReachedTitleScreen;
    }
}
