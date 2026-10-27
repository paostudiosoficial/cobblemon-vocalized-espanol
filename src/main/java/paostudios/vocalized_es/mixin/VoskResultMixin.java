package paostudios.vocalized_es.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paostudios.vocalized_es.Texto;

@Mixin(targets = "com.cobblemon.vocalized.common.vosk.VoskResult")
public class VoskResultMixin {
    @Inject(method = "toText", at = @At("RETURN"), cancellable = true)
    private void vocalizedEs$toText(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Texto.limpiar(cir.getReturnValue()));
    }
}
