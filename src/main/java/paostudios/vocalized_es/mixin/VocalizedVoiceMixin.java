package paostudios.vocalized_es.mixin;

import com.cobblemon.vocalized.common.client.VocalizedVoice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import paostudios.vocalized_es.Idioma;

@Mixin(value = VocalizedVoice.class, remap = false)
public class VocalizedVoiceMixin {
    @ModifyConstant(method = "loadModelInternal", constant = @Constant(stringValue = "models/en_us"))
    private String vocalizedEs$modelo(String original) {
        return Idioma.modelo(original);
    }
}
