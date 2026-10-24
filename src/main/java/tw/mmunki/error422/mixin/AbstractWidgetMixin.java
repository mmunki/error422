package tw.mmunki.error422.mixin;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tw.mmunki.error422.Error422Config;

import java.util.Random;

@Mixin(net.minecraft.client.gui.components.AbstractWidget.class)
public class AbstractWidgetMixin {
    private static final SoundEvent[] ERROR422_SOUNDS = {
            SoundEvents.SCULK_SHRIEKER_SHRIEK,
            SoundEvents.WARDEN_ROAR,
            SoundEvents.ENDERMAN_SCREAM,
            SoundEvents.ANVIL_DESTROY,
            SoundEvents.WITHER_SPAWN,
            SoundEvents.END_PORTAL_SPAWN
    };

    @Inject(method = "playDownSound", at = @At("HEAD"), cancellable = true)
    private void error422$terrifyingClick(SoundManager soundManager, CallbackInfo callbackInfo) {
        if (!Error422Config.get().enabled) {
            return;
        }
        Random random = new Random();
        SoundEvent sound = ERROR422_SOUNDS[random.nextInt(ERROR422_SOUNDS.length)];
        soundManager.play(SimpleSoundInstance.forUI(sound, 0.45F + random.nextFloat() * 0.75F,
                0.35F + random.nextFloat() * 0.4F));
        callbackInfo.cancel();
    }
}
