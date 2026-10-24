package dev.mmunki.error422.mixin;

import dev.mmunki.error422.client.Error422Client;
import dev.mmunki.error422.client.Error422Overlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void error422$renderOverlay(GuiGraphicsExtractor graphics, DeltaTracker delta, CallbackInfo callbackInfo) {
        Error422Overlay.render(graphics, delta.getGameTimeDeltaPartialTick(false));
    }

    @Inject(method = "extractPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void error422$hideHealth(GuiGraphicsExtractor graphics, CallbackInfo callbackInfo) {
        if (Error422Client.shouldCorruptHud()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
    private void error422$hideFood(GuiGraphicsExtractor graphics, Player player, int x, int y, CallbackInfo callbackInfo) {
        if (Error422Client.shouldCorruptHud()) {
            callbackInfo.cancel();
        }
    }
}
