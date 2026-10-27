package paostudios.vocalized_es.mixin;

import com.cobblemon.vocalized.common.client.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import paostudios.vocalized_es.ConfigMic;

@Mixin(value = ClientConfig.class, remap = false)
public class ClientConfigMixin implements ConfigMic {
    private String micListening = "SOLO_COMBATE";

    @Override
    public String vocalizedEs$getMic() {
        return micListening;
    }

    @Override
    public void vocalizedEs$setMic(String valor) {
        micListening = valor;
    }
}
