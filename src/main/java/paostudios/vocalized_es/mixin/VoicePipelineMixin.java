package paostudios.vocalized_es.mixin;

import com.cobblemon.vocalized.common.voice.pipeline.VocalizedVoicePipeline;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paostudios.vocalized_es.Debug;
import paostudios.vocalized_es.Plasmo;
import paostudios.vocalized_es.Texto;

@Mixin(value = VocalizedVoicePipeline.class, remap = false)
public class VoicePipelineMixin {
    @Inject(method = "createSubstringMatchPipeline$lambda$3", at = @At("HEAD"))
    private static void vocalizedEs$texto(VocalizedVoicePipeline self, List<?> frases, String texto, CallbackInfoReturnable<String> cir) {
        Texto.ultimo = texto == null ? "" : texto;
        Debug.mostrar(texto);
    }

    @Inject(method = "detectConfiguredMicName", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$microfono(CallbackInfoReturnable<String> cir) {
        if (Plasmo.activo()) cir.setReturnValue(Plasmo.microfono());
    }

    @Inject(method = "createSubstringMatchPipeline$lambda$3", at = @At("RETURN"), cancellable = true)
    private static void vocalizedEs$siempre(VocalizedVoicePipeline self, List<?> frases, String texto, CallbackInfoReturnable<String> cir) {
        if (cir.getReturnValue() == null && texto != null && !texto.isBlank()) cir.setReturnValue(Texto.SIN_MATCH);
    }
}
