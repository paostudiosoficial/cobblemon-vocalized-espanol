package paostudios.vocalized_es.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import paostudios.vocalized_es.Texto;

@Mixin(targets = "com.cobblemon.vocalized.common.voice.processor.text.TextUtils")
public class TextUtilsMixin {
    @ModifyVariable(method = "normalizeForMatch", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String vocalizedEs$normalize(String s) {
        return Texto.limpiar(s);
    }
}
