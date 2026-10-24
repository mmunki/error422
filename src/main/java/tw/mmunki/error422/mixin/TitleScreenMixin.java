package tw.mmunki.error422.mixin;

import tw.mmunki.error422.client.Error422Overlay;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void error422$corruptTitle(GuiGraphicsExtractor graphics, int mouseX, int mouseY,
                                       float partialTick, CallbackInfo callbackInfo) {
        Error422Overlay.renderTitle(graphics);
    }
}
