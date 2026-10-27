package paostudios.vocalized_es.mixin;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.SingleActionRequest;
import com.cobblemon.vocalized.api.voice.VoicePipeline;
import com.cobblemon.vocalized.common.client.controller.battle.BattleVoiceController;
import com.cobblemon.vocalized.common.voice.pipeline.VocalizedVoicePipeline;
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
import paostudios.vocalized_es.Combate;
import paostudios.vocalized_es.Microfono;
import paostudios.vocalized_es.Texto;

@Mixin(value = BattleVoiceController.class, remap = false)
public class BattleVoiceControllerMixin {
    @Shadow @Final @Mutable
    private List<String> forfeitPhrases;

    @Shadow @Final
    private VocalizedVoicePipeline pipelineFactory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void vocalizedEs$init(CallbackInfo ci) {
        forfeitPhrases = Texto.sumar(forfeitPhrases, Texto.RENDIRSE);
    }

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$activo(CallbackInfoReturnable<Boolean> cir) {
        if (Microfono.actual() == Microfono.NUNCA) cir.setReturnValue(false);
    }

    @Inject(method = "buildPipeline", at = @At("HEAD"), cancellable = true)
    private void vocalizedEs$forzado(CallbackInfoReturnable<VoicePipeline> cir) {
        ClientBattle b = CobblemonClient.INSTANCE.getBattle();
        SingleActionRequest r = b == null ? null : b.getFirstUnansweredRequest();
        if (r == null || r.getMoveSet() != null) return;
        List<String> frases = Combate.frases(true);
        cir.setReturnValue(frases.isEmpty() ? null : pipelineFactory.createSubstringMatchPipeline(frases, m -> Combate.manejar(Texto.ultimo), false));
    }

    @ModifyArg(method = "buildPipeline", at = @At(value = "INVOKE", target = "Lcom/cobblemon/vocalized/common/voice/pipeline/VocalizedVoicePipeline;createSubstringMatchPipeline(Ljava/util/List;Ljava/util/function/Consumer;Z)Lcom/cobblemon/vocalized/api/voice/VoicePipeline;"), index = 0)
    private List<String> vocalizedEs$frases(List<String> frases) {
        return Texto.sumar(frases, Combate.frases(false));
    }

    @Inject(method = "buildPipeline$lambda$1", at = @At("HEAD"), cancellable = true)
    private static void vocalizedEs$match(BattleVoiceController self, List<?> choices, String matched, CallbackInfo ci) {
        if (Combate.manejar(Texto.ultimo)) ci.cancel();
    }
}
