package paostudios.vocalized_es.mixin;

import com.cobblemon.vocalized.common.client.controller.recall.RecallVoiceController;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paostudios.vocalized_es.Fuera;
import paostudios.vocalized_es.Texto;

@Mixin(value = RecallVoiceController.class, remap = false)
public class RecallVoiceControllerMixin {
    @Shadow @Final @Mutable
    private List<String> phrases;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void vocalizedEs$init(CallbackInfo ci) {
        phrases = Texto.sumar(phrases, Texto.VOLVER);
    }

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$activo(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Fuera.activo());
    }

    @ModifyArg(method = "buildPipeline", at = @At(value = "INVOKE", target = "Lcom/cobblemon/vocalized/common/voice/pipeline/VocalizedVoicePipeline;createSubstringMatchPipeline(Ljava/util/List;Ljava/util/function/Consumer;Z)Lcom/cobblemon/vocalized/api/voice/VoicePipeline;"), index = 0)
    private List<String> vocalizedEs$frases(List<String> frases) {
        return Texto.sumar(frases, Fuera.frases());
    }

    @Inject(method = "buildPipeline$lambda$0", at = @At("HEAD"), cancellable = true)
    private static void vocalizedEs$match(RecallVoiceController self, String matched, CallbackInfo ci) {
        if (Fuera.manejar(Texto.ultimo)) ci.cancel();
    }
}
