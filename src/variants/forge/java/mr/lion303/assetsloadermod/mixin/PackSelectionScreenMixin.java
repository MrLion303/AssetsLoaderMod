package mr.lion303.assetsloadermod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionScreen.class)
public abstract class PackSelectionScreenMixin {
    @Shadow private Minecraft minecraft;

    @Inject(method = "onClose", at = @At("TAIL"))
    private void volverAlMundo(CallbackInfo ci) {
        if (this.minecraft.level != null) {
            this.minecraft.setScreen(null);
        }
    }
}
