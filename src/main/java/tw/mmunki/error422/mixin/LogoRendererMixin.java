package tw.mmunki.error422.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tw.mmunki.error422.client.Error422Overlay;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IF)V",
            at = @At("HEAD"), cancellable = true)
    private void error422$horrorLogo(GuiGraphicsExtractor graphics, int width, float alpha,
                                     CallbackInfo callbackInfo) {
        if (Minecraft.getInstance().gui.screen() instanceof TitleScreen) {
            Error422Overlay.renderHorrorLogo(graphics, width);
            callbackInfo.cancel();
        }
    }
}
