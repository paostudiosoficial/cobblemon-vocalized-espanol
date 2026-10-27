package paostudios.vocalized_es.mixin;

import com.cobblemon.vocalized.common.client.VocalizedCommonClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paostudios.vocalized_es.Plasmo;

@Mixin(value = VocalizedCommonClient.class, remap = false)
public class VocalizedCommonClientMixin {
    @Inject(method = "isSpeaking", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$hablando(CallbackInfoReturnable<Boolean> cir) {
        if (Plasmo.muteado()) cir.setReturnValue(false);
    }
}
