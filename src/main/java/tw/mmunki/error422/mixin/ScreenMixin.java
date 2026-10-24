package tw.mmunki.error422.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tw.mmunki.error422.client.Error422Overlay;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "extractPanorama", at = @At("HEAD"), cancellable = true)
    private void error422$replaceTitleBackground(GuiGraphicsExtractor graphics, float partialTick,
                                                 CallbackInfo callbackInfo) {
        if (((Object) this) instanceof TitleScreen) {
            Error422Overlay.renderTitleBackground(graphics, partialTick);
            callbackInfo.cancel();
        }
    }
}
