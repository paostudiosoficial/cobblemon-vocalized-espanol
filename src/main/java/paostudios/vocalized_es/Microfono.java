package paostudios.vocalized_es;

import com.cobblemon.vocalized.common.client.config.ClientConfig;
import com.cobblemon.vocalized.common.config.ConfigLoader;
import net.minecraft.network.chat.Component;

public enum Microfono {
    PULSAR, COMBATE_Y_MUNDO, SOLO_COMBATE, NUNCA;

    public static Microfono actual() {
        try {
            ClientConfig c = ConfigLoader.INSTANCE.getClientConfig();
            if (c.getPushToTalk()) return PULSAR;
            Microfono m = valueOf(((ConfigMic) (Object) c).vocalizedEs$getMic());
            return m == PULSAR ? SOLO_COMBATE : m;
        } catch (Throwable e) {
            return SOLO_COMBATE;
        }
    }

    public static void siguiente() {
        Microfono n = values()[(actual().ordinal() + 1) % values().length];
        try {
            ConfigLoader.INSTANCE.updateClientConfig(c -> {
                c.setPushToTalk(n == PULSAR);
                if (n != PULSAR) ((ConfigMic) (Object) c).vocalizedEs$setMic(n.name());
            });
        } catch (Throwable ignored) {
        }
    }

    public static Component boton() {
        return Component.translatable("button.cobblemon_vocalized.voice_mode",
                Component.translatable("vocalized_es.microfono." + actual().name().toLowerCase()));
    }
}
